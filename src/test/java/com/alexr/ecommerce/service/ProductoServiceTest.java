package com.alexr.ecommerce.service;

import com.alexr.ecommerce.dto.ProductoRequestDTO;
import com.alexr.ecommerce.dto.ProductoResponseDTO;
import com.alexr.ecommerce.exception.CategoriaNotFoundException;
import com.alexr.ecommerce.exception.ProductoNotFoundException;
import com.alexr.ecommerce.model.Categoria;
import com.alexr.ecommerce.model.Producto;
import com.alexr.ecommerce.repository.CategoriaRepository;
import com.alexr.ecommerce.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {
    @Mock
    ProductoRepository productoRepository;

    @Mock
    CategoriaRepository categoriaRepository;

    @InjectMocks
    ProductoService productoService;

    @Test
    void cuandoCreoProductoConStock_debeGuardarYMarcarComoDisponible() {

        ProductoRequestDTO productoRequestDTO = new ProductoRequestDTO("MacBook", 10, BigDecimal.valueOf(1234.54), "Portatil de Apple", 1L);
        Categoria categoria = new Categoria("Portatiles");
        categoria.setId(1L);

        Producto productoGuardado = new Producto("MacBook", 10, BigDecimal.valueOf(1234.54), "Portatil de Apple", categoria);
        productoGuardado.setId(1L);
        productoGuardado.setDisponible(true);


        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);


        ProductoResponseDTO resultado = productoService.save(productoRequestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("MacBook", resultado.getNombre());
        assertEquals("Portatil de Apple", resultado.getDescripcion());
        assertEquals(BigDecimal.valueOf(1234.54), resultado.getPrecio());
        assertEquals(10, resultado.getStock());
        assertTrue(resultado.isDisponible());
        assertEquals("Portatiles", resultado.getCategoria().getNombre());
    }

    @Test
    void cunadoCreoProductoConCategoriaNoValida_debeLanzarCategoriaNotFoundException() {

        ProductoRequestDTO productoRequestDTO = new ProductoRequestDTO("MacBook", 10, BigDecimal.valueOf(1234.54), "Portatil de Apple", 99L);

        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoriaNotFoundException.class, () -> productoService.save(productoRequestDTO));

    }

    @Test
    void cuandoCreoProductoSinStock_debeGuardarYMarcarComoNoDisponible(){

        ProductoRequestDTO productoRequestDTO = new ProductoRequestDTO();
        productoRequestDTO.setIdCategoria(1L);
        productoRequestDTO.setNombre("Iphone");
        productoRequestDTO.setPrecio(BigDecimal.valueOf(1234));
        productoRequestDTO.setStock(0);
        productoRequestDTO.setDescripcion("Movil de Apple");

        Categoria c = new Categoria("Movil");
        c.setId(1L);
        when(categoriaRepository.findById(c.getId())).thenReturn(Optional.of(c));

        Producto productoGuardado = new Producto();
        productoGuardado.setCategoria(c);
        productoGuardado.setNombre("Iphone");
        productoGuardado.setPrecio(BigDecimal.valueOf(1234));
        productoGuardado.setStock(0);
        productoGuardado.setDescripcion("Movil de Apple");
        productoGuardado.setId(1L);
        productoGuardado.setDisponible(false);

        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);
        ProductoResponseDTO resultado = productoService.save(productoRequestDTO);

        assertFalse(resultado.isDisponible());
    }

    @Test
    void cuandoNoHayProductos_debeDevolverListaVacia(){
        when(productoRepository.findAll()).thenReturn(List.of());

        List<ProductoResponseDTO> productos = productoService.findAll();

        assertTrue(productos.isEmpty());
    }

    @Test
    void cuandoHayProductos_debeDevolerUnaListaConEllos(){

        Categoria categoria = new Categoria("Portatiles");
        categoria.setId(1L);

        Producto p = new Producto("MacBook", 10, BigDecimal.valueOf(1234.54), "Portatil de Apple", categoria);
        Producto p2 = new Producto("Iphone", 0, BigDecimal.valueOf(3123), "Movil de Apple", categoria);
        when(productoRepository.findAll()).thenReturn(List.of(p,p2));

        List<ProductoResponseDTO> productos = productoService.findAll();

        assertEquals(2, productos.size());
    }

    @Test
    void cuandoSeBusquePorIdUnProductoYExiste_debeDevolverElProducto(){
        Categoria categoria = new Categoria("Portatiles");
        categoria.setId(1L);
        Producto p = new Producto("MacBook", 10, BigDecimal.valueOf(1234.54), "Portatil de Apple", categoria);
        p.setId(1L);
        p.setDisponible(true);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(p));

        ProductoResponseDTO resultado = productoService.findById(1L);

        assertNotNull(resultado);
    }

    @Test
    void cuandoSeBusquePorIdUnProductoYNoExiste_debeLanzarExcepcionProductoNotFound(){

        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductoNotFoundException.class, () -> productoService.findById(1L));
    }

    @Test
    void cunadoSeEliminaUnProductoExistente_noDebeDevolverNada(){

        when(productoRepository.existsById(1L)).thenReturn(true);
        productoService.delete(1L);
        verify(productoRepository).deleteById(1L);
    }

    @Test
    void cuandoSeEliminaUnProductoYNoExiste_debeLanzarExcepcionProductoNotFound(){
        when(productoRepository.existsById(1L)).thenReturn(false);
        assertThrows(ProductoNotFoundException.class, () -> productoService.delete(1L));
    }

    @Test
    void cuandoSeActualizaUnProductoExistente_debeActualizarYDevolverProducto() {
        Categoria categoria = new Categoria("Portatiles");
        categoria.setId(1L);

        Producto productoExistente = new Producto("MacBook", 10, BigDecimal.valueOf(1234.54), "Portatil de Apple", categoria);
        productoExistente.setId(1L);
        productoExistente.setDisponible(true);

        ProductoRequestDTO productoActualizado = new ProductoRequestDTO("MacBook Pro", 15, BigDecimal.valueOf(1999.99), "Portatil de Apple actualizado", 1L);

        Producto productoGuardado = new Producto("MacBook Pro", 15, BigDecimal.valueOf(1999.99), "Portatil de Apple actualizado", categoria);
        productoGuardado.setId(1L);
        productoGuardado.setDisponible(true);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoExistente));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

        ProductoResponseDTO resultado = productoService.update(1L, productoActualizado);

        assertNotNull(resultado);
        assertEquals("MacBook Pro", resultado.getNombre());
        assertEquals("Portatil de Apple actualizado", resultado.getDescripcion());
        assertEquals(BigDecimal.valueOf(1999.99), resultado.getPrecio());
        assertEquals(15, resultado.getStock());
        assertTrue(resultado.isDisponible());
    }

    @Test
    void cuandoSeActualizaUnProductoConCategoriaNoValida_debeLanzarCategoriaNotFoundException() {
        ProductoRequestDTO productoActualizado = new ProductoRequestDTO("MacBook Pro", 15, BigDecimal.valueOf(1999.99), "Portatil actualizado", 99L);

        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoriaNotFoundException.class, () -> productoService.update(1L, productoActualizado));
    }

    @Test
    void cuandoSeActualizaUnProductoQueNoExiste_debeLanzarProductoNotFoundException() {
        Categoria categoria = new Categoria("Portatiles");
        categoria.setId(1L);

        ProductoRequestDTO productoActualizado = new ProductoRequestDTO("MacBook Pro", 15, BigDecimal.valueOf(1999.99), "Portatil actualizado", 1L);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductoNotFoundException.class, () -> productoService.update(99L, productoActualizado));
    }

    @Test
    void cuandoSeActualizaProductoConStock0_debeMarcarComoNoDisponible() {
        Categoria categoria = new Categoria("Portatiles");
        categoria.setId(1L);

        Producto productoExistente = new Producto("MacBook", 10, BigDecimal.valueOf(1234.54), "Portatil de Apple", categoria);
        productoExistente.setId(1L);
        productoExistente.setDisponible(true);

        ProductoRequestDTO productoActualizado = new ProductoRequestDTO("MacBook", 0, BigDecimal.valueOf(1234.54), "Portatil de Apple", 1L);

        Producto productoGuardado = new Producto("MacBook", 0, BigDecimal.valueOf(1234.54), "Portatil de Apple", categoria);
        productoGuardado.setId(1L);
        productoGuardado.setDisponible(false);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoExistente));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

        ProductoResponseDTO resultado = productoService.update(1L, productoActualizado);

        assertFalse(resultado.isDisponible());
    }

    @Test
    void cuandoSeActualizaProductoSinStockAgregandoStock_debeMarcarComoDisponible() {
        Categoria categoria = new Categoria("Portatiles");
        categoria.setId(1L);

        Producto productoExistente = new Producto("MacBook", 0, BigDecimal.valueOf(1234.54), "Portatil de Apple", categoria);
        productoExistente.setId(1L);
        productoExistente.setDisponible(false);

        ProductoRequestDTO productoActualizado = new ProductoRequestDTO("MacBook", 5, BigDecimal.valueOf(1234.54), "Portatil de Apple", 1L);

        Producto productoGuardado = new Producto("MacBook", 5, BigDecimal.valueOf(1234.54), "Portatil de Apple", categoria);
        productoGuardado.setId(1L);
        productoGuardado.setDisponible(true);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoExistente));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

        ProductoResponseDTO resultado = productoService.update(1L, productoActualizado);

        assertTrue(resultado.isDisponible());
        assertEquals(5, resultado.getStock());
    }
}
