package org.guillaumechamp.discordbot.game.roles;

import net.dv8tion.jda.api.entities.Member;

public class WitchPlayerData extends PlayerData {
    private boolean healingAvailable = true;
    private boolean killingAvailable = true;

    public WitchPlayerData(Member owner) {
        super(owner, RoleType.WITCH);
    }

    public boolean isHealingAvailable() {
        return healingAvailable;
    }

    public boolean isKillingAvailable() {
        return killingAvailable;
    }

    public void useHeal(){
        healingAvailable = false;
    }
    public void useKill(){
        killingAvailable = false;
    }
}
