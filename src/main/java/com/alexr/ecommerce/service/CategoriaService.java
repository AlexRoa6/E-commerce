package com.alexr.ecommerce.service;

import com.alexr.ecommerce.dto.CategoriaRequestDTO;
import com.alexr.ecommerce.dto.CategoriaResponseDTO;
import com.alexr.ecommerce.exception.CategoriaConProductosException;
import com.alexr.ecommerce.exception.CategoriaNotFoundException;
import com.alexr.ecommerce.mappers.CategoriaMapper;
import com.alexr.ecommerce.model.Categoria;
import com.alexr.ecommerce.repository.CategoriaRepository;
import com.alexr.ecommerce.repository.ProductoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CategoriaService {

    private final CategoriaRepository repoC;
    private final ProductoRepository repoP;

    public CategoriaService(CategoriaRepository repoC, ProductoRepository repoP) {
        this.repoC = repoC;
        this.repoP = repoP;
    }

    public Page<CategoriaResponseDTO> findAll(Pageable pageable) {
        Page<Categoria> categorias = repoC.findAll(pageable);
        return categorias.map(c -> CategoriaMapper.toDTO(c));
    }

    public CategoriaResponseDTO findById(Long id) {
        return CategoriaMapper.toDTO(repoC.findById(id)
               .orElseThrow(() -> new CategoriaNotFoundException("Error: La categoria con ID:" + id + " no existe."))
            );

    }

    public CategoriaResponseDTO save(CategoriaRequestDTO categoria) {
        Categoria c = CategoriaMapper.toEntity(categoria);
        return CategoriaMapper.toDTO(repoC.save(c));
    }

    public void deleteById(Long id) {
        if (!repoC.existsById(id)) throw new CategoriaNotFoundException("Categoria con ID: " + id + " no encontrada.");
        if (repoP.countByCategoria_Id(id) > 0)
            throw new CategoriaConProductosException("Error: La  categoria con ID: " + id + " contiene productos.");
        repoC.deleteById(id);
    }

    public CategoriaResponseDTO update(Long id, CategoriaRequestDTO categoriaActualizada) {

        return repoC.findById(id).map(c -> {
            c.setNombre(categoriaActualizada.getNombre());
            return CategoriaMapper.toDTO(repoC.save(c));
        }).orElseThrow(() -> new CategoriaNotFoundException("Categoria con ID: " + id + " no encontrada."));
    }
}
