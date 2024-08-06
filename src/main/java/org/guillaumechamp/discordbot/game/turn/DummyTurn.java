package org.guillaumechamp.discordbot.game.turn;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.roles.ActionType;
import org.guillaumechamp.discordbot.game.roles.EnhanceRoleType;
import org.guillaumechamp.discordbot.game.roles.Role;
import org.guillaumechamp.discordbot.io.UserIntendedException;

import java.util.Collections;
import java.util.List;

public class DummyTurn extends AbstractTurn {
    public DummyTurn(int durationInSecond, PlayerTurn replacedTurn) {
        super(EnhanceRoleType.NONE, null, null);
        this.durationInSecond = durationInSecond;
        this.playerTurn = replacedTurn;
    }

    @Override
    public void handleAction(Member author, Member target, ActionType action) throws UserIntendedException {
        throw new UserIntendedException(UserIntendedException.EXCEPTION_MESSAGE_ACTION_NOT_ALLOWED);
    }

    @Override
    public List<Role> getResult() {
        return Collections.emptyList();
    }
}
