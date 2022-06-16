package com.example.springreactive.config;

import com.example.springreactive.modal.Product;
import com.example.springreactive.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Flux;

@Configuration
public class DataSetup {
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
    };
  }
}
