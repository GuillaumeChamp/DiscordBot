package org.guillaumechamp.discordbot.io;

import org.awaitility.Awaitility;
import org.guillaumechamp.discordbot.game.Game;
import org.guillaumechamp.discordbot.io.listener.Waiter;
import org.guillaumechamp.discordbot.util.AbstractDiscordTest;
import org.guillaumechamp.discordbot.util.TestTurn;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

class WaiterTest extends AbstractDiscordTest {
    @Test
    void shouldWaiterAwakeGameProperly() {
        final int duration = 4;
        //-- Given
        Game game = new Game(0, Collections.singletonList(testMember), testChannel);
        TestTurn action = new TestTurn(duration);
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

        TestTurn action = new TestTurn(duration);
        TestTurn action1 = new TestTurn(duration);
        //-- When
        Waiter.register(game, action);
        Waiter.register(game1, action1);
        //-- Then
        Awaitility.await()
                .atLeast(duration - 1, TimeUnit.SECONDS)
                .atMost(duration + 1, TimeUnit.SECONDS)
                .until(() -> action.isExpired() && action1.isExpired());
    }

}
