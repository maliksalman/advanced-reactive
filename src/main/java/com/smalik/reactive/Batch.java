package com.smalik.reactive;

import java.util.List;
import java.util.UUID;

public class Batch {

    private String uuid;
    private List<Sentence> sentences;

    public Batch(List<Sentence> sentences) {
      this.uuid = UUID.randomUUID().toString();
      this.sentences = sentences;
    }

    public String getUuid() {
      return uuid;
    }
    public List<Sentence> getSentences() {
      return sentences;
    }
  }