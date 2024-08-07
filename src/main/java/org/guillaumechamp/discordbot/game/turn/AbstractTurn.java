package org.guillaumechamp.discordbot.game.turn;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.roles.ActionType;
import org.guillaumechamp.discordbot.game.roles.RoleType;
import org.guillaumechamp.discordbot.game.roles.PlayerData;
import org.guillaumechamp.discordbot.game.roles.PlayerDataUtil;
import org.guillaumechamp.discordbot.io.UserIntendedException;

import java.util.List;

import static org.guillaumechamp.discordbot.io.UserIntendedException.*;

/**
 * Base class for all asynchronous actions
 */
public abstract class AbstractTurn {
    protected final RoleType roleTypeAllowedToAct;
    protected final List<PlayerData> remainingPlayersList;
    private boolean isActive = true;
    protected final List<ActionType> authorizedActions;
    protected int durationInSecond = 60;
    protected PlayerTurn playerTurn = PlayerTurn.NONE;

    protected AbstractTurn(RoleType roleTypeAllowedToAct, List<PlayerData> remainingPlayersList, List<ActionType> authorizedActions) {
        this.roleTypeAllowedToAct = roleTypeAllowedToAct;
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
     * @throws UserIntendedException an exception that explain why nothing happen
     */
    public void handleAction(Member author, Member target, ActionType action) throws UserIntendedException {
        if (PlayerDataUtil.isMemberNotIn(remainingPlayersList, author)) {
            throw new UserIntendedException(EXCEPTION_MESSAGE_AUTHOR_NOT_IN_THE_GAME);
        }
        if (!PlayerDataUtil.isMemberA(remainingPlayersList, author, this.roleTypeAllowedToAct)) {
            throw new UserIntendedException(EXCEPTION_MESSAGE_ACTION_NOT_ALLOWED);
        }
        if (target != null && PlayerDataUtil.isMemberNotIn(remainingPlayersList, target)) {
            throw new UserIntendedException(EXCEPTION_MESSAGE_TARGET_NOT_IN_THE_GAME);
        }
        if (isExpired()) {
            throw new UserIntendedException(EXCEPTION_MESSAGE_ACTION_EXPIRED);
        }
        if (!authorizedActions.contains(action)) {
            throw new UserIntendedException(EXCEPTION_MESSAGE_WRONG_COMMAND);
        }
    }

    public abstract List<PlayerData> getResult() throws UserIntendedException;

    public PlayerTurn getPlayerTurn() {
        return playerTurn;
    }

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
