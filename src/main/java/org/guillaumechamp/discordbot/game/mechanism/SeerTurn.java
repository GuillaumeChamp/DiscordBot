package org.guillaumechamp.discordbot.game.mechanism;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.roles.EnhanceRoleType;
import org.guillaumechamp.discordbot.game.roles.Role;
import org.guillaumechamp.discordbot.game.roles.RoleManagement;
import org.guillaumechamp.discordbot.io.UserIntendedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeerTurn extends AbstractTurn {
    public static final int DEFAULT_DURATION = 15;
    private final List<Role> result = new ArrayList<>();

    public SeerTurn(List<Role> roles) {
        super(EnhanceRoleType.SEER, roles, Collections.singletonList(ActionType.SEER_SEE));
        this.durationInSecond = DEFAULT_DURATION;
        this.playerTurn = PlayerTurn.SEER;
    }

    @Override
    public void handleAction(Member author, Member target, ActionType action) throws UserIntendedException {
        super.handleAction(author, target, action);
        result.add(RoleManagement.getRoleByMemberId(remainingPlayersList, target.getId()));
        this.terminate();
    }

    @Override
    public List<Role> getResult() throws UserIntendedException {
        if (result.isEmpty()) {
            throw new UserIntendedException("You have spec no one");
        }
        return result;
    }
}
