package dev.luzifer.model.messages;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

public class MessagesTest {

    @Test
    public void testMessagesAlphabeticallySorted() {

        // it doesn't find the file without the / in front of it, weird.
        try (InputStream inputStream = Messages.class.getResourceAsStream("/messages.properties")) {

            if(inputStream == null)
                fail("messages.properties could not be loaded!");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            List<String> readLines = bufferedReader.lines().collect(Collectors.toList());

            String previousElement = null;

            for (String line : readLines) {

                String nextElement = line;

                if (previousElement != null && nextElement.compareTo(previousElement) <= 0)
                    fail(MessageFormat.format("{0} is not on the right place. Sort it alphabetically!", nextElement));

                previousElement = nextElement;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
