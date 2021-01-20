package com.smalik.reactive;

public class FileLine {

    private String id;
    private String word;

    FileLine(final String line) {
        String[] split = line.split(",");
        this.id = split[0];
        this.word = split[1];
    }

    public String getId() {
        return id;
    }

    public String getWord() {
        return word;
    }
}

