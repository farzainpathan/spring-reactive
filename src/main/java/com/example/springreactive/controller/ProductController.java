package com.example.springreactive.controller;

import com.example.springreactive.modal.Product;
import com.example.springreactive.modal.ProductEvent;
import com.example.springreactive.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/products")
public class ProductController {

  private final ProductRepository productRepository;

  public ProductController(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @GetMapping
  public Flux<Product> getAllProducts() {
    return productRepository.findAll();
  }

  @GetMapping("{id}")
  public Mono<ResponseEntity<Product>> getProduct(@PathVariable String id) {
    return productRepository
        .findById(id)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Product> saveProduct(@RequestBody Product product) {
    return productRepository.save(product);
  }

  @PutMapping("{id}")
  public Mono<ResponseEntity<Product>> updateProduct(
      @PathVariable(value = "id") String id, @RequestBody Product product) {

    return productRepository
        .findById(id)
        .flatMap(
            productExist ->
                productRepository.save(
                    Product.builder().name(product.getName()).price(product.getPrice()).build()))
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @DeleteMapping("{id}")
  public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable(value = "id") String id) {

    return productRepository
        .findById(id)
        .flatMap(
            existingProduct ->
                productRepository
                    .delete(existingProduct)
                    .then(Mono.just(ResponseEntity.ok().<Void>build())))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @DeleteMapping
  public Mono<Void> deleteAllProducts() {
    return productRepository.deleteAll();
  }

  @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ProductEvent> getProducedEvents() {
    return Flux.interval(Duration.ofSeconds(1))
        .map(value -> new ProductEvent(value, "Product Event"));
  }
}
