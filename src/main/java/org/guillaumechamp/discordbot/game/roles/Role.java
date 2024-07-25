package org.guillaumechamp.discordbot.game.roles;

import net.dv8tion.jda.api.entities.Member;

public class Role {
    Member owner;
    RoleType type;
    EnhanceRoleType realRole;

    public Role(Member owner, EnhanceRoleType realRole) {
        this.owner = owner;
        this.realRole = realRole;
        switch (realRole) {
            case SIMPLE_WOLF:
                type = RoleType.WEREWOLF;
                break;
            default:
                type = RoleType.VILLAGER;
                break;
        }
    }

    public Member getOwner() {
        return owner;
    }

    public String getId() {
        return owner.getId();
    }

    public RoleType getType() {
        return type;
    }

    @Override
    public String toString() {
        return owner.getUser().getName() +
                ", was a " + type;
    }

    public EnhanceRoleType getRealRole() {
        return realRole;
    }
}
