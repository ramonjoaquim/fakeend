package br.com.fakeend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@ComponentScan
public class FakeendApplication {

  public static void main(String[] args) {
    SpringApplication.run(FakeendApplication.class, args);
  }
}
