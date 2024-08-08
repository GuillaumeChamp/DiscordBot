package org.guillaumechamp.discordbot.game.roles;

import net.dv8tion.jda.api.entities.Member;

public class PlayerData {
    private final Member owner;
    private final RoleSide type;
    private final RoleType roleType;

    public PlayerData(Member owner, RoleType roleType) {
        this.owner = owner;
        this.roleType = roleType;
        switch (roleType) {
            case SIMPLE_WOLF -> type = RoleSide.WEREWOLF;
            case SIMPLE_VILLAGER, SEER, WITCH -> type = RoleSide.VILLAGER;
            default -> type = RoleSide.SOLO;
        }
    }

    public Member getOwner() {
        return owner;
    }

    public String getId() {
        return owner.getId();
    }

    public RoleSide getType() {
        return type;
    }

    @Override
    public String toString() {
        return owner.getUser().getName() +
                ", was a " + type + "("+type+")";
    }

    public RoleType getRole() {
        return roleType;
    }
}
