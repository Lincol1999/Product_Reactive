package com.lmorales.project.apiproductsreactivo.handler;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.lmorales.project.apiproductsreactivo.models.dto.ProductoDTO;
import com.lmorales.project.apiproductsreactivo.service.ProductoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ProductoHandler {
    
    @Autowired
    private ProductoService productoService;

    @Autowired
    private Validator validator;

    @Value("${config.uploads.path}")
    private String uploadPath;
    public Mono<ServerResponse> listar(ServerRequest request){
        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(productoService.findAll(), ProductoDTO.class);
    }
  
    public Mono<ServerResponse> crear(ServerRequest request){
        
        Mono<ProductoDTO> producto = request.bodyToMono(ProductoDTO.class);

        return producto.flatMap(p -> {

            //Creamos un obj que nos permite validad, es de tipo Errors,pero la instancia es de BindingResult
            //BeanPropertyBindingResult recibe el obj que vamos a validad y el tipo del obj Producto
            Errors errors = new BeanPropertyBindingResult(p, ProductoDTO.class.getName());
        
            //el validate recibe el producto y los errores posible en la validación
            validator.validate(p, errors);

            if (errors.hasErrors()) {
                return Flux.fromIterable( errors.getFieldErrors())
                .map( fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collectList()
                .flatMap(list -> ServerResponse.badRequest().body(BodyInserters.fromValue(list)));
            }else{ 

                if (p.getCreateAt() == null) {
                    p.setCreateAt(new Date());
                }

                return productoService.save(p)
                .flatMap(prodDB -> ServerResponse
                    .created(URI.create("/api/v2/producto".concat(prodDB.getId())))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(prodDB))
                );
            }
        });
    }
    
    public Mono<ServerResponse> update(ServerRequest request){

        Mono<ProductoDTO> producto = request.bodyToMono(ProductoDTO.class);

        String id = request.pathVariable("id");

        Mono<ProductoDTO> productoDb = productoService.findById(id);

        return productoDb.zipWith(producto, (db, req) -> {
            db.setNombre(req.getNombre());
            db.setPrecio(req.getPrecio());
            return db;
        })

        .flatMap( p -> ServerResponse
            .created(URI.create("/api/v2/producto".concat(p.getId())))
            .contentType(MediaType.APPLICATION_JSON)
            .body(productoService.save(p), ProductoDTO.class)
            .switchIfEmpty(ServerResponse.notFound().build())
        );

    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        
        String id = request.pathVariable("id");

        Mono<ProductoDTO> productoDb = productoService.findById(id);

        return productoDb.flatMap( p -> 
             productoService.detele(p)
            .then(ServerResponse.noContent().build())
        )
        .switchIfEmpty(ServerResponse.notFound().build());
        
    }

    //Subir la imagen
    public Mono<ServerResponse> upload(ServerRequest request) {

        String id = request.pathVariable("id");

        return request.multipartData()
        .map(multiPart -> multiPart.toSingleValueMap().get("file"))
        .cast(FilePart.class)
        .flatMap( file -> productoService.findById(id)
            .flatMap( p -> {
                p.setFoto(UUID.randomUUID().toString() + "-" + file.filename()
                    .replace(" ", "")
                    .replace(":", "")
                    .replace("\\", "")
                );

                return file.transferTo(new File(uploadPath + p.getFoto()))
                .then(productoService.save(p));
            })

            .flatMap(p -> ServerResponse
                .created(URI.create("/api/v2/producto".concat(p.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(p)))
            .switchIfEmpty(ServerResponse.notFound().build())
        );
    }

    public Mono<ServerResponse> createWithPhoto(ServerRequest request) {
        
        Mono<ProductoDTO> producto = request.multipartData()

        .map(multiPart -> {
            FormFieldPart nombre = (FormFieldPart) multiPart.toSingleValueMap().get("nombre");
            FormFieldPart precio = (FormFieldPart) multiPart.toSingleValueMap().get("precio");
            return new ProductoDTO (nombre.value(), Double.parseDouble(precio.value()));
        });

        return request.multipartData()
        .map(multiPart -> multiPart.toSingleValueMap().get("file"))
        .cast(FilePart.class)
        .flatMap( file -> producto
            .flatMap( p -> {
                p.setFoto(UUID.randomUUID().toString() + "-" + file.filename()
                    .replace(" ", "")
                    .replace(":", "")
                    .replace("\\", "")
                );

                p.setCreateAt(new Date());
                return file.transferTo(new File(uploadPath + p.getFoto()))
                .then(productoService.save(p));
            })
        )
        .flatMap( p -> ServerResponse
            .created(URI.create("/api/v2/producto".concat(p.getId())))
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(p))
        );
    }
}
