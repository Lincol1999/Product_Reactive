package com.lmorales.project.apiproductsreactivo.service.Impls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lmorales.project.apiproductsreactivo.models.dto.ProductoDTO;
import com.lmorales.project.apiproductsreactivo.models.entity.Producto;
import com.lmorales.project.apiproductsreactivo.models.mapper.ProductoMapper;
import com.lmorales.project.apiproductsreactivo.repository.ProductoRepository;
import com.lmorales.project.apiproductsreactivo.service.ProductoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductoServiceImpl implements ProductoService{

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProductoMapper mapper;

    @Override
    public Flux<ProductoDTO> findAll() {
        return productoRepository.findAll().map(mapper::EntityToDto);
    }

    @Override
    public Mono<ProductoDTO> findById(String id) {
        return productoRepository.findById(id).map(mapper::EntityToDto);
    }

    @Override
    public Mono<ProductoDTO> save(ProductoDTO productoDTO) {
        Producto prod = mapper.DtoToEntity(productoDTO);
        return productoRepository.save(prod).map(mapper::EntityToDto);
    }

    @Override
    public Mono<Void> detele(ProductoDTO productoDTO) {
        Producto prod = mapper.DtoToEntity(productoDTO);
        return productoRepository.delete(prod);
    }

   
    
}
