package com.example.categoria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class CategoriaRoute {

    private static final String CATEGORIAS_URL = "/categorias";

    @Bean
    public RouterFunction<ServerResponse> route(CustomerHandler handler) {
        return RouterFunctions
                .route(RequestPredicates.GET(CATEGORIAS_URL).and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::getAll)
                .andRoute(RequestPredicates.GET(CATEGORIAS_URL.concat("/{id}")).and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), handler::getById);
        //.andRoute(POST("/customerss").and(accept(MediaType.APPLICATION_JSON)), handler::save);
    }

}

@Component
@RequiredArgsConstructor
class CustomerHandler {

    private final CategoriaService service;

    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
        Flux<Categoria> customers = service.getAll();
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(customers, Categoria.class);
    }

    public Mono<ServerResponse> getById(ServerRequest serverRequest) {
        Mono<Categoria> customer = service.getById(serverRequest.pathVariable("id"));
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(customer, Categoria.class);
    }

//    public Mono<ServerResponse> save(ServerRequest serverRequest) {
//        Mono<Categoria> customerMono = serverRequest.bodyToMono(Categoria.class);
//        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
//                .body(HttpRequest.BodyPublishers.fromPublisher(customerMono.flatMap(service::save), Categoria.class));
//    }

}

@Service
class CategoriaService {

    public Flux<Categoria> getAll() {
        return null;
    }

    public Mono<Categoria> getById(String id) {
        return Mono.just(new Categoria(1L, "Food"));
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Categoria {
    private long id;
    private String nome;
}