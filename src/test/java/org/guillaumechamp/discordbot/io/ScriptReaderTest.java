package org.guillaumechamp.discordbot.io;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class ScriptReaderTest {
    private static final String PATH_FR = "src/main/resources/textFR.properties";
    private static final String PATH_EN = "src/main/resources/textEN.properties";

    /**
     * Check file availability
     */
    @Test
    void shouldRetrieveBasicMessage() {
        String test = ScriptReader.readLine("vote", ScriptReader.TextLanguage.FR);
        String test1 = ScriptReader.readLine("vote", ScriptReader.TextLanguage.EN);
        assertThat(test)
                .isNotNull()
                .isNotEqualTo(test1);
    }

    /**
     * Check file integrity
     */
    @Test
    void shouldENFileBeUpToDate() throws IOException {
        //-- Given
        Properties sourceProperties = new Properties();
        InputStream stream = Files.newInputStream(Paths.get(PATH_FR));
        sourceProperties.load(stream);
        //-- When
        Properties propertiesToTest = new Properties();
        stream = Files.newInputStream(Paths.get(PATH_EN));
        propertiesToTest.load(stream);
        //-- Then
        assertThat(propertiesToTest.keySet()).containsAll(sourceProperties.keySet());
    }

    /**
     * Check file integrity
     */
    @Test
    void shouldFRFileBeUpToDate() throws IOException {
        //-- Given
        Properties sourceProperties = new Properties();
        InputStream stream = Files.newInputStream(Paths.get(PATH_EN));
        sourceProperties.load(stream);
        //-- When
        Properties propertiesToTest = new Properties();
        stream = Files.newInputStream(Paths.get(PATH_FR));
        propertiesToTest.load(stream);
        //-- Then
        assertThat(propertiesToTest.keySet()).containsAll(sourceProperties.keySet());
    }
}
