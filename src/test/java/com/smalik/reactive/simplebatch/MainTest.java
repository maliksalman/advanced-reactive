package com.smalik.reactive.simplebatch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class MainTest {

  @Test
  void testMakeBatches() throws Exception {

    String data = 
      "06256,hello\r\n" +
      "06256,world 0\r\n" +
      "06789,foo\r\n" +
      "06789,bar\r\n" +
      "06789,fiz 0\r\n" +
      "06087,how\r\n" +
      "06087,you\r\n" +
      "06087,doin 0\r\n" +
      "13456,hello\r\n" +
      "13456,world 1\r\n" +
      "16789,foo\r\n" +
      "16789,bar\r\n" +
      "16789,fiz\r\n" +
      "16789,foo\r\n" +
      "16789,bar\r\n" +
      "16789,fiz 1\r\n" +
      "19087,how\r\n" +
      "19087,you\r\n" +
      "19087,doin 1\r\n" +
      "23456,hello\r\n" +
      "23456,world 2\r\n";

    try (
      // GIVEN
      BufferedReader reader = new BufferedReader(new StringReader(data))) {
        
      // WHEN
      Main tested = spy(new Main());
      tested.makeBatches(reader);

      // THEN
      ArgumentCaptor<List<Sentence>> captured = ArgumentCaptor.forClass(List.class);
      verify(tested, times(3)).handleBatch(captured.capture());

      assertThat(captured.getAllValues().get(0)).isEqualTo(Arrays.asList(
        new Sentence("06256", "hello world 0"),
        new Sentence("06789", "foo bar fiz 0"),
        new Sentence("06087", "how you doin 0")
      ));
      assertThat(captured.getAllValues().get(1)).isEqualTo(Arrays.asList(
        new Sentence("13456", "hello world 1"),
        new Sentence("16789", "foo bar fiz foo bar fiz 1"),
        new Sentence("19087", "how you doin 1")
      ));
      assertThat(captured.getAllValues().get(2)).isEqualTo(Arrays.asList(
        new Sentence("23456", "hello world 2")
      ));
    }
  }
}