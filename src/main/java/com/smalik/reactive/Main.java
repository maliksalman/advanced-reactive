package com.smalik.reactive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class Main implements CommandLineRunner {

  Logger logger = LoggerFactory.getLogger(Main.class);
  ObjectMapper mapper = new ObjectMapper();

  public static void main(final String[] args) {
    SpringApplication.run(Main.class, args);
  }

  @Override
  public void run(final String... args) throws Exception {
    Flux
      .fromStream(Files.lines(new File("file.csv").toPath()))
      .map(s -> {
        String[] split = s.split(",");
        return new FileLine(split[0], split[1]);
      })
      .windowUntilChanged(line -> line.id)
      .map(m -> new Sentence(m.collectList()))
      .buffer(3)
      .subscribe(batch -> handleBatch(batch));
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

  static class RealSentence {
    String id;
    String content;
    public RealSentence(List<FileLine> lines) {
      this.id = lines.get(0).id;
      List<String> words = lines.stream().map(w -> w.word).collect(Collectors.toList());
      this.content = String.join(" ", words);
    }
  }

  static class Sentence {
    Mono<List<FileLine>> lines;
    public Sentence(Mono<List<FileLine>> lines) {
      this.lines = lines;
    }
  }
  static class BatchEntry {
    String id;
    String words;
    public BatchEntry(String id, String words) {
      this.id = id;
      this.words = words;
    }
  }

  static class Batch {
    String uuid;
    List<BatchEntry> entries;
    public Batch(String uuid, List<BatchEntry> entries) {
      this.uuid = uuid;
      this.entries = entries;
    }
  }
}