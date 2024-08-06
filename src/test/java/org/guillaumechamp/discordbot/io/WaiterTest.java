package org.guillaumechamp.discordbot.io;

import com.sun.jdi.request.InvalidRequestStateException;
import org.awaitility.Awaitility;
import org.guillaumechamp.discordbot.game.Game;
import org.guillaumechamp.discordbot.game.turn.DummyTurn;
import org.guillaumechamp.discordbot.game.turn.PlayerTurn;
import org.guillaumechamp.discordbot.io.listener.Waiter;
import org.guillaumechamp.discordbot.util.AbstractDiscordTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;

class WaiterTest extends AbstractDiscordTest {
    @Test
    void shouldWaiterAwakeGameProperly() {
        final int duration = 4;
        //-- Given
        Game game = new Game(0, Collections.singletonList(testMember), testChannel);
        DummyTurn action = new DummyTurn(duration, PlayerTurn.NONE);
        //-- When
        Waiter.register(game, action);
        //-- Then
        Awaitility.await()
                .atLeast(duration - 1, TimeUnit.SECONDS)
                .atMost(duration + 1, TimeUnit.SECONDS)
                .until(action::isExpired);
    }

    @Test
    void shouldWaiterHoldFewActions() {
        final int duration = 5;
        //-- Given
        Game game = new Game(0, Collections.singletonList(testMember), testChannel);
        Game game1 = new Game(1, Collections.singletonList(testMember), testChannel);

        DummyTurn action = new DummyTurn(duration, PlayerTurn.NONE);
        DummyTurn action1 = new DummyTurn(duration, PlayerTurn.NONE);
        //-- When
        Waiter.register(game, action);
        Waiter.register(game1, action1);
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
        Waiter.register(game, action);
        //-- Then
        assertThat(Waiter.triggerActionEarlier(game)).isTrue();
    }

    @Test
    void shouldWaiterNotCancelTaskIfNoTaskRegistered() {
        //-- Given
        Game game = new Game(2, Collections.singletonList(testMember), testChannel);
        //-- Then
        assertThat(Waiter.triggerActionEarlier(game)).isFalse();
    }

    @Test
    void shouldWaiterForbidTwoRegistrations() {
        final int duration = 5;
        //-- Given
        Game game = new Game(0, Collections.singletonList(testMember), testChannel);

        DummyTurn action = new DummyTurn(duration, PlayerTurn.NONE);
        DummyTurn action1 = new DummyTurn(duration, PlayerTurn.NONE);
        //-- When
        Waiter.register(game, action);
        //-- Then
        assertThatThrownBy(() -> Waiter.register(game, action1))
                .isInstanceOf(InvalidRequestStateException.class)
                .hasMessage("two actions registered for the same game");
    }
}
