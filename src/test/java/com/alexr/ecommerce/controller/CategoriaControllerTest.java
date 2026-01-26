package com.alexr.ecommerce.controller;

import com.alexr.ecommerce.dto.CategoriaRequestDTO;
import com.alexr.ecommerce.dto.CategoriaResponseDTO;
import com.alexr.ecommerce.exception.CategoriaConProductosException;
import com.alexr.ecommerce.exception.CategoriaNotFoundException;
import com.alexr.ecommerce.service.CategoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoriaService service;

    @Test
    void findAllSinCategorias_debeDevolverListaVacia() throws Exception {
        Page<CategoriaResponseDTO> page = new PageImpl<>(List.of());
        when(service.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));

    }

    @Test
    void findAllConCategorias_debeDevolverLista() throws Exception {
        CategoriaResponseDTO c1 = new CategoriaResponseDTO(1L, "Portatiles");
        CategoriaResponseDTO c2 = new CategoriaResponseDTO(2L, "Moviles");
        Page<CategoriaResponseDTO> page = new PageImpl<>(List.of(c1, c2));
        when(service.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void findByIdCategoriaExiste_debeDevolverOkYLaCategoria() throws Exception {
        CategoriaResponseDTO c = new CategoriaResponseDTO(1L, "Portatiles");

        when(service.findById(1L)).thenReturn(c);

        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(c)));
    }

    @Test
    void findByIdCategoriaNoExiste_debeLanzarExcepcionCategoriaNotFound() throws Exception {

        when(service.findById(1L)).thenThrow(new CategoriaNotFoundException("Categoria con ID:1 no encontrada."));

        mockMvc.perform(get("/api/categorias/1"))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Categoria con ID:1 no encontrada."))
                .andExpect(jsonPath("$.error").value("Categoria no encontrada"));
    }

    @Test
    void saveCategoriaValida_debeDevolverLaCategoria() throws Exception {

        CategoriaRequestDTO c = new CategoriaRequestDTO("Portatiles");
        CategoriaResponseDTO c2 = new CategoriaResponseDTO(1L, "Portatiles");

        when(service.save(any(CategoriaRequestDTO.class))).thenReturn(c2);

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(c2)));
    }

    @Test
    void saveCategoriaConAtributosNoValidos_debeLanzarMethodArgumentNotValidException() throws Exception {
        CategoriaRequestDTO c = new CategoriaRequestDTO(null);

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("El nombre es obligatorio"))
                .andExpect(jsonPath("$.error").value("Error de validación"));
    }

    @Test
    void saveCategoriaConNombreCorto_debeLanzarMethodArgumentNotValidException() throws Exception {
        CategoriaRequestDTO c = new CategoriaRequestDTO("AB");

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("El nombre debe tener entre 3 y 200 caracteres"))
                .andExpect(jsonPath("$.error").value("Error de validación"));
    }

    @Test
    void deleteCategoriaValida_debeDevolverStatusNoContent() throws Exception {

        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCategoriaNoExiste_debeLanzarExcepcionCategoriaNotFound() throws Exception {

        doThrow(new CategoriaNotFoundException("Categoria con ID:1 no encontrada.")).when(service).deleteById(1L);

        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Categoria con ID:1 no encontrada."))
                .andExpect(jsonPath("$.error").value("Categoria no encontrada"));
    }

    @Test
    void deleteCategoriaConProductos_debeLanzarExcepcionCategoriaConProductos() throws Exception {

        doThrow(new CategoriaConProductosException("No se puede eliminar la categoria porque tiene productos asociados."))
                .when(service).deleteById(1L);

        mockMvc.perform(delete("/api/categorias/1"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("No se puede eliminar la categoria porque tiene productos asociados."))
                .andExpect(jsonPath("$.error").value("Operacion no permitida"));
    }

    @Test
    void updateCategoriaValida_debeDevolverLaCategoria() throws Exception {

        CategoriaRequestDTO c = new CategoriaRequestDTO("Portatiles Actualizados");
        CategoriaResponseDTO c2 = new CategoriaResponseDTO(1L, "Portatiles Actualizados");

        when(service.update(eq(1L), any(CategoriaRequestDTO.class))).thenReturn(c2);

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(c2)));
    }

    @Test
    void updateCategoriaConAtributosNoValidos_debeLanzarMethodArgumentNotValidException() throws Exception {
        CategoriaRequestDTO c = new CategoriaRequestDTO(null);

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("El nombre es obligatorio"))
                .andExpect(jsonPath("$.error").value("Error de validación"));
    }

    @Test
    void updateCategoriaNoExiste_debeLanzarExcepcionCategoriaNotFound() throws Exception {
        CategoriaRequestDTO c = new CategoriaRequestDTO("Portatiles");
        when(service.update(eq(1L), any(CategoriaRequestDTO.class)))
                .thenThrow(new CategoriaNotFoundException("Categoria con ID:1 no encontrada."));

        mockMvc.perform(put("/api/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(c)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Categoria con ID:1 no encontrada."))
                .andExpect(jsonPath("$.error").value("Categoria no encontrada"));
    }

}
