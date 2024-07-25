package org.guillaumechamp.discordbot.game.mechanism;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.roles.EnhanceRoleType;
import org.guillaumechamp.discordbot.game.roles.Role;
import org.guillaumechamp.discordbot.game.roles.RoleManagement;
import org.guillaumechamp.discordbot.io.ProcessingException;

import java.util.ArrayList;
import java.util.List;

public class WitchAction extends BaseAction {
    private static final int DEFAULT_DURATION = 15;
    ArrayList<Role> deadPerson = new ArrayList<>();

    public WitchAction(EnhanceRoleType author, List<Role> roles, Role eliminated) {
        super(author, roles, List.of("kill", "save"));
        this.durationInSecond = DEFAULT_DURATION;
        deadPerson.add(eliminated);
    }

    @Override
    public void handleAction(Member author, Member target, String action) throws ProcessingException {
        super.handleAction(author, target, action);
        if (action.equals("save")) {
            deadPerson.remove(0);
            this.terminate();
        } else if (action.equals("kill")) {
            deadPerson.add(RoleManagement.getRoleByMemberId(remainingPlayersList, target.getId()));
            this.terminate();
        }
    }

    @Override
    public ArrayList<Role> getResult() throws ProcessingException {
        return deadPerson;

    }
}
