package org.guillaumechamp.discordbot.game.turn;

import net.dv8tion.jda.api.entities.Member;
import org.apache.commons.collections4.CollectionUtils;
import org.guillaumechamp.discordbot.game.roles.*;
import org.guillaumechamp.discordbot.io.UserIntendedException;

import java.util.ArrayList;
import java.util.List;

public class WitchTurn extends AbstractTurn {
    public static final int DEFAULT_DURATION = 15;
    ArrayList<PlayerData> deadPerson = new ArrayList<>();

    public WitchTurn(List<PlayerData> roles, PlayerData eliminated) {
        super(RoleType.WITCH, roles, List.of(ActionType.WITCH_KILL, ActionType.WITCH_SAVE));
        this.durationInSecond = DEFAULT_DURATION;
        if (eliminated!=null){
            deadPerson.add(eliminated);
        }
    }

    @Override
    public void handleAction(Member author, Member target, ActionType action) throws UserIntendedException {
        super.handleAction(author, target, action);
        WitchPlayerData witch = (WitchPlayerData) PlayerDataUtil.getPlayerDataByRole(remainingPlayersList, RoleType.WITCH);
        if (action.equals(ActionType.WITCH_SAVE)) {
            handleSaveAction(witch,target);
        } else if (action.equals(ActionType.WITCH_KILL)) {
            handleKillAction(witch,target);
        }
    }

    private void handleSaveAction(WitchPlayerData witch, Member target) throws UserIntendedException {
        if (!witch.isHealingAvailable()){
            throw new UserIntendedException("You already used this power !");
        }
        if (CollectionUtils.isEmpty(deadPerson)){
            throw new UserIntendedException("You have no one to save !");
        }
        if (deadPerson.stream().noneMatch(role -> role.getOwner() == target)){
            throw new UserIntendedException("This user will not died !");
        }
        witch.useHeal();
        deadPerson.remove(0);
    }

    private void handleKillAction(WitchPlayerData witch, Member target) throws UserIntendedException {
        if (!witch.isKillingAvailable()){
            throw new UserIntendedException("You already used this power !");
        }
        witch.useKill();
        deadPerson.add(PlayerDataUtil.getRoleByMemberId(remainingPlayersList, target.getId()));
    }

    @Override
    public List<PlayerData> getResult() {
        return deadPerson;
    }
}