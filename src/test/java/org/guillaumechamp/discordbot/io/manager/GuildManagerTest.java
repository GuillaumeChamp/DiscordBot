package org.guillaumechamp.discordbot.io.manager;

import org.guillaumechamp.discordbot.testUtil.DiscordTestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GuildManagerTest {

    @Test
    void shouldGetManagerShouldRetrieveExistingInstance(){
        // --Given
        GameManager gameManager = GuildManager.getGameManager(DiscordTestUtil.getApi().getGuilds().get(0));
        // --When
        GameManager gameManager1 = GuildManager.getGameManager(DiscordTestUtil.getApi().getGuilds().get(0));
        // --Then
        assertThat(gameManager).isEqualTo(gameManager1);
    }

    /* at this moment, the bot is connected to only one guild so the following test are not possible
        - should create a game manager per guild
     */
}
