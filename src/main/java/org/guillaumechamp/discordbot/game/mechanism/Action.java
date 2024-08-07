package org.guillaumechamp.discordbot.game.mechanism;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.roles.EnhanceRoleType;
import org.guillaumechamp.discordbot.game.roles.Role;
import org.guillaumechamp.discordbot.game.roles.RoleManagement;
import org.guillaumechamp.discordbot.io.ProcessingException;

import java.util.ArrayList;
import java.util.List;

public abstract class Action implements ParallelAction {
    protected final EnhanceRoleType author;
    protected final List<Role> roles;
    protected boolean isActive = true;
    protected final ArrayList<String> authorizedActions;
    protected final int durationInSecond;

    protected Action(EnhanceRoleType author, List<Role> roles, int durationInSecond) {
        this.author = author;
        this.roles = roles;
        this.durationInSecond = durationInSecond;
        authorizedActions = new ArrayList<>();
    }

    @Override
    public void handleAction(Member author, Member target, String action) throws ProcessingException {
        if (!RoleManagement.isA(roles, author, this.author))
            throw new ProcessingException("You are not authorized to use this action at this moment");
        if (RoleManagement.isNotIn(roles, author)) throw new ProcessingException("This person is not in the game");
        if (!authorizedActions.contains(action))
            throw new ProcessingException("You can use command now but not this one");
        if (!isActive) throw new ProcessingException("This is too late, this action is no lounger authorized");
    }

    @Override
    public void terminate() {
        isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public Integer getDuration() {
        return durationInSecond;
    }
}
