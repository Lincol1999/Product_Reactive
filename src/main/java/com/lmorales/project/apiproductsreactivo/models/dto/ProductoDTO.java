package com.lmorales.project.apiproductsreactivo.models.dto;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

//Mapeamos la respuesta del DTO 
public class ProductoDTO {

    
    @Id
    private String id;
    
    @NotBlank
    private String nombre;
    
    @NotNull
    private Double precio;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createAt;
    
    private String foto;
    
    // Este contructor es para el createWithPhoto de ProductoHandler.
    public ProductoDTO(String nombre, Double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }
}
