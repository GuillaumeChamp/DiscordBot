package org.guillaumechamp.discordbot.game;

import net.dv8tion.jda.api.entities.Guild;
import org.guillaumechamp.discordbot.io.manager.ChannelUtils;
import org.guillaumechamp.discordbot.service.BotConfig;
import org.guillaumechamp.discordbot.testUtil.DiscordTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;

class PendingGameTest {
    private static final Guild testGuild = DiscordTestUtil.getApi().getGuilds().get(0);


    @BeforeEach
    void setup(){
        BotConfig.changeBotMessagePolicy(true);
    }
    @AfterEach
    void clearChannels() {
        ChannelUtils.clearAllCreatedChannelsFromGuild(testGuild);
    }

    @Test
    void dummyTest(){
        assertThatNoException().isThrownBy(()->new PendingGame(testGuild,0,512));
    }
}
