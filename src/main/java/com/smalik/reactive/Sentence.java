package com.smalik.reactive;

import java.util.List;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Sentence {

    private String id;
    private String content;

    public Sentence(List<FileLine> lines) {
      this.id = lines.get(0).getId();
      List<String> words = lines.stream().map(w -> w.getWord()).collect(Collectors.toList());
      this.content = String.join(" ", words);
    }

    public Sentence(String id, String content) {
      this.id = id;
      this.content = content;
    }
  }
