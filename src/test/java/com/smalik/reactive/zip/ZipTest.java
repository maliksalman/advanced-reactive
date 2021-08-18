package com.smalik.reactive.zip;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.StringReader;

import com.smalik.reactive.FileLine;
import com.smalik.reactive.Sentence;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ZipTest {
    
    @Test
    void testProcess() throws Exception {

        final String dataLeft = 
        "01,hello\r\n" +
        "01,world\r\n" +
        "02,foo\r\n" +
        "02,bar\r\n" +
        "03,how\r\n" +
        "03,you\r\n" +
        "03,doin\r\n" +
        "04,hello\r\n" +
        "04,world\r\n" +
        "05,foo\r\n" +
        "05,bar\r\n" +
        "06,how\r\n" +
        "06,you\r\n" +
        "06,doin\r\n";
  
        final String dataRight = 
        "01,101\r\n" +
        "02,102\r\n" +
        "03,103\r\n" +
        "04,104\r\n" +
        "05,105\r\n" +
        "06,106\r\n";

        try (
            // GIVEN
            BufferedReader left = new BufferedReader(new StringReader(dataLeft));
            BufferedReader right = new BufferedReader(new StringReader(dataRight))) {

            final Zip tested = spy(new Zip());
            
            // WHEN
            tested.process(left, right);
    
            // THEN
            final ArgumentCaptor<Sentence> capturedSentence = ArgumentCaptor.forClass(Sentence.class);
            final ArgumentCaptor<FileLine> capturedLine = ArgumentCaptor.forClass(FileLine.class);
            verify(tested, times(6)).handleBatch(capturedSentence.capture(), capturedLine.capture());
    
            assertThat(capturedSentence.getAllValues().get(0)).isEqualTo(new Sentence("01", "hello world"));
            assertThat(capturedSentence.getAllValues().get(1)).isEqualTo(new Sentence("02", "foo bar"));
            assertThat(capturedSentence.getAllValues().get(2)).isEqualTo(new Sentence("03", "how you doin"));
            assertThat(capturedSentence.getAllValues().get(3)).isEqualTo(new Sentence("04", "hello world"));
            assertThat(capturedSentence.getAllValues().get(4)).isEqualTo(new Sentence("05", "foo bar"));
            assertThat(capturedSentence.getAllValues().get(5)).isEqualTo(new Sentence("06", "how you doin"));

            assertThat(capturedLine.getAllValues().get(0)).isEqualTo(new FileLine("01,101"));
            assertThat(capturedLine.getAllValues().get(1)).isEqualTo(new FileLine("02,102"));
            assertThat(capturedLine.getAllValues().get(2)).isEqualTo(new FileLine("03,103"));
            assertThat(capturedLine.getAllValues().get(3)).isEqualTo(new FileLine("04,104"));
            assertThat(capturedLine.getAllValues().get(4)).isEqualTo(new FileLine("05,105"));
            assertThat(capturedLine.getAllValues().get(5)).isEqualTo(new FileLine("06,106"));
        }
    }
}