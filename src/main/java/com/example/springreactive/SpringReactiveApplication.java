package com.example.springreactive;

import com.example.springreactive.handler.ProductHandler;
import com.example.springreactive.modal.Product;
import com.example.springreactive.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
public class SpringReactiveApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringReactiveApplication.class, args);
  }

  @Bean
  CommandLineRunner init(ReactiveMongoOperations operations, ProductRepository productRepository) {
    return args -> {
      Flux<Product> productFlux =
          Flux.just(
                  Product.builder().id(null).name("Big Latte").price(2.99).build(),
                  Product.builder().id(null).name("Big Decaf").price(2.49).build(),
                  Product.builder().id(null).name("Green Tea").price(1.99).build())
              .flatMap(productRepository::save);

      productFlux.thenMany(productRepository.findAll()).subscribe(System.out::println);

      /*operations
      .collectionExists(Product.class)
      .flatMap(exists -> exists ? operations.dropCollection(Product.class) : Mono.just(exists))
      .thenMany(v -> operations.createCollection(Product.class))
      .thenMany(productFlux)
      .thenMany(productRepository.findAll())
      .subscribe(System.out::println);*/
    };
  }

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
