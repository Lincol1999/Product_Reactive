package com.lmorales.project.apiproductsreactivo.models.mapper;


import com.lmorales.project.apiproductsreactivo.models.dto.ProductoDTO;
import com.lmorales.project.apiproductsreactivo.models.entity.Producto;

public interface ProductoMapper {
    
    // DTO -> Entity
    Producto DtoToEntity(ProductoDTO dto);

    //Entity -> DTO
    ProductoDTO EntityToDto(Producto producto);

}
