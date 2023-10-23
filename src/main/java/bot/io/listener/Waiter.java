package bot.io.listener;

import bot.game.Game;
import bot.game.mechanism.ParallelAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

public class Waiter implements Runnable {
    Thread thread;
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
                key.terminate();
                if (key.isActive()) {
                    Game game = pendingList.remove(key);
                    game.playNextAction();
                }
                watchList.remove(key);
            });
        }
        waiter.thread.interrupt();
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
        manageThread();
    }

    /**
     * Use to remove an action from the watch of the waiter
     *
     * @return true if successful, false if action not found
     */
    public static boolean removeAction(Game source, ParallelAction action) {
        if (waiter == null) {
            return false;
        }
        if (waiter.pendingList.containsKey(action)) {
            waiter.pendingList.remove(action, source);
            waiter.watchList.remove(action);
            action.terminate();
            return true;
        }
        return false;
    }

    private static void manageThread() {
        Thread myThread = waiter.thread;
        if (myThread == null) {
            myThread = new Thread(waiter);
        } else if (!myThread.isAlive()) {
            if (myThread.isInterrupted()) {
                myThread = new Thread(waiter);
            }
        }
        myThread.start();
    }
}
