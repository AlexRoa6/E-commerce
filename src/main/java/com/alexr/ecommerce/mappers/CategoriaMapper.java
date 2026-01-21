package com.alexr.ecommerce.mappers;

import com.alexr.ecommerce.dto.CategoriaRequestDTO;
import com.alexr.ecommerce.dto.CategoriaResponseDTO;
import com.alexr.ecommerce.model.Categoria;

public class CategoriaMapper {
    public static CategoriaResponseDTO toDTO(Categoria c){
        return new CategoriaResponseDTO(
                c.getId(),
                c.getNombre()
        );
    }

    public static Categoria toEntity(CategoriaRequestDTO c){
        return new Categoria(
                null,
                c.getNombre()
        );
    }
}
