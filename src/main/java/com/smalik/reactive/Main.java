package com.smalik.reactive;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;

import java.io.*;
import java.nio.charset.Charset;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class Main implements CommandLineRunner {

    public static void main(final String[] args) {
        SpringApplication.run(Main.class, args);
    }

    private Logger logger = LoggerFactory.getLogger(Main.class);
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void run(final String... args) throws Exception {

        try (
                InputStream stream = getClass().getClassLoader().getResourceAsStream("data.csv");
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charset.defaultCharset()))) {

            Flux
                    .fromStream(reader.lines())
                    .map(line -> new FileLine(line))
                    .windowUntilChanged(fileLine -> fileLine.getId())
                    .flatMap(flux -> flux.collectList())
                    .map(lines -> new Sentence(lines))
                    .buffer(3)
                    .map(sentences -> new Batch(sentences))
                    .subscribe(b -> {
                        try {
                            logger.info(mapper.writeValueAsString(b));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}