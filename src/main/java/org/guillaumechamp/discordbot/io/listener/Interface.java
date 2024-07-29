package org.guillaumechamp.discordbot.io.listener;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.collections4.list.FixedSizeList;
import org.guillaumechamp.discordbot.game.GameInterface;
import org.guillaumechamp.discordbot.game.PendingGame;
import org.guillaumechamp.discordbot.game.mechanism.AbstractTurn;
import org.guillaumechamp.discordbot.game.mechanism.ActionType;
import org.guillaumechamp.discordbot.io.UserIntendedException;

import java.util.Arrays;
import java.util.List;

/**
 * Act as interface to map a guild (i.e. a server) to a set of game
 */
public class Interface {
    public static final Integer MAX_GAME_PER_GUILD = 3;
    Guild server;
    List<GameInterface> gameList = FixedSizeList.fixedSizeList(Arrays.asList(new GameInterface[MAX_GAME_PER_GUILD]));
    List<AbstractTurn> currentAction = FixedSizeList.fixedSizeList(Arrays.asList(new AbstractTurn[MAX_GAME_PER_GUILD]));

    public Interface(Guild server) {
        this.server = server;
    }

    /**
     * Create a new game
     *
     * @param maxPlayerSize max size of the game
     */
    public void createGame(int maxPlayerSize) throws UserIntendedException {
        for (int i = 0; i < MAX_GAME_PER_GUILD; i++)
            if (gameList.get(i) == null) {
                gameList.set(i, new PendingGame(server, i, maxPlayerSize));
                return;
            }
        throw new UserIntendedException("max number of game reached");
    }

    /**
     * Add a player to a specify party
     *
     * @param member person to add
     * @param gameId id of the party
     * @throws UserIntendedException if unable to add
     */
    public void addPlayer(Member member, int gameId) throws UserIntendedException {
        if (member == null) {
            throw new UserIntendedException("Unable to find who talk");
        }
        if (gameList.get(gameId) == null) {
            throw new UserIntendedException("The game not exist yet use /create to create it");
        }
        gameList.get(gameId).addPlayer(member);
    }

    /**
     * @param id of the game to launch
     * @throws UserIntendedException if unable to start
     */
    public void start(int id) throws UserIntendedException {
        if (gameList.get(id) == null) {
            throw new UserIntendedException("Create the game before using /create");
        }
        gameList.set(id, gameList.get(id).startGame());
    }

    /**
     * Stop a game
     *
     * @param gameIndex index og the game to stop
     *                  do not check if game exist
     */
    public void stop(int gameIndex) {
        gameList.set(gameIndex, null);
    }

    public void transferCommandToTheAction(int gameIndex, Member member, Member target, String action) throws UserIntendedException {
        if (gameList.get(gameIndex) == null) {
            throw new UserIntendedException("This game is not active");
        }
        currentAction.get(gameIndex).handleAction(member, target, ActionType.stringToActionType(action));
    }

    public void registerAction(int index, AbstractTurn vote) {
        currentAction.set(index, vote);
    }

}
