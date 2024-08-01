package org.guillaumechamp.discordbot.game.roles;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.mechanism.EndOfGameException;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RoleManagement {

    private RoleManagement() {
    }

    /**
     * Check if the game should end
     * Winning conditions are no remaining werewolf or no remain villager
     *
     * @param roles all remaining roles
     * @throws EndOfGameException if the game is over
     */
    public static void checkWin(Collection<Role> roles) throws EndOfGameException {
        int numberVillager = 0;
        int numberWerewolf = 0;
        int numberSolo = 0;
        for (Role r : roles) {
            switch (r.getType()) {
                case WEREWOLF:
                    numberWerewolf++;
                    break;
                case VILLAGER:
                    numberVillager++;
                    break;
                default:
                    numberSolo++;
                    break;
            }
        }
        if (numberWerewolf + numberSolo <= 0) throw new EndOfGameException("Villager win !", roles);
        if (numberVillager + numberSolo <= 0) throw new EndOfGameException("Werewolf win !", roles);
    }

    /**
     * Check if a member is in a game
     *
     * @param roles all remaining roles
     * @param voter the personne we check
     * @return true if the player is in the list
     */
    public static boolean isNotIn(Collection<Role> roles, Member voter) {
        for (Role r : roles) {
            if (r.getId().equals(voter.getId())) return false;
        }
        return true;
    }

    /**
     * Check the role of someone
     *
     * @param roles  list to search in
     * @param target the person we search
     * @param type   type to match
     * @return true if target is a type
     */
    public static boolean isA(Collection<Role> roles, Member target, EnhanceRoleType type) {
        for (Role r : roles) {
            if (r.getId().equals(target.getId())) {
                if (type == EnhanceRoleType.ALL) {
                    return true;
                }
                return r.getRealRole() == type;
            }
        }
        return false;
    }

    /**
     * Check the role of someone
     *
     * @param roles list to search in
     * @param voter the person we search
     * @param type  types to match
     * @return true if voter is a type
     */
    public static boolean isA(Collection<Role> roles, Member voter, RoleType type) {
        for (Role r : roles) {
            if (r.getId().equals(voter.getId()))
                return r.getType() == type;
        }
        return false;
    }

    /**
     * Recover the role using the member ID
     *
     * @param roles    the list we want to look in. Usually all remaining player
     * @param memberId User member.getId()
     * @return The role of the member
     * @throws InvalidParameterException If this member is not in the list (Maybe use isNotIn before)
     */
    public static Role getRoleByMemberId(Collection<Role> roles, String memberId) throws InvalidParameterException {
        for (Role r : roles) {
            if (r.getId().equals(memberId)) return r;
        }
        throw new InvalidParameterException("This player is not in this list");
    }

    /**
     * Get all roles that is of this RoleType. Usually used for vote.
     *
     * @param roles the list we want to look in. Usually all remaining player
     * @param type  the RoleType
     * @return All the Role instance matching this type. An empty ArrayList if no remaining.
     */
    public static List<Member> getAllByRoleType(Collection<Role> roles, RoleType type) {
        List<Member> ans = new ArrayList<>();
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
     * @throws InvalidParameterException if the role is not found
     */
    public static Role getByRole(Collection<Role> roles, EnhanceRoleType type) throws InvalidParameterException {
        for (Role r : roles) {
            if (r.getRealRole() == type) return r;
        }
        throw new InvalidParameterException("This role is not in the list");
    }

    /**
     * Check if a role is not in a composition
     *
     * @param roles all remaining roles
     * @param type  the role to look for
     * @return true if the role is not in the list
     */
    public static boolean roleIsNotIn(Collection<Role> roles, EnhanceRoleType type) {
        for (Role r : roles) {
            if (r.getRealRole() == type) return false;
        }
        return true;
    }

}
