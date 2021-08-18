package com.smalik.reactive.zip;

import java.io.BufferedReader;

import com.smalik.reactive.FileLine;
import com.smalik.reactive.Sentence;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class Zip {
 
    public void process(BufferedReader left, BufferedReader right) {
        Flux<Sentence> sentences = Flux
            .fromStream(left.lines())
            .map(line -> new FileLine(line))
            .windowUntilChanged(line -> line.getId())
            .flatMap(flux -> flux.collectList())
            .map(lines -> new Sentence(lines))
            .log();

        Flux<FileLine> lines = Flux
            .fromStream(right.lines())
            .map(line -> new FileLine(line))
            .log();

        // Flux.zip(sentences, lines)
        //     .doOnNext(t -> handleBatch(t.getT1(), t.getT2()))
        //     .subscribe(t -> System.out.println(t));
        sentences
            .join(lines,
                s -> Flux.never(),
                l -> Flux.just(1),
                // l -> Mono.just(l.getId()),
                (s, l) -> handleBatch(s, l))
            .subscribe(t -> System.out.println(t));
    }

    public Tuple2<Sentence, FileLine> handleBatch(Sentence sentence, FileLine line) {
        return Tuples.of(sentence, line);
    }
}