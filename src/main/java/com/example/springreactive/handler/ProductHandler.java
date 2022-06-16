package com.example.springreactive.handler;

import com.example.springreactive.modal.Product;
import com.example.springreactive.modal.ProductEvent;
import com.example.springreactive.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Service
public class ProductHandler {
  private final ProductRepository productRepository;

  public ProductHandler(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public Mono<ServerResponse> getAllProducts(ServerRequest request) {
    Flux<Product> products = productRepository.findAll();

    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(products, Product.class);
  }

  public Mono<ServerResponse> getProduct(ServerRequest request) {
    String id = request.pathVariable("id");

    Mono<Product> productMono = this.productRepository.findById(id);
    Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    return productMono.flatMap(
        product ->
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(product))
                .switchIfEmpty(notFound));
  }

  public Mono<ServerResponse> saveProduct(ServerRequest request) {
    Mono<Product> productMono = request.bodyToMono(Product.class);

    return productMono.flatMap(
        product ->
            ServerResponse.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(productRepository.save(product), Product.class));
  }

  public Mono<ServerResponse> updateProduct(ServerRequest request) {
    String id = request.pathVariable("id");

    Mono<Product> existingProductMono = this.productRepository.findById(id);
    Mono<Product> productMono = request.bodyToMono(Product.class);
    Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    return productMono
        .zipWith(
            existingProductMono,
            (product, existingProduct) ->
                new Product(existingProduct.getId(), product.getName(), product.getPrice()))
        .flatMap(
            product ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(productRepository.save(product), Product.class))
        .switchIfEmpty(notFound);
  }

  public Mono<ServerResponse> deleteProduct(ServerRequest request) {
    String id = request.pathVariable("id");

    Mono<Product> productMono = request.bodyToMono(Product.class);
    Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    return productMono
        .flatMap(
            existingProduct -> ServerResponse.ok().build(productRepository.delete(existingProduct)))
        .switchIfEmpty(notFound);
  }

  public Mono<ServerResponse> deleteAll(ServerRequest request) {

    return ServerResponse.ok().build(productRepository.deleteAll());
  }

  public Mono<ServerResponse> getProductEvents(ServerRequest request) {
    Flux<ProductEvent> productEventFlux =
        Flux.interval(Duration.ofSeconds(1))
            .map(val -> ProductEvent.builder().eventId(val).eventType("Product Event").build());

    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(productEventFlux, ProductEvent.class);
  }
}
