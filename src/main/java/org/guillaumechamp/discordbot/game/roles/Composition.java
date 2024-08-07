package org.guillaumechamp.discordbot.game.roles;

import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.List;

public class Composition {
    private final List<RoleType> stack = new ArrayList<>();

    /**
     * Create a new composition performing operations depending on the game size
     * To add new roleType you need to precise added card to the stack and removed one
     * If stack overlap the number of player, may remove before draw
     *
     * @param gameSize number of roleType to assign (in fact can be more than that to add randomness on number of present roles)
     */
    private Composition(int gameSize) {
        int numberWerewolf = (int) Math.floor(0.3 * (gameSize));
        int numberVillager = (gameSize) - numberWerewolf;
        for (int i = 0; i < numberWerewolf; i++) {
            stack.add(RoleType.SIMPLE_WOLF);
        }
        for (int i = 0; i < numberVillager; i++) {
            stack.add(RoleType.SIMPLE_VILLAGER);
        }
        if (gameSize > 4) {
            stack.remove(RoleType.SIMPLE_VILLAGER);
            stack.add(RoleType.SEER);
            stack.remove(RoleType.SIMPLE_VILLAGER);
            stack.add(RoleType.WITCH);
        }
    }

    /**
     * Return a random roleType which is in the remaining pool of roleType
     *
     * @param owner member to assign
     * @return a new random type roleType instance
     */
    private PlayerData generatePlayerData(Member owner) {
        RoleType roleType = drawARole();
        if (roleType.equals(RoleType.WITCH)) {
            return new WitchPlayerData(owner);
        }
        return new PlayerData(owner, roleType);
    }

    private RoleType drawARole() {
        double rng = Math.random();
        int position = (int) Math.floor(rng * stack.size());
        return stack.remove(position);
    }

    public static List<PlayerData> assignRoles(List<Member> members) {
        int size = members.size();
        Composition compo = new Composition(size);
        List<PlayerData> roleList = new ArrayList<>(size);

        for (Member member : members) {
            roleList.add(compo.generatePlayerData(member));
        }
        return roleList;
    }

}
