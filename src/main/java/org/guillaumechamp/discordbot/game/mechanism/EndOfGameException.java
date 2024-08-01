package org.guillaumechamp.discordbot.game.mechanism;

import org.guillaumechamp.discordbot.game.roles.Role;

import java.util.Collection;

public class EndOfGameException extends IllegalStateException {
    private final transient Collection<Role> winningTeam;

    public EndOfGameException(String message, Collection<Role> composition) {
        super(message);
        this.winningTeam = composition;
    }

    @Override
    public String getMessage() {
        return "End Of Game !\n" + super.getMessage() + "\nRemaining roles : " + winningTeam.toString();
    }
}
