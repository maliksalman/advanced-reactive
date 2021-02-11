package com.smalik.reactive.simplebatch;

import lombok.Getter;

@Getter
public class FileLine {

    private String id;
    private String word;

    FileLine(final String line) {
        String[] split = line.split(",");
        this.id = split[0];
        this.word = split[1];
    }
}

