package bot.game.roles;

import bot.game.mechanism.GameException;
import bot.io.ProcessingException;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoleManagement {

    private RoleManagement() {
    }

    /**
     * Check if the game should end
     * Winning conditions are no remaining werewolf or no remain villager
     *
     * @param roles all remaining roles
     * @throws GameException if the game is over
     */
    public static void checkWin(List<Role> roles) throws GameException {
        int numberVillager = 0;
        int numberWerewolf = 0;
        int numberSolo = 0;
        for (Role r : roles) {
            switch (r.getType()) {
                case werewolf:
                    numberWerewolf++;
                    break;
                case villager:
                    numberVillager++;
                    break;
                default:
                    numberSolo++;
                    break;
            }
        }
        if (numberWerewolf + numberSolo <= 0) throw new GameException("Villager win !", roles);
        if (numberVillager + numberSolo <= 0) throw new GameException("Werewolf win !", roles);
    }

    /**
     * Check if a member is in a game
     *
     * @param roles all remaining roles
     * @param voter the personne we check
     * @return true if the player is in the list
     */
    public static boolean isNotIn(List<Role> roles, Member voter) {
        for (Role r : roles) {
            if (r.getId().equals(voter.getId())) return false;
        }
        return true;
    }

    /**
     * Check the role of someone
     *
     * @param roles list to search in
     * @param voter the personne we search
     * @param type  type to match
     * @return true if voter is a type
     */
    public static boolean isA(List<Role> roles, Member voter, EnhanceRoleType type) {
        for (Role r : roles) {
            if (r.getId().equals(voter.getId())) return r.getRealRole() == type;
        }
        return false;
    }

    /**
     * Check the role of someone
     *
     * @param roles list to search in
     * @param voter the personne we search
     * @param type  types to match
     * @return true if voter is a type
     */
    public static boolean isA(List<Role> roles, Member voter, RoleType[] type) {
        for (Role r : roles) {
            if (r.getId().equals(voter.getId()))
                return Arrays.asList(type).contains(r.getType());
        }
        return false;
    }

    /**
     * Recover the role using the member ID
     *
     * @param roles    the list we want to look in. Usually all remaining player
     * @param memberId User member.getId()
     * @return The role of the member
     * @throws ProcessingException If this member is not in the list (Maybe use isNotIn before)
     */
    public static Role getRoleOf(List<Role> roles, String memberId) throws ProcessingException {
        for (Role r :
                roles) {
            if (r.getId().equals(memberId)) return r;
        }
        throw new ProcessingException("This player is not in");
    }

    /**
     * Get all roles that is of this RoleType. Usually used for vote.
     *
     * @param roles the list we want to look in. Usually all remaining player
     * @param type  the RoleType
     * @return All the Role instance matching this type. An empty ArrayList if no remaining.
     */
    public static List<Member> getAll(List<Role> roles, RoleType type) {
        ArrayList<Member> ans = new ArrayList<>();
        for (Role r :
                roles) {
            if (r.getType() == type) ans.add(r.getOwner());
        }
        return ans;
    }

    /**
     * Use to retrieve a role by looking for the enhance role type
     *
     * @param roles all the list
     * @param type  the enhance role to look for
     * @return the role if found
     * @throws ProcessingException if the role is not found
     */
    public static Role getByRole(List<Role> roles, EnhanceRoleType type) throws ProcessingException {
        for (Role r : roles) {
            if (r.getRealRole() == type) return r;
        }
        throw new ProcessingException("This role is not in the list");
    }

    /**
     * Check if a role is not in a composition
     *
     * @param roles all remaining roles
     * @param type  the role to look for
     * @return true if the role is not in the list
     */
    public static boolean roleIsNotIn(List<Role> roles, EnhanceRoleType type) {
        for (Role r : roles) {
            if (r.getRealRole() == type) return false;
        }
        return true;
    }

}
