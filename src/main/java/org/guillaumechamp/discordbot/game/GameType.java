package org.guillaumechamp.discordbot.game;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.io.ProcessingException;


public interface GameType {
    GameType startGame() throws ProcessingException;

    void addPlayer(Member member) throws ProcessingException;

    Integer getGameId();
}
