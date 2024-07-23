package bot.game;

import bot.io.ChannelManager;
import bot.io.ProcessingException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;

public class PendingGame implements GameType {
    private final int id;
    private final int limit;
    private boolean isStarted = false;
    private final ArrayList<Member> players;
    private final TextChannel channel;

    public PendingGame(Guild server, int id, int limit) {
        this.players = new ArrayList<>();
        this.id = id;
        this.limit = limit;
        channel = ChannelManager.createChannelForAGuild(server, "game" + id);
        channel.sendMessage("A new game will start !\n/join " + id + " to join it (" + limit + " players max )").queue();
    }

    public void addPlayer(Member member) throws ProcessingException {
        if (isStarted) throw new ProcessingException("the game is already start");
        if (players.size() >= limit) throw new ProcessingException("The game is full");
        players.add(member);
        channel.sendMessage(member.getEffectiveName() + " join the game").queue();
        if (players.size() == limit) this.startGame();
    }

    public Game startGame() throws ProcessingException {
        if (isStarted) throw new ProcessingException("the game is already in progress");
        isStarted = true;
        Game newGame = new Game(id, players, channel);
        newGame.playNextAction();
        return newGame;
    }

    public Integer getGameId() {
        return id;
    }

}
