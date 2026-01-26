package com.alexr.ecommerce.service;

import com.alexr.ecommerce.dto.ProductoRequestDTO;
import com.alexr.ecommerce.dto.ProductoResponseDTO;
import com.alexr.ecommerce.exception.CategoriaNotFoundException;
import com.alexr.ecommerce.exception.ProductoNotFoundException;
import com.alexr.ecommerce.mappers.ProductoMapper;
import com.alexr.ecommerce.model.Categoria;
import com.alexr.ecommerce.model.Producto;
import com.alexr.ecommerce.repository.CategoriaRepository;
import com.alexr.ecommerce.repository.ProductoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {
    private final ProductoRepository repoP;
    private final CategoriaRepository repoC;

    public ProductoService(ProductoRepository repoP, CategoriaRepository repoC) {
        this.repoP = repoP;
        this.repoC = repoC;
    }

    public Page<ProductoResponseDTO> findAll(Pageable pageable) {
        Page<Producto> productos = repoP.findAll(pageable);
        return productos.map(p -> ProductoMapper.toDTO(p));
    }

    public ProductoResponseDTO findById(Long id) {
        return ProductoMapper.toDTO(repoP.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException("Producto con ID:" + id + " no encontrado.")));
    }

    public ProductoResponseDTO update(Long id, ProductoRequestDTO productoActualizado) {

        Categoria c = repoC.findById(productoActualizado.getIdCategoria())
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria: " + productoActualizado.getIdCategoria() + " no existe."));

        return repoP.findById(id).map(p -> {
            p.setNombre(productoActualizado.getNombre());
            p.setCategoria(c);
            p.setDescripcion(productoActualizado.getDescripcion());
            p.setDisponible(productoActualizado.getStock() > 0);
            p.setPrecio(productoActualizado.getPrecio());
            p.setStock(productoActualizado.getStock());
            return ProductoMapper.toDTO(repoP.save(p));
        }).orElseThrow(() -> new ProductoNotFoundException("Producto con ID: " + id + " no encontrado."));
    }

    public void delete(Long id) {
        if (!repoP.existsById(id)) throw new ProductoNotFoundException("Producto con ID: " + id + " no encontrado.");
        repoP.deleteById(id);
    }

    public ProductoResponseDTO save(ProductoRequestDTO producto) {
        Categoria c = repoC.findById(producto.getIdCategoria())
                .orElseThrow(() -> new CategoriaNotFoundException("Categoria: " + producto.getIdCategoria() + " no existe."));

        Producto p = ProductoMapper.toEntity(producto, c);
        p.setDisponible(p.getStock() > 0);
        return ProductoMapper.toDTO(repoP.save(p));
    }
}
