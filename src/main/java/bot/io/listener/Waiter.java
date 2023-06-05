package bot.io.listener;

import bot.game.Game;
import bot.game.game.mechanism.Action;
import bot.game.game.mechanism.ParallelAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Waiter implements Runnable{
    private static Waiter waiter;
    private final HashMap<ParallelAction, Game> pendingList;
    private final HashMap<ParallelAction, LocalDateTime> watchList;
    private Waiter(){
        pendingList = new HashMap<>(3);
        watchList = new HashMap<>(3);
    }

    @Override
    public void run() {
        while (!pendingList.isEmpty()){
            LocalDateTime now = LocalDateTime.now();
            List<ParallelAction> toTerminate = new ArrayList<>(3);
            watchList.forEach((key, value)->{
                if (now.isAfter(value)){
                    toTerminate.add(key);
                }
            });
            toTerminate.forEach(key->{
                //pendingList.get(key)();
                key.terminate();
                pendingList.remove(key);
                watchList.remove(key);
            });
        }

    }

    public static void register(Game game, ParallelAction action){
        if (waiter==null){
            waiter = new Waiter();
        }
        if (waiter.pendingList.containsValue(game)){
            throw new WaiterException("WARNING : "+game.getGameId()+" has tried to register an action but another is still alive");
        }
        waiter.pendingList.put(action,game);
        waiter.watchList.put(action,LocalDateTime.now().plusSeconds(60));
        new Thread(waiter).start();
    }
}
