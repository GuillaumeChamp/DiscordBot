package org.guillaumechamp.discordbot.service;

import com.sun.jdi.request.InvalidRequestStateException;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.awaitility.Awaitility;
import org.guillaumechamp.discordbot.game.Game;
import org.guillaumechamp.discordbot.game.turn.DummyTurn;
import org.guillaumechamp.discordbot.game.turn.PlayerTurn;
import org.guillaumechamp.discordbot.testUtil.DiscordTestUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

class WaiterServiceTest {
    TextChannel testChannel = DiscordTestUtil.getTestChannel();
    Member testMember = DiscordTestUtil.getAMember(0);

    @BeforeAll
    static void mute(){
        BotConfig.changeBotMessagePolicy(true);
    }
    @AfterAll
    static void unmute(){
        BotConfig.changeBotMessagePolicy(false);
    }

    @Test
    void shouldWaiterAwakeGameProperly() {
        final int duration = 2;
        //-- Given
        Game game = new Game(0, Collections.singletonList(testMember), testChannel);
        DummyTurn action = new DummyTurn(duration, PlayerTurn.NONE);
        //-- When
        WaiterService.register(game, action);
        //-- Then
        Awaitility.await()
                .atLeast(duration - 1, TimeUnit.SECONDS)
                .atMost(duration + 1, TimeUnit.SECONDS)
                .until(action::isExpired);
    }

    @Test
    void shouldWaiterHoldFewActions() {
        final int duration = 3;
        //-- Given
        Game game = new Game(0, Collections.singletonList(testMember), testChannel);
        Game game1 = new Game(1, Collections.singletonList(testMember), testChannel);

        DummyTurn action = new DummyTurn(duration, PlayerTurn.NONE);
        DummyTurn action1 = new DummyTurn(duration, PlayerTurn.NONE);
        //-- When
        WaiterService.register(game, action);
        WaiterService.register(game1, action1);
        //-- Then
        Awaitility.await()
                .atLeast(duration - 1, TimeUnit.SECONDS)
                .atMost(duration + 1, TimeUnit.SECONDS)
                .until(() -> action.isExpired() && action1.isExpired());
    }

    @Test
    void shouldWaiterAlloyCancelling() {
        final int duration = 10;
        //-- Given
        Game game = new Game(2, Collections.singletonList(testMember), testChannel);
        game.terminate();
        DummyTurn action = new DummyTurn(duration, PlayerTurn.NONE);
        //-- When
        WaiterService.register(game, action);
        //-- Then
        assertThat(WaiterService.triggerActionEarlier(game)).isTrue();
    }

    @Test
    void shouldWaiterNotCancelTaskIfNoTaskRegistered() {
        //-- Given
        Game game = new Game(2, Collections.singletonList(testMember), testChannel);
        //-- Then
        assertThat(WaiterService.triggerActionEarlier(game)).isFalse();
    }

    @Test
    void shouldWaiterForbidTwoRegistrations() {
        final int duration = 5;
        //-- Given
        Game game = new Game(0, Collections.singletonList(testMember), testChannel);

        DummyTurn action = new DummyTurn(duration, PlayerTurn.NONE);
        DummyTurn action1 = new DummyTurn(duration, PlayerTurn.NONE);
        //-- When
        WaiterService.register(game, action);
        //-- Then
        assertThatThrownBy(() -> WaiterService.register(game, action1))
                .isInstanceOf(InvalidRequestStateException.class)
                .hasMessage("two actions registered for the same game");
    }
}
