package org.guillaumechamp.discordbot.io.listener;

import com.sun.jdi.request.InvalidRequestStateException;
import org.guillaumechamp.discordbot.game.Game;
import org.guillaumechamp.discordbot.game.turn.AbstractTurn;
import org.guillaumechamp.discordbot.io.BotLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Service to manage all actions that required to wait a moment before triggering it
 */
public class Waiter {
    private static final Map<Game, ScheduledFuture<?>> registeredActions = new HashMap<>(3);
    private static final ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(3);

    private Waiter() {
    }

    public static void initWaiter() {
        threadPoolExecutor.setRemoveOnCancelPolicy(true);
    }

    public static void register(Game game, AbstractTurn action) {
        if (registeredActions.containsKey(game)) {
            BotLogger.log(BotLogger.FATAL, "two actions registered for the same game");
            throw new InvalidRequestStateException("two actions registered for the same game");
        }
        ScheduledFuture<?> future = threadPoolExecutor.schedule(() -> {
            registeredActions.remove(game);
            action.terminate();
            game.playNextAction();
        }, action.getDuration(), TimeUnit.SECONDS);
        registeredActions.put(game, future);
    }

    /**
     * Use to remove an action from the watch of the waiter
     *
     * @return true if successful, false if action not found
     */
    public static boolean triggerActionEarlier(Game source) {
        ScheduledFuture<?> future = registeredActions.get(source);
        if (future == null) {
            BotLogger.log(BotLogger.WARN, "No action registered for this game");
            return false;
        }
        future.cancel(false);
        registeredActions.remove(source);
        source.playNextAction();
        return true;
    }
}
