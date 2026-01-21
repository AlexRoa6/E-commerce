package com.alexr.ecommerce.mappers;

import com.alexr.ecommerce.dto.ProductoRequestDTO;
import com.alexr.ecommerce.dto.ProductoResponseDTO;
import com.alexr.ecommerce.model.Categoria;
import com.alexr.ecommerce.model.Producto;

public class ProductoMapper {

    public static ProductoResponseDTO toDTO(Producto p){

        return new ProductoResponseDTO(
            p.getId(),
            p.getNombre(),
            p.getStock(),
            p.getPrecio(),
            p.getDescripcion(),
            p.isDisponible(),
            CategoriaMapper.toDTO(p.getCategoria())
        );
    }

    public static Producto toEntity(ProductoRequestDTO p, Categoria c){
        return new Producto(
                p.getNombre(),
                p.getStock(),
                p.getPrecio(),
                p.getDescripcion(),
                c
        );
    }

}
