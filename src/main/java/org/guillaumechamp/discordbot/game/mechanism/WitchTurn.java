package org.guillaumechamp.discordbot.game.mechanism;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.roles.EnhanceRoleType;
import org.guillaumechamp.discordbot.game.roles.Role;
import org.guillaumechamp.discordbot.game.roles.RoleManagement;
import org.guillaumechamp.discordbot.io.UserIntendedException;

import java.util.ArrayList;
import java.util.List;

public class WitchTurn extends AbstractTurn {
    public static final int DEFAULT_DURATION = 15;
    ArrayList<Role> deadPerson = new ArrayList<>();

    public WitchTurn(EnhanceRoleType author, List<Role> roles, Role eliminated) {
        super(author, roles, List.of(ActionType.WITCH_KILL, ActionType.WITCH_SAVE));
        this.durationInSecond = DEFAULT_DURATION;
        deadPerson.add(eliminated);
    }

    @Override
    public void handleAction(Member author, Member target, ActionType action) throws UserIntendedException {
        super.handleAction(author, target, action);
        if (action.equals(ActionType.WITCH_SAVE)) {
            deadPerson.remove(0);
            this.terminate();
        } else if (action.equals(ActionType.WITCH_KILL)) {
            deadPerson.add(RoleManagement.getRoleByMemberId(remainingPlayersList, target.getId()));
            this.terminate();
        }
    }

    @Override
    public ArrayList<Role> getResult() {
        return deadPerson;
    }
}
