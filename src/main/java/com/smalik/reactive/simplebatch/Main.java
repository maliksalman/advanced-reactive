package com.smalik.reactive.simplebatch;

import java.io.BufferedReader;

import java.util.List;

import com.smalik.reactive.FileLine;
import com.smalik.reactive.Sentence;

import reactor.core.publisher.Flux;

public class Main {

  public void makeBatches(BufferedReader reader) throws Exception {
      Flux
          .fromStream(reader.lines())
          .map(line -> new FileLine(line))
          .windowUntilChanged(fileLine -> fileLine.getId())
          .flatMap(flux -> flux.collectList())
          .map(lines -> new Sentence(lines))
          .buffer(3)
          .subscribe(sentences -> handleBatch(sentences));
  }

  public void handleBatch(List<Sentence> sentences) { }
}