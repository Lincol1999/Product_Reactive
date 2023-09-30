package com.lmorales.project.apiproductsreactivo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.lmorales.project.apiproductsreactivo.models.entity.Producto;

public interface ProductoRepository extends ReactiveMongoRepository<Producto, String>{
    
}
