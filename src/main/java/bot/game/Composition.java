package bot.game;

import bot.game.roles.EnhanceRoleType;
import bot.game.roles.Role;
import bot.game.roles.WitchRole;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.List;

public class Composition {
    private final List<EnhanceRoleType> stack = new ArrayList<>();

    /**
     * Create a new composition performing operations depending on the game size
     * To add new role you need to precise added card to the stack and removed one
     * If stack overlap the number of player, may remove before draw
     *
     * @param gameSize number of role to assign (in fact can be more than that to add randomness on number of present roles)
     */
    public Composition(int gameSize) {
        int numberWerewolf = (int) Math.floor(0.3 * (gameSize));
        int numberVillager = (gameSize) - numberWerewolf;
        for (int i = 0; i < numberWerewolf; i++) {
            stack.add(EnhanceRoleType.simpleWolf);
        }
        for (int i = 0; i < numberVillager; i++) {
            stack.add(EnhanceRoleType.simpleVillager);
        }
        if (gameSize > 4) {
            stack.remove(EnhanceRoleType.simpleVillager);
            stack.add(EnhanceRoleType.seer);
            stack.remove(EnhanceRoleType.simpleVillager);
            stack.add(EnhanceRoleType.witch);
        }
    }

    /**
     * Return a random role which is in the remaining pool of role
     *
     * @param m member to assign
     * @return a new random type role instance
     */
    public Role getARole(Member m) {
        EnhanceRoleType realRole = drawARole();
        if (realRole.equals(EnhanceRoleType.witch)) return new WitchRole(m);
        return new Role(m, realRole);
    }

    public EnhanceRoleType drawARole() {
        double rng = Math.random();
        int position = (int) Math.floor(rng * stack.size());
        return stack.remove(position);
    }

    /**
     * Use for balance test
     *
     * @return the roles in the stack
     */
    @Override
    public String toString() {
        return stack.toString();
    }


}
