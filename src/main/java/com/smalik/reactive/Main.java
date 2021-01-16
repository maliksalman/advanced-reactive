package com.smalik.reactive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class Main implements CommandLineRunner {

  Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(final String[] args) {
    SpringApplication.run(Main.class, args);
  }

  @Override
  public void run(final String... args) throws Exception {

    // create flux from FileLine objects
    ConnectableFlux<FileLine> flux = Flux.<FileLine>push(emitter -> {
      try {
        final File file = new File("file.csv");
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
    }).publish();

    // do some transformations
    flux
      .windowUntilChanged(fl -> fl.id)
      .map(m -> new Sentence(m.collectList()))
      .buffer(3)
      .subscribe(batch -> handleBatch(batch));

    // lets start everything
    flux.connect();
  }

  private void handleBatch(List<Sentence> batch) {
    logger.info(">>>> BATCH <<<<");
    for (Sentence sentence : batch) {
      Mono<List<FileLine>> lines = sentence.lines;
      lines.subscribe(l -> {
        String id = l.get(0).id;
        List<String> words = l.stream().map(w -> w.word).collect(Collectors.toList());
        logger.info("ID={} Words={}", id, String.join(" ", words));
      });
    }
  }

  static class FileLine {
    String id;
    String word;

    FileLine(final String id, final String word) {
      this.id = id;
      this.word = word;
    }
  }

  static class Sentence {
    Mono<List<FileLine>> lines;
    public Sentence(Mono<List<FileLine>> lines) {
      this.lines = lines;
    }
  }
}