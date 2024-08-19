package org.guillaumechamp.discordbot.io.reader;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PropertyReaderTest {
    @Test
    void shouldBotPropertyContainPasswordKey(){
        assertThat(PropertyReader.getBotPropertyFromFile("password")).isNotEmpty();
    }

    @Test
    void shouldGetPropertyFromFileRaiseAnExceptionIfFileIsInvalid(){
        assertThatThrownBy(()->PropertyReader.getPropertyFromFile("invalid","invalid"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void shouldGetPropertyFromFileReturnNullIfKeyIsInvalid(){
        assertThat(PropertyReader.getPropertyFromFile("src/main/resources/botConfiguration.properties","invalid")).isNull();
    }
}
