package org.guillaumechamp.discordbot.game.mechanism;

import org.guillaumechamp.discordbot.game.roles.EnhanceRoleType;
import org.guillaumechamp.discordbot.game.roles.Role;

import java.util.Collections;
import java.util.List;

public class DummyTurn extends AbstractTurn {
    public DummyTurn(int durationInSecond, PlayerTurn replacedTurn) {
        super(EnhanceRoleType.NONE, null, null);
        this.durationInSecond = durationInSecond;
        this.playerTurn = replacedTurn;
    }

    @Override
    public List<Role> getResult() {
        return Collections.emptyList();
    }
}
