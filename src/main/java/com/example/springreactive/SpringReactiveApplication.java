package com.example.springreactive;

import com.example.springreactive.modal.Product;
import com.example.springreactive.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringReactiveApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringReactiveApplication.class, args);
  }
}
