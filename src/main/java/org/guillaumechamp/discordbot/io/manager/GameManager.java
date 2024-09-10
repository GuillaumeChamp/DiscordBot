package org.guillaumechamp.discordbot.io.manager;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.collections4.list.FixedSizeList;
import org.guillaumechamp.discordbot.game.GameInterface;
import org.guillaumechamp.discordbot.game.PendingGame;
import org.guillaumechamp.discordbot.game.turn.AbstractTurn;
import org.guillaumechamp.discordbot.game.roles.ActionType;
import org.guillaumechamp.discordbot.io.UserIntendedException;

import java.util.Arrays;
import java.util.List;

/**
 * Act as interface to map a guild (i.e. a server) to a set of game
 */
public class GameManager {
    public static final int MAX_GAME_PER_GUILD = 3;
    private final Guild server;
    private final List<GameInterface> gameList = FixedSizeList.fixedSizeList(Arrays.asList(new GameInterface[MAX_GAME_PER_GUILD]));
    private final List<AbstractTurn> currentAction = FixedSizeList.fixedSizeList(Arrays.asList(new AbstractTurn[MAX_GAME_PER_GUILD]));

    public GameManager(Guild server) {
        this.server = server;
    }

    /**
     * Create a new game
     *
     * @param maxPlayerSize max size of the game
     */
    public void createGame(Integer maxPlayerSize) throws UserIntendedException {
        if (maxPlayerSize==null){
            maxPlayerSize = 512;
        }
        for (int i = 0; i < MAX_GAME_PER_GUILD; i++)
            if (gameList.get(i) == null) {
                gameList.set(i, new PendingGame(server, i, maxPlayerSize));
                return;
            }
        throw new UserIntendedException(UserIntendedException.EXCEPTION_MESSAGE_MAX_NUMBER_OF_GAME_REACHED);
    }

    /**
     * Add a player to a specify party
     *
     * @param member person to add
     * @param gameId id of the party
     * @throws UserIntendedException if unable to add
     */
    public void addPlayer(Member member, Integer gameId) throws UserIntendedException {
        if (gameId==null){
            gameId=0;
        }
        if (gameList.get(gameId) == null) {
            throw new UserIntendedException(UserIntendedException.EXCEPTION_MESSAGE_GAME_DOES_NOT_EXIST);
        }
        gameList.get(gameId).addPlayer(member);
    }

    /**
     * @param gameId of the game to launch
     * @throws UserIntendedException if unable to start
     */
    public void start(Integer gameId) throws UserIntendedException {
        if (gameId==null){
            gameId=0;
        }
        if (gameList.get(gameId) == null) {
            throw new UserIntendedException(UserIntendedException.EXCEPTION_MESSAGE_GAME_DOES_NOT_EXIST);
        }
        gameList.set(gameId, gameList.get(gameId).startGame());
    }

    /**
     * Stop a game
     *
     * @param gameId index og the game to stop
     *                  do not check if game exist
     */
    public void stop(Integer gameId) throws UserIntendedException {
        if (gameId==null){
            gameId=0;
        }
        if (gameList.get(gameId)==null){
            throw new UserIntendedException(UserIntendedException.EXCEPTION_MESSAGE_GAME_DOES_NOT_EXIST);
        }
        gameList.get(gameId).terminate();
        gameList.set(gameId, null);
    }

    public void transferCommandToTheAction(int gameIndex, Member member, Member target, String action) throws UserIntendedException {
        if (gameList.get(gameIndex) == null) {
            throw new UserIntendedException(UserIntendedException.EXCEPTION_MESSAGE_GAME_DOES_NOT_EXIST);
        }
        currentAction.get(gameIndex).handleAction(member, target, ActionType.stringToActionType(action));
    }

    public void registerAction(int index, AbstractTurn vote) {
        currentAction.set(index, vote);
    }

}
