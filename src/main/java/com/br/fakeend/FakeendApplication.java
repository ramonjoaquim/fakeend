package com.br.fakeend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.br.fakeend")
public class FakeendApplication {

  public static void main(String[] args) {
    SpringApplication.run(FakeendApplication.class, args);
  }
}
