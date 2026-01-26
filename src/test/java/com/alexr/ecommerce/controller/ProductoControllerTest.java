package com.alexr.ecommerce.controller;

import com.alexr.ecommerce.dto.CategoriaResponseDTO;
import com.alexr.ecommerce.dto.ErrorResponse;
import com.alexr.ecommerce.dto.ProductoRequestDTO;
import com.alexr.ecommerce.dto.ProductoResponseDTO;
import com.alexr.ecommerce.exception.ProductoNotFoundException;
import com.alexr.ecommerce.model.Categoria;
import com.alexr.ecommerce.model.Producto;
import com.alexr.ecommerce.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductoService service;

    @Test
    void findAllSinProductos_debeDevolerListaVacia() throws Exception {
        Page<ProductoResponseDTO> page = new PageImpl<>(List.of());
        when(service.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get(("/api/productos")))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void findAllConProductos_debeDevolverLista() throws Exception {
        CategoriaResponseDTO c = new CategoriaResponseDTO(1L, "Portatiles");
        ProductoResponseDTO p = new ProductoResponseDTO(1L, "MacBook", 0, BigDecimal.valueOf(1234.54), "Portatil de Apple",true, c);
        ProductoResponseDTO p2 = new ProductoResponseDTO(2L, "Iphone", 0, BigDecimal.valueOf(1234.54), "Portatil de Apple",true, c);
        Page<ProductoResponseDTO> page = new PageImpl<>(List.of(p, p2));
        when(service.findAll(any(Pageable.class))).thenReturn(page);
        mockMvc.perform(get(("/api/productos")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void findByIdProductoExiste_debeDevolverOkYElProducto() throws Exception {
        CategoriaResponseDTO c = new CategoriaResponseDTO(1L, "Portatiles");
        ProductoResponseDTO p = new ProductoResponseDTO(1L, "MacBook", 0, BigDecimal.valueOf(1234.54), "Portatil de Apple",true, c);

        when(service.findById(1L)).thenReturn(p);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(p)));
    }

    @Test
    void findByIdProductoNoExiste_debeLanzarExcepcionProductoNotFound() throws Exception {

        when(service.findById(1L)).thenThrow(new ProductoNotFoundException("Producto con ID:1 no encontrado."));

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Producto con ID:1 no encontrado."))
                .andExpect(jsonPath("$.error").value("Producto no encontrado"));
    }

    @Test
    void saveProductoValido_debeDevolverElProducto() throws Exception {

        ProductoRequestDTO p = new ProductoRequestDTO("MacBook", 0, BigDecimal.valueOf(1234.54), "Portatil de Apple", 1L);

        CategoriaResponseDTO c = new CategoriaResponseDTO(1L, "Portatiles");
        ProductoResponseDTO p2 = new ProductoResponseDTO(1L, "MacBook", 0, BigDecimal.valueOf(1234.54), "Portatil de Apple",true, c);

        when(service.save(any(ProductoRequestDTO.class))).thenReturn(p2);

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(p2)));
    }

    @Test
    void saveProductoConAtributosNoValidos_debeLanzarMethodArgumentNotValidException() throws Exception {
        ProductoRequestDTO p = new ProductoRequestDTO(null, 0, BigDecimal.valueOf(1234.54), "Portatil de Apple", 1L);

        mockMvc.perform(post("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("El nombre es obligatorio"))
                .andExpect(jsonPath("$.error").value("Error de validación"));
    }

    @Test
    void deleteProductoValido_debeDevolverStatusNoContent() throws Exception {

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProductoNoExiste_debeLanzarExcepcionProductoNotFound() throws Exception {

        doThrow(new ProductoNotFoundException("Producto con ID:1 no encontrado.")).when(service).delete(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Producto con ID:1 no encontrado."))
                .andExpect(jsonPath("$.error").value("Producto no encontrado"));
    }

    @Test
    void updateProductoValido_debeDevolverElProducto() throws Exception {

        ProductoRequestDTO p = new ProductoRequestDTO("MacBook", 0, BigDecimal.valueOf(1234.54), "Portatil de Apple", 1L);

        CategoriaResponseDTO c = new CategoriaResponseDTO(1L, "Portatiles");
        ProductoResponseDTO p2 = new ProductoResponseDTO(1L, "MacBook", 0, BigDecimal.valueOf(1234.54), "Portatil de Apple",true, c);

        when(service.update(eq(1L), any(ProductoRequestDTO.class))).thenReturn(p2);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(p2)));
    }

    @Test
    void updateProductoConAtributosNoValidos_debeLanzarMethodArgumentNotValidException() throws Exception {
        ProductoRequestDTO p = new ProductoRequestDTO(null, 0, BigDecimal.valueOf(1234.54), "Portatil de Apple", 1L);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("El nombre es obligatorio"))
                .andExpect(jsonPath("$.error").value("Error de validación"));
    }

    @Test
    void updateProductoNoExiste_debeLanzarExcepcionProductoNotFound() throws Exception {
        ProductoRequestDTO p = new ProductoRequestDTO("MacBook", 0, BigDecimal.valueOf(1234.54), "Portatil de Apple", 1L);
        when(service.update(eq(1L), any(ProductoRequestDTO.class))).thenThrow(new ProductoNotFoundException("Producto con ID:1 no encontrado."));

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Producto con ID:1 no encontrado."))
                .andExpect(jsonPath("$.error").value("Producto no encontrado"));
    }

}
