package org.guillaumechamp.discordbot.io.manager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.guillaumechamp.discordbot.service.BotConfig;
import org.guillaumechamp.discordbot.testUtil.DiscordTestUtil;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
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
        assertThatThrownBy(() -> ChannelManager.createChannelForAGuild(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("server must not be null");
    }

    @Test
    void shouldCreateChannelForAGuildThrowExceptionIfNameIsNull() {
        Guild guild = DiscordTestUtil.getApi().getGuilds().get(0);
        assertThatThrownBy(() -> ChannelManager.createChannelForAGuild(guild, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("channel name must not be empty");
    }

    @Test
    void shouldCreateChannelForAGuildThrowExceptionIfNameIsEmpty() {
        Guild guild = DiscordTestUtil.getApi().getGuilds().get(0);
        assertThatThrownBy(() -> ChannelManager.createChannelForAGuild(guild, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("channel name must not be empty");
    }

    @Test
    void shouldCreateChannelForAGuildOverrideOlderChannelWithTheSameName() {
        // --Given
        Guild guild = DiscordTestUtil.getApi().getGuilds().get(0);
        String randomName = "fksf64fser";
        ChannelManager.createChannelForAGuild(guild, randomName);
        TextChannel createdChannel = guild.getTextChannelsByName(randomName, false).get(0);
        await().pollDelay(2, TimeUnit.SECONDS).until(() -> true);
        // --When
        ChannelManager.createChannelForAGuild(guild, randomName);
        TextChannel newChannel = guild.getTextChannelsByName(randomName, false).get(0);
        // --Then
        assertThat(createdChannel.getTimeCreated()).isNotEqualTo(newChannel.getTimeCreated());
        // --Finally
        newChannel.delete().queue();
    }

    @Test
    void shouldResolveGameIndexFromChannelNameReturnValidValueForValidInput() {
        assertThat(ChannelManager.resolveGameIndexFromChannelName("game2werewolf")).isEqualTo(2);
    }

    @Test
    void shouldResolveGameIndexFromChannelNameThrowExceptionIfChannelNotStartByGame() {
        assertThatThrownBy(() -> ChannelManager.resolveGameIndexFromChannelName("sssgame2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("This is not a game channel");
    }

    @Test
    void shouldResolveGameIndexFromChannelNameThrowExceptionIfNumberIsInvalid() {
        assertThatThrownBy(() -> ChannelManager.resolveGameIndexFromChannelName("game-"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("This is not a valid pattern name, expected game#suffix");
    }

    @Test
    void shouldResolveGameIndexFromChannelNameThrowExceptionIfNumberIsTooHigh() {
        assertThatThrownBy(() -> ChannelManager.resolveGameIndexFromChannelName("game" + GameManager.MAX_GAME_PER_GUILD + 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("This is not a valid pattern name, expected game#suffix");
    }

    @Test
    void shouldMuteThrowExceptionIfMemberIsNull() {
        assertThatThrownBy(() -> ChannelManager.muteAMember(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("member is null");
        assertThatThrownBy(() -> ChannelManager.unmuteAMember(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("member is null");
    }

    @Test
    void shouldMuteBeIgnoredIfMemberIsNotInAVoiceChannel() {
        Member member = DiscordTestUtil.getAMember(0);
        assertThatNoException().isThrownBy(() -> ChannelManager.muteAMember(member));
        assertThatNoException().isThrownBy(() -> ChannelManager.unmuteAMember(member));
    }

    @Test
    void shouldSendPrivateMessageToAMemberThrowExceptionIfMemberIsNull() {
        assertThatThrownBy(() -> ChannelManager.sendPrivateMessageToAMember(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("destination is null");
    }

    @Test
    void shouldSendPrivateMessageToAMemberThrowExceptionIfMessageIsEmpty() {
        Member member = DiscordTestUtil.getAMember(0);
        assertThatThrownBy(() -> ChannelManager.sendPrivateMessageToAMember(member, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("message is null or empty");
        assertThatThrownBy(() -> ChannelManager.sendPrivateMessageToAMember(member, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("message is null or empty");
    }

    @Test
    void shouldSendPrivateMessageToAMemberNotSendMessageIfMuteMode() {
        // --Given
        Member member = DiscordTestUtil.getOwner();
        PrivateChannel privateChannel = member.getUser().openPrivateChannel().complete();
        String testMessage = "This test message will never be seen #00000";
        BotConfig.changeBotMessagePolicy(true);
        // --When
        ChannelManager.sendPrivateMessageToAMember(member, testMessage);
        BotConfig.changeBotMessagePolicy(false);
        await().pollDelay(1, TimeUnit.SECONDS).until(() -> true);
        // --Then
        assertThat(privateChannel.getHistoryAround(privateChannel.getLatestMessageId(), 1).complete().getRetrievedHistory())
                .last()
                .extracting(Message::getContentRaw)
                .isNotEqualTo(testMessage);

    }

    @Test
    void shouldSendPrivateMessageToAMemberWorkProperly() {
        // --Given
        Member member = DiscordTestUtil.getOwner();
        PrivateChannel privateChannel = member.getUser().openPrivateChannel().complete();
        String testMessage = "This is a test message please ignore me #00001";
        // --When
        ChannelManager.sendPrivateMessageToAMember(member, testMessage);
        await().pollDelay(1, TimeUnit.SECONDS).until(() -> true);
        // --Then
        assertThat(privateChannel.getHistoryAround(privateChannel.getLatestMessageId(), 1).complete().getRetrievedHistory())
                .last()
                .extracting(Message::getContentRaw)
                .isEqualTo(testMessage);
    }

    @Test
    void shouldSendMessageToAChannelThrowExceptionIfMemberIsNull() {
        assertThatThrownBy(() -> ChannelManager.sendMessageToAChannel(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("destination is null");
    }

    @Test
    void shouldSendMessageToAChannelThrowExceptionIfMessageIsEmpty() {
        TextChannel testChannel = DiscordTestUtil.getTestChannel();
        assertThatThrownBy(() -> ChannelManager.sendMessageToAChannel(testChannel, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("message is null or empty");
        assertThatThrownBy(() -> ChannelManager.sendMessageToAChannel(testChannel, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("message is null or empty");
    }

    @Test
    void shouldSendMessageToAChannelNotSendMessageIfMuteMode() {
        // --Given
        TextChannel testChannel = DiscordTestUtil.getTestChannel();
        String testMessage = "This test message will never be seen #00003";
        BotConfig.changeBotMessagePolicy(true);
        // --When
        ChannelManager.sendMessageToAChannel(testChannel, testMessage);
        BotConfig.changeBotMessagePolicy(false);
        await().pollDelay(1, TimeUnit.SECONDS).until(() -> true);
        // --Then
        assertThat(testChannel.getHistoryAround(testChannel.getLatestMessageId(), 1).complete().getRetrievedHistory())
                .last()
                .extracting(Message::getContentRaw)
                .isNotEqualTo(testMessage);

    }

    @Test
    void shouldSendMessageToAChannelWorkProperly() {
        // --Given
        TextChannel testChannel = DiscordTestUtil.getTestChannel();
        String testMessage = "This is a test message please ignore me #00004";
        // --When
        ChannelManager.sendMessageToAChannel(testChannel, testMessage);
        await().pollDelay(1, TimeUnit.SECONDS).until(() -> true);
        // --Then
        assertThat(testChannel.getHistoryAround(testChannel.getLatestMessageId(), 1).complete().getRetrievedHistory())
                .last()
                .extracting(Message::getContentRaw)
                .isEqualTo(testMessage);
    }

    @Test
    void shouldCreateRestrictedChannelThrowExceptionIfMemberIsNull() {
        assertThatThrownBy(() -> ChannelManager.createRestrictedChannel(null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("server is null");
    }

    @Test
    void shouldCreateRestrictedChannelThrowExceptionIfChannelNameIsEmpty() {
        Guild server = DiscordTestUtil.getApi().getGuilds().get(0);
        assertThatThrownBy(() -> ChannelManager.createRestrictedChannel(server, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("channel name is null or empty");
        assertThatThrownBy(() -> ChannelManager.createRestrictedChannel(server, null, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("channel name is null or empty");
    }

    @Test
    void shouldCreateRestrictedChannelNotThrowExceptionIfMembersIsNullOrEmpty() {
        // --Given
        String testName = "testChannel17";
        Guild server = DiscordTestUtil.getApi().getGuilds().get(0);
        // --When
        assertThatNoException().isThrownBy(() -> ChannelManager.createRestrictedChannel(server, null, testName));
        assertThatNoException().isThrownBy(() -> ChannelManager.createRestrictedChannel(server, Collections.emptyList(), testName));
        // --Finally
        server.getTextChannelsByName(testName, true).forEach(channel -> channel.delete().queue());
    }

    @Test
    void shouldCreateRestrictedChannelWorkWithAList() {
        // --Given
        String testName = "testChannel18";
        Guild server = DiscordTestUtil.getApi().getGuilds().get(0);
        List<Member> testMembers = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1));
        // --When
        ChannelManager.createRestrictedChannel(server, testMembers, testName);
        // --Then
        assertThat(server.getTextChannelsByName(testName, true))
                .singleElement()
                .satisfies(channel -> {
                    assertThat(channel.canTalk(testMembers.get(0))).isTrue();
                    assertThat(channel.canTalk(testMembers.get(1))).isTrue();
                });
        // --Finally
        server.getTextChannelsByName(testName, true).forEach(channel -> channel.delete().queue());
    }


    /*
    ### the following test are missing because there are too expensive, intrusive or depend on members online###
        - create restricted channel grant the right permissions (because bot and owner are invalid test user and need two more users)
        - mute a member (because need an online user and will mute it which is too intrusive)
     */

}
