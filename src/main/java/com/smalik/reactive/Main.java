package com.smalik.reactive;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
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
      .flatMap(m -> m.collectList())
      .map(lines -> new RealSentence(lines))
      .buffer(3)
      .map(sentences -> new Batch(sentences))
      .subscribe(b -> handleBatch(b));
  }

  private void handleBatch(Batch b) {
    try {
      logger.info(mapper.writeValueAsString(b));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
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

    public String getId() {
      return id;
    }
    public String getContent() {
      return content;
    }
  }

  static class Batch {

    String uuid;
    List<RealSentence> sentences;

    public Batch(List<RealSentence> sentences) {
      this.uuid = UUID.randomUUID().toString();
      this.sentences = sentences;
    }

    public String getUuid() {
      return uuid;
    }
    public List<RealSentence> getSentences() {
      return sentences;
    }
  }
}