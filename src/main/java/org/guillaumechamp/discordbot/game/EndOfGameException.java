package org.guillaumechamp.discordbot.game;

import org.guillaumechamp.discordbot.game.roles.PlayerData;

import java.util.Collection;

public class EndOfGameException extends IllegalStateException {
    private final transient Collection<PlayerData> winningTeam;

    public EndOfGameException(String message, Collection<PlayerData> composition) {
        super(message);
        this.winningTeam = composition;
    }

    @Override
    public String getMessage() {
        return "End Of Game !\n" + super.getMessage() + "\nRemaining roles : " + winningTeam.toString();
    }
}
