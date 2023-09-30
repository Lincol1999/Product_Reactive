package com.lmorales.project.apiproductsreactivo.service;

import com.lmorales.project.apiproductsreactivo.models.dto.ProductoDTO;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoService {

     Flux<ProductoDTO> findAll(); 

     Mono<ProductoDTO> findById(String id);

     Mono<ProductoDTO> save(ProductoDTO productoDTO);

     Mono<Void> detele(ProductoDTO productoDTO);
    
}
