package org.guillaumechamp.discordbot.game.turn;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.roles.ActionType;
import org.guillaumechamp.discordbot.game.roles.RoleType;
import org.guillaumechamp.discordbot.game.roles.PlayerData;
import org.guillaumechamp.discordbot.game.roles.PlayerDataUtil;
import org.guillaumechamp.discordbot.io.UserIntendedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeerTurn extends AbstractTurn {
    public static final int DEFAULT_DURATION = 15;
    private final List<PlayerData> result = new ArrayList<>();

    public SeerTurn(List<PlayerData> roles) {
        super(RoleType.SEER, roles, Collections.singletonList(ActionType.SEER_SEE));
        this.durationInSecond = DEFAULT_DURATION;
        this.playerTurn = PlayerTurn.SEER;
    }

    @Override
    public void handleAction(Member author, Member target, ActionType action) throws UserIntendedException {
        super.handleAction(author, target, action);
        result.add(PlayerDataUtil.getRoleByMemberId(remainingPlayersList, target.getId()));
        this.terminate();
    }

    @Override
    public List<PlayerData> getResult() throws UserIntendedException {
        if (result.isEmpty()) {
            throw new UserIntendedException(UserIntendedException.EXCEPTION_MESSAGE_SEER_NO_SPEC);
        }
        return result;
    }
}
