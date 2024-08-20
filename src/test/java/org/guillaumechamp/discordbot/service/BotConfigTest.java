package org.guillaumechamp.discordbot.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BotConfigTest {
    @Test
    void shouldIsSilenceBeingAccessible(){
        assertThat(BotConfig.isSilence()).isFalse();
    }
    @Test
    void shouldAllowSettingSilenceMod(){
        // --When
        BotConfig.changeBotMessagePolicy(true);
        // --Then
        assertThat(BotConfig.isSilence()).isTrue();
        // --Finally
        BotConfig.changeBotMessagePolicy(false);

    }
}
