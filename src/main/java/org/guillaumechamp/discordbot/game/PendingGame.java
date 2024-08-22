package org.guillaumechamp.discordbot.game;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.guillaumechamp.discordbot.io.manager.ChannelUtils;
import org.guillaumechamp.discordbot.io.UserIntendedException;

import java.util.ArrayList;
import java.util.List;

public class PendingGame implements GameInterface {
    private final int id;
    private final int limit;
    private boolean isExpired = false;
    private final List<Member> players;
    private final TextChannel channel;

    public PendingGame(Guild server, int id, int limit) {
        if (server==null){
            throw new IllegalArgumentException("Pending game must be attached to a guild");
        }
        this.players = new ArrayList<>();
        this.id = id;
        this.limit = limit;
        channel = ChannelUtils.createChannelForAGuild(server, ChannelUtils.getGameChannelNameByIndexAndStatus(id, true));
        ChannelUtils.sendMessageToAChannel(channel,"A new game will start !\n/join " + id + " to join it (" + limit + " players max )");
    }

    public void addPlayer(Member member) throws UserIntendedException {
        if (isExpired) {
            throw new UserIntendedException(UserIntendedException.EXCEPTION_MESSAGE_GAME_ALREADY_STARTED);
        }
        if (players.size() >= limit) {
            // in production this case is extremely because that mean that two addPlayer request are processed at the same time
            // (when game is started this pending game is dereferenced)
            throw new UserIntendedException(UserIntendedException.EXCEPTION_MESSAGE_MAX_NUMBER_OF_PLAYER_REACHED);
        }
        players.add(member);
        ChannelUtils.sendMessageToAChannel(channel,member.getEffectiveName() + " join the game");
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
            throw new UserIntendedException(UserIntendedException.EXCEPTION_MESSAGE_GAME_ALREADY_STARTED);
        }
        isExpired = true;
        Game newGame = new Game(id, players, channel);
        newGame.playNextAction();
        return newGame;
    }

}
