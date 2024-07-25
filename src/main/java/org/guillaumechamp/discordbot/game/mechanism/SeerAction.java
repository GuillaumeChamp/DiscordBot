package org.guillaumechamp.discordbot.game.mechanism;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.roles.EnhanceRoleType;
import org.guillaumechamp.discordbot.game.roles.Role;
import org.guillaumechamp.discordbot.game.roles.RoleManagement;
import org.guillaumechamp.discordbot.io.ProcessingException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeerAction extends BaseAction {
    private static final int DEFAULT_DURATION = 15;
    private final List<Role> result = new ArrayList<>();

    public SeerAction(List<Role> roles) {
        super(EnhanceRoleType.SEER, roles, Collections.singletonList(ActionType.SEER_SEE));
        this.durationInSecond = DEFAULT_DURATION;
    }

    @Override
    public void handleAction(Member author, Member target, ActionType action) throws ProcessingException {
        super.handleAction(author, target, action);
        result.add(RoleManagement.getRoleByMemberId(remainingPlayersList, target.getId()));
        this.terminate();
    }

    @Override
    public List<Role> getResult() {
        return result;
    }
}
