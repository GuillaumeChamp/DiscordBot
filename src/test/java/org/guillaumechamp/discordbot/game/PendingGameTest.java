package org.guillaumechamp.discordbot.game;

import net.dv8tion.jda.api.entities.Guild;
import org.guillaumechamp.discordbot.io.UserIntendedException;
import org.guillaumechamp.discordbot.io.manager.ChannelUtils;
import org.guillaumechamp.discordbot.service.BotConfig;
import org.guillaumechamp.discordbot.testUtil.DiscordTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    void shouldPendingGameBeCreateWithoutIssue(){
        assertThatNoException().isThrownBy(()->new PendingGame(testGuild,0,512));
    }

    @Test
    @SuppressWarnings("DataFlowIssue")
    void shouldPendingGameBuilderThrowExceptionIfGuildIsNull(){
        assertThatThrownBy(()->new PendingGame(null,0,512))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Pending game must be attached to a guild");
    }

    @Test
    void shouldAddPlayerThrowExceptionIfGameIsExpired(){
        // --Given
        PendingGame game = new PendingGame(testGuild,0,512);
        // --When
        game.terminate();
        // --Then
        assertThatThrownBy(()->game.addPlayer(DiscordTestUtil.getAMember(0)))
                .isInstanceOf(UserIntendedException.class)
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_GAME_ALREADY_STARTED);
    }

    @Test
    void shouldAddPlayerThrowExceptionIfGameIsFull() throws UserIntendedException {
        // --Given
        PendingGame game = new PendingGame(testGuild,0,1);
        // --When
        game.addPlayer(DiscordTestUtil.getAMember(0));
        // --Then
        assertThatThrownBy(()->game.addPlayer(DiscordTestUtil.getAMember(1)))
                .isInstanceOf(UserIntendedException.class)
                // we have a game started exception instead of a game full because the pending game automatically start while full
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_GAME_ALREADY_STARTED);
    }

    @Test
    void shouldStartGaleThrowExceptionIfGameIsExpired(){
        // --Given
        PendingGame game = new PendingGame(testGuild,0,512);
        // --When
        game.terminate();
        // --Then
        assertThatThrownBy(game::startGame)
                .isInstanceOf(UserIntendedException.class)
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_GAME_ALREADY_STARTED);
    }
    /*
    The following aspect are not tested because test must remain silence :
        - Message send when joining the game
        - Message send when the game is created
     */
}
