package bot.io.listener;

import bot.game.Game;
import bot.game.game.mechanism.ParallelAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

public class Waiter implements Runnable {
    private static Waiter waiter;
    private final HashMap<ParallelAction, Game> pendingList;
    private final ConcurrentHashMap<ParallelAction, LocalDateTime> watchList;

    private Waiter() {
        pendingList = new HashMap<>(3);
        watchList = new ConcurrentHashMap<>(3);
    }

    @Override
    public void run() {
        while (!pendingList.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            List<ParallelAction> toTerminate = new ArrayList<>(3);
            watchList.forEach((key, value) -> {
                if (now.isAfter(value)) {
                    toTerminate.add(key);
                }
            });
            toTerminate.forEach(key -> {
                //pendingList.get(key)();
                key.terminate();
                pendingList.remove(key);
                watchList.remove(key);
            });
        }

    }

    public static void register(Game game, ParallelAction action) {
        if (waiter == null) {
            waiter = new Waiter();
        }
        if (waiter.pendingList.containsValue(game)) {
            StringJoiner message = new StringJoiner(" ")
                    .add("WARNING :")
                    .add(game.getGameId().toString())
                    .add("has tried to register an action but another is still alive");
            throw new WaiterException(message.toString());
        }
        waiter.pendingList.put(action, game);
        waiter.watchList.put(action, LocalDateTime.now().plusSeconds(action.getDuration()));
        new Thread(waiter).start();
    }
}
