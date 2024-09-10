package org.guillaumechamp.discordbot.io.manager;

import net.dv8tion.jda.api.entities.Guild;
import org.guillaumechamp.discordbot.game.turn.DummyTurn;
import org.guillaumechamp.discordbot.game.turn.PlayerTurn;
import org.guillaumechamp.discordbot.io.UserIntendedException;
import org.guillaumechamp.discordbot.service.BotConfig;
import org.guillaumechamp.discordbot.testUtil.DiscordTestUtil;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

class GameManagerTest {
    private static final Guild testGuild = DiscordTestUtil.getApi().getGuilds().get(0);

    @BeforeAll
    static void mute() {
        BotConfig.changeBotMessagePolicy(true);
    }

    @AfterEach
    void clearChannels() {
        ChannelUtils.clearAllCreatedChannelsFromGuild(testGuild);
    }

    @AfterAll
    static void unmute() {
        BotConfig.changeBotMessagePolicy(false);
    }

    @Test
    void shouldGameManagerHandleCreationWithMissingSizeOfGame() {
        // --Given
        GameManager manager = new GameManager(testGuild);
        // --When
        assertThatNoException().isThrownBy(() -> manager.createGame(null));
    }

    @Test
    void shouldGameManagerHandleCreationUntilLimitIsReach() {
        // --Given
        GameManager manager = new GameManager(testGuild);
        // --When
        for (int i = 0; i < GameManager.MAX_GAME_PER_GUILD; i++) {
            assertThatNoException().isThrownBy(() -> manager.createGame(512));
        }
        // --Then
        assertThatThrownBy(() -> manager.createGame(512))
                .isInstanceOf(UserIntendedException.class)
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_MAX_NUMBER_OF_GAME_REACHED);
    }

    @Test
    void shouldGameManagerThrowExceptionWhileJoiningANonExistingGame() throws UserIntendedException {
        // --Given
        GameManager manager = new GameManager(testGuild);
        // --When
        manager.createGame(512);
        // --Then
        assertThatNoException().isThrownBy(() -> manager.addPlayer(DiscordTestUtil.getOwner(), null));
        assertThatThrownBy(() -> manager.addPlayer(DiscordTestUtil.getOwner(), 1))
                .isInstanceOf(UserIntendedException.class)
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_GAME_DOES_NOT_EXIST);
    }

    @Test
    void shouldGameManagerThrowExceptionWhileStartingANonExistingGame() throws UserIntendedException {
        // --Given
        GameManager manager = new GameManager(testGuild);
        // --When
        manager.createGame(512);
        // --Then
        assertThatThrownBy(() -> manager.start(1))
                .isInstanceOf(UserIntendedException.class)
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_GAME_DOES_NOT_EXIST);
        assertThatNoException().isThrownBy(() -> manager.start(null));
    }

    @Test
    void shouldGameManagerThrowExceptionWhileStopANonExistingGame() throws UserIntendedException {
        // --Given
        GameManager manager = new GameManager(testGuild);
        // --When
        manager.createGame(null);
        // --Then
        assertThatThrownBy(() -> manager.stop(1))
                .isInstanceOf(UserIntendedException.class)
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_GAME_DOES_NOT_EXIST);
        assertThatNoException().isThrownBy(() -> manager.stop(null));
    }

    @Test
    void shouldGameManagerThrowExceptionWhileTransferCommandToTheAction() throws UserIntendedException {
        // --Given
        GameManager manager = new GameManager(testGuild);
        // --When
        manager.createGame(512);
        // --Then
        assertThatThrownBy(() -> manager.transferCommandToTheAction(1, null, null, null))
                .isInstanceOf(UserIntendedException.class)
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_GAME_DOES_NOT_EXIST);
        assertThatThrownBy(() -> manager.transferCommandToTheAction(0, null, null, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRegisterActionWorkProperly(){
        // --Given
        GameManager manager = new GameManager(testGuild);
        // --When
        assertThatNoException().isThrownBy(()->manager.registerAction(0, new DummyTurn(1, PlayerTurn.NONE)));
    }
}
