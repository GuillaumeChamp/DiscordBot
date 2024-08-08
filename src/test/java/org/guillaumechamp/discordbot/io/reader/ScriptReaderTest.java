package org.guillaumechamp.discordbot.io.reader;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScriptReaderTest {
    private static final String PATH_FR = "src/main/resources/textFR.properties";
    private static final String PATH_EN = "src/main/resources/textEN.properties";

    /**
     * Check file availability
     */
    @Test
    void shouldRetrieveBasicMessage() {
        String test = ScriptReader.readLine(ScriptReader.KeyEntry.START_SEER, ScriptReader.SupportedLanguage.FR);
        String test1 = ScriptReader.readLine(ScriptReader.KeyEntry.START_SEER, ScriptReader.SupportedLanguage.EN);
        assertThat(test)
                .isNotNull()
                .isNotEqualTo(test1);
    }

    /**
     * Check file integrity
     */
    @Test
    void shouldENFileBeUpToDate() throws IOException {
        //-- When
        Properties propertiesToTest = new Properties();
        InputStream stream = Files.newInputStream(Paths.get(PATH_EN));
        propertiesToTest.load(stream);
        //-- Then
        assertThat(propertiesToTest.keySet()).containsAll(Arrays.stream(ScriptReader.KeyEntry.values()).map(keyEntry -> keyEntry.keyName).toList());
    }

    /**
     * Check file integrity
     */
    @Test
    void shouldFRFileBeUpToDate() throws IOException {
        //-- When
        Properties propertiesToTest = new Properties();
        InputStream stream = Files.newInputStream(Paths.get(PATH_FR));
        propertiesToTest.load(stream);
        //-- Then
        assertThat(propertiesToTest.keySet()).containsAll(Arrays.stream(ScriptReader.KeyEntry.values()).map(keyEntry -> keyEntry.keyName).toList());
    }

    @Test
    void shouldParsingWorkProperly() {
        //-- Given
        Pair<ScriptReader.Tag, String> namePair = Pair.of(ScriptReader.Tag.NAME, "john doe");
        Pair<ScriptReader.Tag, String> rolePair = Pair.of(ScriptReader.Tag.ROLE, "unknown");
        //-- When
        String value = ScriptReader.readLineAndParse(ScriptReader.KeyEntry.ELIMINATED, ScriptReader.SupportedLanguage.EN, namePair, rolePair);
        //-- Then
        assertThat(value).isNotEmpty().doesNotContain("%");
    }

    @Test
    void shouldTagAssertThrowExceptionWhitToManyTag() {
        //-- Given
        List<ScriptReader.Tag> tagList = List.of(ScriptReader.Tag.NAME, ScriptReader.Tag.ROLE);
        //-- Then
        assertThatThrownBy(() -> ScriptReader.KeyEntry.VOTE.assertTagsAreFilled(tagList))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessageContaining("too many tag");
    }

    @Test
    void shouldTagAssertThrowExceptionWhitNotEnough() {
        //-- Given
        List<ScriptReader.Tag> tagList = List.of(ScriptReader.Tag.NAME);
        //-- Then
        assertThatThrownBy(() -> ScriptReader.KeyEntry.ELIMINATED.assertTagsAreFilled(tagList))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessageContaining("missing tag");
    }

    @Test
    void shouldTagAssertThrowExceptionWhitWrongTag() {
        //-- Given
        List<ScriptReader.Tag> tagList = List.of(ScriptReader.Tag.ROLE);
        //-- Then
        assertThatThrownBy(() -> ScriptReader.KeyEntry.WITCH_KILL.assertTagsAreFilled(tagList))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessageContaining("tag " + ScriptReader.Tag.NAME + " is missing");
    }

    @Test
    void shouldReadLineWithTagReturnAnError() {
        assertThatThrownBy(() -> ScriptReader.readLine(ScriptReader.KeyEntry.ELIMINATED, ScriptReader.SupportedLanguage.FR))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessageContaining(Arrays.toString(new ScriptReader.Tag[]{ScriptReader.Tag.NAME, ScriptReader.Tag.ROLE}));
    }
}
