package bot.game;

import bot.game.roles.EnhanceRoleType;
import bot.game.roles.Role;
import bot.game.roles.WitchRole;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;

public class Composition {
    int numberWerewolf;
    int numberVillager;
    ArrayList<EnhanceRoleType> stack = new ArrayList<>();

    /**
     * Create a new composition performing operations depending on the game size
     * @param gameSize number of role to assign (in fact can be more than that to add randomness on number of present roles)
     */
    public Composition(int gameSize){
        numberWerewolf = (int) Math.floor(0.27*(gameSize-2));
        numberVillager = (gameSize-2)-numberWerewolf;
        for (int i = 0; i < numberWerewolf; i++) {
            stack.add(EnhanceRoleType.simpleWolf);
        }
        for (int i = 0; i < numberVillager; i++) {
            stack.add(EnhanceRoleType.simpleVillager);
        }
        if (gameSize>4) {
            stack.add(EnhanceRoleType.seer);
            stack.add(EnhanceRoleType.witch);
        }
        else {
            stack.add(EnhanceRoleType.simpleWolf);
            stack.add(EnhanceRoleType.simpleVillager);
        }
    }

    /**
     * Return a random role which is in the remaining pool of role
     * @param m member to assign
     * @return a new random type role instance
     */
    public Role getARole(Member m){
        double rng = Math.random();
        EnhanceRoleType realRole = stack.get((int) Math.floor(rng*stack.size()));
        if (realRole.equals(EnhanceRoleType.witch)) return new WitchRole(m);
        return new Role(m,realRole);
    }

    /**
     * Use for balance test
     * @return the roles in the stack
     */
    @Override
    public String toString() {
        return stack.toString();
    }


}
