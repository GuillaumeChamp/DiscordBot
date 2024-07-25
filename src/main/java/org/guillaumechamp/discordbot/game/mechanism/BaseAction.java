package org.guillaumechamp.discordbot.game.mechanism;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.roles.EnhanceRoleType;
import org.guillaumechamp.discordbot.game.roles.Role;
import org.guillaumechamp.discordbot.game.roles.RoleManagement;
import org.guillaumechamp.discordbot.io.ProcessingException;

import java.util.List;

/**
 * Base class for all asynchronous actions
 */
public abstract class BaseAction {
    protected final EnhanceRoleType roleAllowedToAct;
    protected final List<Role> remainingPlayersList;
    private boolean isActive = true;
    protected final List<String> authorizedActions;
    protected int durationInSecond = 60;

    protected BaseAction(EnhanceRoleType roleAllowedToAct, List<Role> remainingPlayersList, List<String> authorizedActions) {
        this.roleAllowedToAct = roleAllowedToAct;
        this.remainingPlayersList = remainingPlayersList;
        this.authorizedActions = authorizedActions;
    }

    /**
     * Core of action handling.
     * check if the author can perform the given action (i.e. the author is in the game, can perform an action, can perform that action and if it's the right time to do it)
     *
     * @param author the member performing the action
     * @param target the target of the action (can be null)
     * @param action the action
     * @throws ProcessingException an exception that explain why nothing happen
     */
    public void handleAction(Member author, Member target, String action) throws ProcessingException {
        if (RoleManagement.isNotIn(remainingPlayersList, author)) {
            throw new ProcessingException("You are not in the game");
        }
        if (target != null && RoleManagement.isNotIn(remainingPlayersList, target)) {
            throw new ProcessingException("The target is not in the game");
        }
        if (!RoleManagement.isA(remainingPlayersList, author, this.roleAllowedToAct)) {
            throw new ProcessingException("You are not authorized to use this action at this moment");
        }
        if (!isActive) {
            throw new ProcessingException("This is too late, this action is no lounger authorized");
        }
        if (!authorizedActions.contains(action)) {
            throw new ProcessingException("You can use command now but not this one");
        }
    }

    public abstract List<Role> getResult() throws ProcessingException;

    public void terminate() {
        isActive = false;
    }

    public boolean isExpired() {
        return !isActive;
    }

    public Integer getDuration() {
        return durationInSecond;
    }
}
