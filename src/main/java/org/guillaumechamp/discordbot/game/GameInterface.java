package org.guillaumechamp.discordbot.game;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.io.UserIntendedException;


public interface GameInterface {
    GameInterface startGame() throws UserIntendedException;

    void addPlayer(Member member) throws UserIntendedException;

}
