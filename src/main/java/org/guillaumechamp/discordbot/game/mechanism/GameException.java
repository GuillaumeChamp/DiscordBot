package org.guillaumechamp.discordbot.game.mechanism;

import org.guillaumechamp.discordbot.game.roles.Role;
import org.guillaumechamp.discordbot.io.UserIntendedException;

import java.util.List;

public class GameException extends UserIntendedException {
    private final transient List<Role> winningTeam;

    public GameException(String message, List<Role> composition) {
        super(message);
        this.winningTeam = composition;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "\nRemaining roles : " + winningTeam.toString();
    }
}
