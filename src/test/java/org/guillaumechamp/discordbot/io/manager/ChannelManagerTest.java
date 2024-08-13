package org.guillaumechamp.discordbot.io.manager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.guillaumechamp.discordbot.testUtil.DiscordTestUtil;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;

@SuppressWarnings("DataFlowIssue")
class ChannelManagerTest {
    @Test
    void shouldClearAllCreatedChannelsFromGuildClearAllChannels() {
        // --Given
        Guild guild = DiscordTestUtil.getApi().getGuilds().get(0);
        guild.createTextChannel(ChannelManager.getGameChannelNameByIndexAndStatus(0, true)).queue();
        guild.createTextChannel(ChannelManager.getGameChannelNameByIndexAndStatus(1, false)).queue();
        guild.createTextChannel(ChannelManager.getGameChannelNameByIndexAndStatus(2, true)).queue();
        // --When
        await().pollDelay(2, TimeUnit.SECONDS).until(() -> true);
        ChannelManager.clearAllCreatedChannelsFromGuild(guild);
        await().pollDelay(2, TimeUnit.SECONDS).until(() -> true);
        // --Then
        assertThat(guild.getTextChannelsByName(ChannelManager.getGameChannelNameByIndexAndStatus(0, true), true)).isEmpty();
        assertThat(guild.getTextChannelsByName(ChannelManager.getGameChannelNameByIndexAndStatus(1, false), true)).isEmpty();
        assertThat(guild.getTextChannelsByName(ChannelManager.getGameChannelNameByIndexAndStatus(2, true), true)).isEmpty();
    }

    @Test
    void shouldClearAllCreatedChannelsFromGuildDoNotingIfGuildIsNull() {
        assertThatNoException().isThrownBy(() -> ChannelManager.clearAllCreatedChannelsFromGuild(null));
    }

    @Test
    void shouldGetGameChannelNameByIndexAndStatusThrowExceptionForNegativeIndex() {
        assertThatThrownBy(() -> ChannelManager.getGameChannelNameByIndexAndStatus(-1, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("index must be positive");
        assertThatThrownBy(() -> ChannelManager.getGameChannelNameByIndexAndStatus(-1, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("index must be positive");
    }

    @Test
    void shouldGetGameChannelNameByIndexAndStatusThrowExceptionForExcessiveNumber() {
        assertThatThrownBy(() -> ChannelManager.getGameChannelNameByIndexAndStatus(GameManager.MAX_GAME_PER_GUILD, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Index out of bound");
        assertThatThrownBy(() -> ChannelManager.getGameChannelNameByIndexAndStatus(GameManager.MAX_GAME_PER_GUILD, false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Index out of bound");
    }

    @Test
    void shouldGetGameChannelNameByIndexAndStatusReturnWolfChannelString() {
        assertThat(ChannelManager.getGameChannelNameByIndexAndStatus(0, true))
                .isNotEmpty()
                .isEqualTo("game0wolf");
    }

    @Test
    void shouldGetGameChannelNameByIndexAndStatusReturnPublicChannelString() {
        assertThat(ChannelManager.getGameChannelNameByIndexAndStatus(0, false))
                .isNotEmpty()
                .isEqualTo("game0");
    }

    @Test
    void shouldCreateChannelForAGuildThrowExceptionIfGuildIsNull() {
        assertThatThrownBy(() -> ChannelManager.createChannelForAGuild(null,null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("server must not be null");
    }

    @Test
    void shouldCreateChannelForAGuildThrowExceptionIfNameIsNull() {
        Guild guild = DiscordTestUtil.getApi().getGuilds().get(0);
        assertThatThrownBy(() -> ChannelManager.createChannelForAGuild(guild,null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("channel name must not be empty");
    }

    @Test
    void shouldCreateChannelForAGuildThrowExceptionIfNameIsEmpty() {
        Guild guild = DiscordTestUtil.getApi().getGuilds().get(0);
        assertThatThrownBy(() -> ChannelManager.createChannelForAGuild(guild,""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("channel name must not be empty");
    }

    @Test
    void shouldCreateChannelForAGuildOverrideOlderChannelWithTheSameName() {
        // --Given
        Guild guild = DiscordTestUtil.getApi().getGuilds().get(0);
        String randomName = "fksf64fser";
        ChannelManager.createChannelForAGuild(guild,randomName);
        TextChannel createdChannel = guild.getTextChannelsByName(randomName, false).get(0);
        // --When
        ChannelManager.createChannelForAGuild(guild,randomName);
        TextChannel newChannel = guild.getTextChannelsByName(randomName, false).get(0);
        // --Then
        assertThat(createdChannel.getTimeCreated()).isNotEqualTo(newChannel.getTimeCreated());
        // --Finally
        newChannel.delete().queue();
    }
}
