package io;

import bot.game.Game;
import bot.io.listener.Waiter;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import util.AbstractDiscordTest;
import util.TestAction;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

class WaiterTest extends AbstractDiscordTest {
    @Test
    void shouldWaiterAwakeGameProperly() {
        final int duration = 10;
        //-- Given
        Game game = new Game(0, Collections.singletonList(testMember), testChannel);
        game.interruptGame();
        TestAction action = new TestAction(duration);
        //-- When
        Waiter.register(game, action);
        //-- Then
        Awaitility.await().atLeast(duration - 1, TimeUnit.SECONDS).atMost(duration + 1, TimeUnit.SECONDS).until(() -> !action.isActive());
    }

    @Test
    void shouldWaiterHoldFewActions() {
        final int duration = 10;
        //-- Given
        Game game = new Game(0, Collections.singletonList(testMember), testChannel);
        game.interruptGame();
        Game game1 = new Game(1, Collections.singletonList(testMember), testChannel);
        game1.interruptGame();

        TestAction action = new TestAction(duration);
        TestAction action1 = new TestAction(duration);
        //-- When
        Waiter.register(game, action);
        Waiter.register(game1, action1);
        //-- Then
        Awaitility.await()
                .atLeast(duration - 1, TimeUnit.SECONDS)
                .atMost(duration + 1, TimeUnit.SECONDS)
                .until(() -> !action.isActive() && !action1.isActive());
    }

}
