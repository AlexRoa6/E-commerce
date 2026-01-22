package com.alexr.ecommerce.service;

import com.alexr.ecommerce.dto.CategoriaRequestDTO;
import com.alexr.ecommerce.dto.CategoriaResponseDTO;
import com.alexr.ecommerce.exception.CategoriaConProductosException;
import com.alexr.ecommerce.exception.CategoriaNotFoundException;
import com.alexr.ecommerce.model.Categoria;
import com.alexr.ecommerce.repository.CategoriaRepository;
import com.alexr.ecommerce.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;
    private CategoriaRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        categoria = new Categoria("Electrónica");
        categoria.setId(1L);
        requestDTO = new CategoriaRequestDTO("Electrónica");
    }

    @Nested
    @DisplayName("Tests para findAll()")
    class FindAllTests {

        @Test
        @DisplayName("Debe retornar lista de categorías cuando existen")
        void findAll_ConCategorias_RetornaLista() {
            Categoria categoria2 = new Categoria("Ropa");
            categoria2.setId(2L);
            when(categoriaRepository.findAll()).thenReturn(List.of(categoria, categoria2));

            List<CategoriaResponseDTO> resultado = categoriaService.findAll();

            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            assertEquals("Electrónica", resultado.get(0).getNombre());
            assertEquals("Ropa", resultado.get(1).getNombre());
            verify(categoriaRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay categorías")
        void findAll_SinCategorias_RetornaListaVacia() {
            when(categoriaRepository.findAll()).thenReturn(Collections.emptyList());

            List<CategoriaResponseDTO> resultado = categoriaService.findAll();

            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            verify(categoriaRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Tests para findById()")
    class FindByIdTests {

        @Test
        @DisplayName("Debe retornar categoría cuando existe")
        void findById_CategoriaExiste_RetornaCategoria() {
            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

            CategoriaResponseDTO resultado = categoriaService.findById(1L);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals("Electrónica", resultado.getNombre());
            verify(categoriaRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando categoría no existe")
        void findById_CategoriaNoExiste_LanzaExcepcion() {
            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            CategoriaNotFoundException exception = assertThrows(
                CategoriaNotFoundException.class,
                () -> categoriaService.findById(99L)
            );

            assertTrue(exception.getMessage().contains("99"));
            verify(categoriaRepository, times(1)).findById(99L);
        }
    }

    @Nested
    @DisplayName("Tests para save()")
    class SaveTests {

        @Test
        @DisplayName("Debe guardar y retornar la categoría creada")
        void save_CategoriaValida_RetornaCategoriaGuardada() {
            when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

            CategoriaResponseDTO resultado = categoriaService.save(requestDTO);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals("Electrónica", resultado.getNombre());
            verify(categoriaRepository, times(1)).save(any(Categoria.class));
        }
    }

    @Nested
    @DisplayName("Tests para deleteById()")
    class DeleteByIdTests {

        @Test
        @DisplayName("Debe eliminar categoría cuando existe y no tiene productos")
        void deleteById_CategoriaExisteSinProductos_EliminaCorrectamente() {
            when(categoriaRepository.existsById(1L)).thenReturn(true);
            when(productoRepository.countByCategoria_Id(1L)).thenReturn(0L);
            doNothing().when(categoriaRepository).deleteById(1L);

            assertDoesNotThrow(() -> categoriaService.deleteById(1L));

            verify(categoriaRepository, times(1)).existsById(1L);
            verify(productoRepository, times(1)).countByCategoria_Id(1L);
            verify(categoriaRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando categoría no existe")
        void deleteById_CategoriaNoExiste_LanzaExcepcion() {
            when(categoriaRepository.existsById(99L)).thenReturn(false);

            CategoriaNotFoundException exception = assertThrows(
                CategoriaNotFoundException.class,
                () -> categoriaService.deleteById(99L)
            );

            assertTrue(exception.getMessage().contains("99"));
            verify(categoriaRepository, times(1)).existsById(99L);
            verify(categoriaRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando categoría tiene productos asociados")
        void deleteById_CategoriaConProductos_LanzaExcepcion() {
            when(categoriaRepository.existsById(1L)).thenReturn(true);
            when(productoRepository.countByCategoria_Id(1L)).thenReturn(5L);

            CategoriaConProductosException exception = assertThrows(
                CategoriaConProductosException.class,
                () -> categoriaService.deleteById(1L)
            );

            assertTrue(exception.getMessage().contains("1"));
            verify(categoriaRepository, times(1)).existsById(1L);
            verify(productoRepository, times(1)).countByCategoria_Id(1L);
            verify(categoriaRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("Tests para update()")
    class UpdateTests {

        @Test
        @DisplayName("Debe actualizar y retornar la categoría modificada")
        void update_CategoriaExiste_RetornaCategoriaActualizada() {
            CategoriaRequestDTO updateDTO = new CategoriaRequestDTO("Electrónica Actualizada");
            Categoria categoriaActualizada = new Categoria("Electrónica Actualizada");
            categoriaActualizada.setId(1L);

            when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
            when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaActualizada);

            CategoriaResponseDTO resultado = categoriaService.update(1L, updateDTO);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals("Electrónica Actualizada", resultado.getNombre());
            verify(categoriaRepository, times(1)).findById(1L);
            verify(categoriaRepository, times(1)).save(any(Categoria.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando categoría a actualizar no existe")
        void update_CategoriaNoExiste_LanzaExcepcion() {
            CategoriaRequestDTO updateDTO = new CategoriaRequestDTO("Nueva Categoría");
            when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

            CategoriaNotFoundException exception = assertThrows(
                CategoriaNotFoundException.class,
                () -> categoriaService.update(99L, updateDTO)
            );

            assertTrue(exception.getMessage().contains("99"));
            verify(categoriaRepository, times(1)).findById(99L);
            verify(categoriaRepository, never()).save(any());
        }
    }
}
