package com.example.springreactive.router;

import com.example.springreactive.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class Router {
  @Bean
  RouterFunction<ServerResponse> routes(ProductHandler handler) {
    return route()
        .GET("/handler/products/events", accept(TEXT_EVENT_STREAM), handler::getProductEvents)
        .GET("/handler/products/{id}", accept(APPLICATION_JSON), handler::getProduct)
        .GET("/handler/products", accept(APPLICATION_JSON), handler::getAllProducts)
        .PUT("/handler/products/{id}", accept(APPLICATION_JSON), handler::updateProduct)
        .POST("/handler/products", accept(APPLICATION_JSON), handler::saveProduct)
        .DELETE("/handler/products/{id}", accept(APPLICATION_JSON), handler::deleteProduct)
        .DELETE("/handler/products", accept(APPLICATION_JSON), handler::deleteAll)
        .build();
  }
}
