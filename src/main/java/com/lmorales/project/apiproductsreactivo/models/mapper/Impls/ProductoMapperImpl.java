package com.lmorales.project.apiproductsreactivo.models.mapper.Impls;

import org.springframework.stereotype.Component;

import com.lmorales.project.apiproductsreactivo.models.dto.ProductoDTO;
import com.lmorales.project.apiproductsreactivo.models.entity.Producto;
import com.lmorales.project.apiproductsreactivo.models.mapper.ProductoMapper;

@Component
public class ProductoMapperImpl implements ProductoMapper{

    @Override
    public Producto DtoToEntity(ProductoDTO dto) {
     return Producto.builder()
            .id(dto.getId())
            .nombre(dto.getNombre())
            .precio(dto.getPrecio())
            .createAt(dto.getCreateAt())
            .foto(dto.getFoto())
        .build();
    }

    @Override
    public ProductoDTO EntityToDto(Producto producto) {
       return ProductoDTO.builder()
            .id(producto.getId())
            .nombre(producto.getNombre())
            .precio(producto.getPrecio())
            .createAt(producto.getCreateAt())
            .foto(producto.getFoto())
        .build();
    }
    
}
