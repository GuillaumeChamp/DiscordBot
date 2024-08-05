package org.guillaumechamp.discordbot.game;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.guillaumechamp.discordbot.io.ChannelManager;
import org.guillaumechamp.discordbot.io.UserIntendedException;

import java.util.ArrayList;

public class PendingGame implements GameInterface {
    private final int id;
    private final int limit;
    private boolean isExpired = false;
    private final ArrayList<Member> players;
    private final TextChannel channel;

    public PendingGame(Guild server, int id, int limit) {
        this.players = new ArrayList<>();
        this.id = id;
        this.limit = limit;
        channel = ChannelManager.createChannelForAGuild(server, ChannelManager.getGameChannelNameByIndexAndStatus(id, true));
        channel.sendMessage("A new game will start !\n/join " + id + " to join it (" + limit + " players max )").queue();
    }

    public void addPlayer(Member member) throws UserIntendedException {
        if (isExpired) {
            throw new UserIntendedException("the game is already started");
        }
        if (players.size() >= limit) {
            throw new UserIntendedException("The game is full");
        }
        players.add(member);
        channel.sendMessage(member.getEffectiveName() + " join the game").queue();
        if (players.size() == limit) {
            this.startGame();
        }
    }

    @Override
    public void terminate() {
        this.isExpired = true;
    }

    public Game startGame() throws UserIntendedException {
        if (isExpired) {
            throw new UserIntendedException("the game is already in progress");
        }
        isExpired = true;
        Game newGame = new Game(id, players, channel);
        newGame.playNextAction();
        return newGame;
    }

}
