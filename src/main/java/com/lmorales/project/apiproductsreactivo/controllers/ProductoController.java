package com.lmorales.project.apiproductsreactivo.controllers;

import java.io.File;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.lmorales.project.apiproductsreactivo.models.dto.ProductoDTO;
import com.lmorales.project.apiproductsreactivo.service.ProductoService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("${API_PRODUCT}")
@Slf4j
public class ProductoController {
 
    @Autowired
    private ProductoService productoService;

    @Value("${config.uploads.path}")
    private String uploadPath;

    @Value("${config.rute}")
    private String rutePath;

    @GetMapping
    public Mono<ResponseEntity<Flux<ProductoDTO>>> listar() {
        return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(productoService.findAll()));
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String, Object>>> create(@Valid @RequestBody Mono<ProductoDTO> productoDto){
        //Mapeamos la respuesta 
        Map<String, Object> resp = new HashMap<String, Object>();

        return productoDto.flatMap( producto -> {

            if (producto.getCreateAt() == null) {
                producto.setCreateAt(new Date());
            }

                return productoService.save(producto)
                .map( p -> {
                    //Acá mapeamos la respuesta si el producto se guardó
                    resp.put("producto", p);
                    resp.put("mensaje", "El producto fue creado con éxito!");
                    resp.put("timestap", new Date());
                    return ResponseEntity 
                    // .status(HttpStatus.CREATED)
                    .created(URI.create(rutePath.concat(p.getId())))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(resp);
                });
            })
        //Si sale un error
        .onErrorResume(t -> {
            return Mono.just(t).cast(WebExchangeBindException.class)
            .flatMap( e -> Mono.just(e.getFieldErrors()))
            .flatMapMany(err -> Flux.fromIterable(err))
            .map( fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
            .collectList()
            .flatMap( list -> {
                resp.put("erros", list);
                resp.put("timestap", new Date());
                resp.put("status", HttpStatus.BAD_REQUEST.value());
                return Mono.just(ResponseEntity.badRequest().body(resp));
            });
        });
                
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Map<String, Object>>> update(@Valid @RequestBody ProductoDTO productoDto, @PathVariable(value = "id") String id){
            
        Map<String, Object> respuesta = new HashMap<>();

        return productoService.findById(id)

            .flatMap( p -> {
                p.setNombre(productoDto.getNombre());
                p.setPrecio(productoDto.getPrecio());
                return productoService.save(p);
            })

            .map( p -> {
                respuesta.put("productoActualizado", p);
                respuesta.put("mensaje", "El producto a sido actualizado");
                respuesta.put("timestap", new Date());
                return ResponseEntity
                .created(URI.create(rutePath.concat(p.getId())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(respuesta);
            })
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return productoService.findById(id)
        .flatMap( p -> {
            return productoService.detele(p)
            .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
        })
        .defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/upload/{id}")
    public Mono<ResponseEntity<ProductoDTO>> upload(@PathVariable String id, @RequestPart FilePart file) {
        return productoService.findById(id)
        .flatMap( p -> {
            p.setFoto(UUID.randomUUID().toString() + "-" + file.filename()    
            .replace(" ", "")
            .replace(":", "")
            .replace("\\", ""));

            return file.transferTo(new File(uploadPath + p.getFoto()))
            .then(productoService.save(p));
        })
        .map(p -> ResponseEntity.ok(p))
        .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/v2")
    public Mono<ResponseEntity<Map<String, Object>>> createWithPhoto(@Valid ProductoDTO productoMono, @RequestPart FilePart file) {

            Map<String, Object> respuesta = new HashMap<>();

            if (productoMono.getCreateAt() == null) {
                productoMono.setCreateAt(new Date());
            }
            
            productoMono.setFoto(UUID.randomUUID().toString() + "-" + file.filename()
            .replace(" ", "")
            .replace(":", "")
            .replace("\\", "")
            );
            
            // Guardar el archivo y el producto en paralelo
            Mono<Void> saveFile = file.transferTo(new File(uploadPath + productoMono.getFoto()));
            Mono<ProductoDTO> saveProducto = productoService.save(productoMono);

            return Mono.zip(saveProducto, saveFile)
                    .then(Mono.fromCallable(() -> {
                        respuesta.put("producto", productoMono);
                        respuesta.put("mensaje", "El producto fue creado con éxito");
                        respuesta.put("timestamp", new Date());

                        return ResponseEntity
                                .created(URI.create(rutePath + productoMono.getId()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(respuesta);
            }))
            //Si sale un error
            .onErrorResume(t -> {
                return Mono.just(t).cast(WebExchangeBindException.class)
                .flatMap( e -> Mono.just(e.getFieldErrors()))
                .flatMapMany(err -> Flux.fromIterable(err))
                .map( fieldError -> 
                        {
                            log.info(fieldError.getDefaultMessage());
                            return "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage();
                        }
                    )
                            
                .collectList()
                .flatMap( list -> {
                    respuesta.put("erros", list);
                    respuesta.put("timestap", new Date());
                    respuesta.put("status", HttpStatus.BAD_REQUEST.value());
                    return Mono.just(ResponseEntity.badRequest().body(respuesta));
                });
            });
    }
}
