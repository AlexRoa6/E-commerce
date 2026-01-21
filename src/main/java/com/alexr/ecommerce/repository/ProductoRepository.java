package com.alexr.ecommerce.repository;

import com.alexr.ecommerce.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    long countByCategoria_Id(Long id);
}
