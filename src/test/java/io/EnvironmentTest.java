package io;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EnvironmentTest {
    @Test
    public void shouldTokenBeAccessible() {
        String token = System.getenv("BOT_TOKEN");
        assertThat(token).hasSize(72);
    }
}
