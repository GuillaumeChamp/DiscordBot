package org.guillaumechamp.discordbot.game.roles;

import net.dv8tion.jda.api.entities.Member;

public class WitchRole extends Role {
    private boolean healingAvailable = true;
    private boolean killingAvailable = true;

    public WitchRole(Member owner) {
        super(owner, EnhanceRoleType.witch);
    }

    public boolean isHealingAvailable() {
        return healingAvailable;
    }

    public boolean isKillingAvailable() {
        return killingAvailable;
    }

    public void use(String type) {
        if (type.equals("heal")) healingAvailable = false;
        else killingAvailable = false;
    }
}
