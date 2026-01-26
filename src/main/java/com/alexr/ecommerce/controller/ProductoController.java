package com.alexr.ecommerce.controller;

import com.alexr.ecommerce.dto.ProductoRequestDTO;
import com.alexr.ecommerce.dto.ProductoResponseDTO;
import com.alexr.ecommerce.model.Producto;
import com.alexr.ecommerce.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<ProductoResponseDTO>> findAll(Pageable pageable){
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> save(@Valid @RequestBody ProductoRequestDTO producto){
        ProductoResponseDTO p = service.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(p);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> update(@PathVariable Long id,@Valid @RequestBody ProductoRequestDTO productoActualizado) {
        return ResponseEntity.ok(service.update(id, productoActualizado));
    }
}
