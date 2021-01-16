package com.smalik.reactive;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class Main implements CommandLineRunner {

  Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(final String[] args) {
    SpringApplication.run(Main.class, args);
  }

  @Override
  public void run(final String... args) throws Exception {

    ConnectableFlux<Object> publish = Flux.create(emitter -> {
      try {
        final File file = new File("/home/smalik/workspace/advanced-reactrive", "file.csv");
        Files.lines(file.toPath())
        .map(line -> {
          final String[] split = line.split(",");
          return new FileLine(split[0], split[1]);
        })
        .forEach(fl -> {
          emitter.next(fl);
        });
        emitter.complete();
      } catch (final IOException e) {
        emitter.error(e);
      }
    })
    .publish();

    publish.connect();

  }

  static class FileLine {
    String id;
    String word;

    FileLine(final String id, final String word) {
      this.id = id;
      this.word = word;
    }
  }
}