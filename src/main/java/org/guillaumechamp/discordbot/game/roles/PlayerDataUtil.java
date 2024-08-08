package org.guillaumechamp.discordbot.game.roles;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.EndOfGameException;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PlayerDataUtil {

    private PlayerDataUtil() {
    }

    /**
     * Check if the game should end
     * Winning conditions are :
     * 1- no remaining werewolf and solo (village win)
     * 2- same number of wolf than villager and no solo (werewolf win)
     * 3- One remaining player (solo win)
     *
     * @param remainingPlayers all remaining roles
     * @throws EndOfGameException if the game is over
     */
    public static void checkWin(Collection<PlayerData> remainingPlayers) throws EndOfGameException {
        int numberVillager = 0;
        int numberWerewolf = 0;
        int numberSolo = 0;
        for (PlayerData r : remainingPlayers) {
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
        if (numberWerewolf + numberSolo == 0) {
            throw new EndOfGameException("Villager win !", remainingPlayers);
        }
        if (numberVillager <= numberWerewolf && numberSolo == 0) {
            throw new EndOfGameException("Werewolf win !", remainingPlayers);
        }
        if (remainingPlayers.size() == 1) {
            throw new EndOfGameException("A player win alone !", remainingPlayers);
        }
    }

    /**
     * Check if a member is in a collection of players
     *
     * @param players      nullable, player's list
     * @param memberToFind nullable, the person to find
     * @return true if the player is in the collection, else false
     */
    public static boolean isMemberNotIn(Collection<PlayerData> players, Member memberToFind) {
        if (players == null || memberToFind == null) {
            //not useless because we use collection
            return true;
        }
        for (PlayerData playerData : players) {
            if (playerData.getId().equals(memberToFind.getId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check the roleType of someone
     *
     * @param players        nullable, player's list
     * @param memberToLookup nullable, the person to lookup
     * @param role           role to check
     * @return true if the member own the given role, or if the member is in list and role is All. False elsewhere
     */
    public static boolean isMemberA(Collection<PlayerData> players, Member memberToLookup, RoleType role) {
        if (players == null || memberToLookup == null) {
            throw new InvalidParameterException("players or member is null");
        }
        for (PlayerData playerData : players) {
            if (playerData.getId().equals(memberToLookup.getId())) {
                if (role == RoleType.ALL) {
                    return true;
                }
                return playerData.getRole() == role;
            }
        }
        throw new InvalidParameterException("player not found");
    }

    /**
     * Check the roleType of someone
     *
     * @param players        nullable, player's list
     * @param memberToLookup nullable, the member to lookup
     * @param side           side
     * @return true if voter is a type
     */
    public static boolean isMemberInThatSide(Collection<PlayerData> players, Member memberToLookup, RoleSide side) {
        if (players == null || memberToLookup == null) {
            //not useless because we use collection
            return false;
        }
        for (PlayerData playerData : players) {
            if (playerData.getId().equals(memberToLookup.getId()))
                return playerData.getType() == side;
        }
        return false;
    }

    /**
     * Check if a roleType is not in a composition
     *
     * @param players nullable, player's list
     * @param role    the role to look for
     * @return true if the role is not in the list, false elsewhere
     */
    public static boolean isRoleNotIn(Collection<PlayerData> players, RoleType role) {
        if (players == null) {
            //not useless because we use collection
            return true;
        }
        for (PlayerData player : players) {
            if (player.getRole() == role) {
                return false;
            }
        }
        return true;
    }

    /**
     * Recover the PlayerData using the member ID
     *
     * @param players  player's list
     * @param memberId the id of a member
     * @return the player data of the member
     * @throws InvalidParameterException if one of the parameter is null or if the member is not in the player list
     * @see Member#getId()
     */
    public static PlayerData getRoleByMemberId(Collection<PlayerData> players, String memberId) throws InvalidParameterException {
        if (players == null || memberId == null) {
            //not useless because we use collection
            throw new InvalidParameterException("null parameter not allowed");
        }
        for (PlayerData player : players) {
            if (player.getId().equals(memberId)) {
                return player;
            }
        }
        throw new InvalidParameterException("This player is not in this list");
    }

    /**
     * Get all member of this side
     *
     * @param players player's list
     * @param side    the side
     * @return All the PlayerData instance belonging to this side. An empty list if none matching or empty player list.
     */
    public static List<Member> getAllMembersBySide(Collection<PlayerData> players, RoleSide side) {
        if (players == null) {
            //not useless because we use collection
            return Collections.emptyList();
        }
        List<Member> matching = new ArrayList<>(players.size() / 2);
        for (PlayerData player : players) {
            if (player.getType() == side) {
                matching.add(player.getOwner());
            }
        }
        return matching;
    }

    /**
     * Use to retrieve a roleType by looking for the enhance roleType type
     *
     * @param players players player's list
     * @param role    the role to look for
     * @return the matching player data
     * @throws InvalidParameterException if the role is not found or if the player's collection is null
     */
    public static PlayerData getPlayerDataByRole(Collection<PlayerData> players, RoleType role) throws InvalidParameterException {
        if (players == null) {
            //not useless because we use collection
            throw new InvalidParameterException("null collection of player not allowed");
        }
        for (PlayerData player : players) {
            if (player.getRole() == role) {
                return player;
            }
        }
        throw new InvalidParameterException("this role is not in the list");
    }

}
