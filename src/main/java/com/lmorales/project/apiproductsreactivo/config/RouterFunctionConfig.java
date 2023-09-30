package com.lmorales.project.apiproductsreactivo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.lmorales.project.apiproductsreactivo.handler.ProductoHandler;

@Configuration
public class RouterFunctionConfig {

    @Value("${config.router}")
    private String configRouter;

    @Bean
    RouterFunction<ServerResponse> routes(ProductoHandler handler){
        return RouterFunctions.route(RequestPredicates.GET(configRouter), handler::listar)
            .andRoute(RequestPredicates.POST(configRouter), handler::crear)
            .andRoute(RequestPredicates.PUT(configRouter), handler::update)
            .andRoute(RequestPredicates.DELETE(configRouter.concat("/{id}")), handler::delete)
            .andRoute(RequestPredicates.POST(configRouter.concat("/upload/{id}")), handler::upload)
            .andRoute(RequestPredicates.POST(configRouter.concat("/crear")), handler::createWithPhoto)
        ;
    }
}
