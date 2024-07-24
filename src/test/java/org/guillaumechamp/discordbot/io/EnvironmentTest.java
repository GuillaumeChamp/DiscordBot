package org.guillaumechamp.discordbot.io;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnvironmentTest {
    @Test
    void shouldTokenBeAccessible() {
        String token = System.getenv("BOT_TOKEN");
        assertThat(token).hasSize(72);
    }
}
