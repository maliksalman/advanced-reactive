package com.smalik.reactive;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class FileLine {

    private String id;
    private String word;

    public FileLine(final String line) {
        String[] split = line.split(",");
        this.id = split[0];
        this.word = split[1];
    }
}