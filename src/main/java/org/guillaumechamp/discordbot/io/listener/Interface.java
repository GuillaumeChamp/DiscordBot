package org.guillaumechamp.discordbot.io.listener;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.GameType;
import org.guillaumechamp.discordbot.game.PendingGame;
import org.guillaumechamp.discordbot.game.mechanism.ParallelAction;
import org.guillaumechamp.discordbot.io.ProcessingException;

/**
 * This class is an interface between the game and the bot
 */
public class Interface {
    Guild server;
    GameType[] pendingGames;
    ParallelAction[] votes = new ParallelAction[3];

    //Singleton
    public Interface(Guild server) {
        this.server = server;
        pendingGames = new GameType[]{null, null, null};
    }

    /**
     * Create a new game
     *
     * @param limit max size of the game
     */
    public void createGame(int limit) throws ProcessingException {
        for (int i = 0; i < 3; i++)
            if (pendingGames[i] == null) {
                pendingGames[i] = new PendingGame(server, i, limit);
                return;
            }
        throw new ProcessingException("max number of game reached");
    }

    /**
     * Add a player to a specify party
     *
     * @param member personne to add
     * @param id     id of the party
     * @throws ProcessingException if unable to add
     */
    public void addPlayer(Member member, int id) throws ProcessingException {
        if (member == null) throw new ProcessingException("Unable to find who talk");
        if (pendingGames[id] == null) throw new ProcessingException("The game not exist yet use /create to create it");
        pendingGames[id].addPlayer(member);
    }

    /**
     * @param id of the game to launch
     * @throws ProcessingException if unable to start
     */
    public void start(int id) throws ProcessingException {
        if (pendingGames[id] == null) throw new ProcessingException("Create the game before using /create");
        pendingGames[id] = pendingGames[id].startGame();

    }

    public void terminateGame(GameType game) {
        pendingGames[game.getGameId()] = null;
    }

    public void stop(int option) {
        pendingGames[option] = null;
    }

    public void performAction(int gameIndex, Member member, Member target, String action) throws ProcessingException {
        if (pendingGames[gameIndex] == null) throw new ProcessingException("This game is not active");
        votes[gameIndex].handleAction(member, target, action);
    }

    public void registerAction(int index, ParallelAction vote) {
        votes[index] = vote;
    }

}
