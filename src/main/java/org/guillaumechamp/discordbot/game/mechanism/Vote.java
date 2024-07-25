package org.guillaumechamp.discordbot.game.mechanism;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.roles.EnhanceRoleType;
import org.guillaumechamp.discordbot.game.roles.Role;
import org.guillaumechamp.discordbot.game.roles.RoleManagement;
import org.guillaumechamp.discordbot.game.roles.RoleType;
import org.guillaumechamp.discordbot.io.ProcessingException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Vote extends BaseAction {
    public enum VoteType {ALL, WEREWOLF}

    private final Map<Role, String> playerTargetMap = new HashMap<>();
    private final RoleType roleToVote;

    /**
     * Create a new vote
     *
     * @param voteType macro to design who can vote
     * @param roles    all player remaining in the game
     */
    public Vote(VoteType voteType, List<Role> roles) {
        super(EnhanceRoleType.ALL, roles, Collections.singletonList(ActionType.VOTE));
        for (Role role : roles) {
            playerTargetMap.put(role, null);
        }
        this.roleToVote = voteType == VoteType.WEREWOLF ? RoleType.WEREWOLF : null;
    }

    /**
     * Add a vote
     *
     * @param voter  the personne who want to vote
     * @param target the personne for whom it wants to vote
     * @param action must be "vote" or the action will be rejected
     * @throws ProcessingException an exception that explain why nothing happen
     */
    @Override
    public void handleAction(Member voter, Member target, ActionType action) throws ProcessingException {
        super.handleAction(voter, target, action);
        if (target == null) {
            throw new ProcessingException("No player targeted");
        }
        if (RoleType.WEREWOLF.equals(roleToVote) && !RoleManagement.isA(remainingPlayersList, target, RoleType.WEREWOLF)) {
            throw new ProcessingException("You cannot vote");
        }
        playerTargetMap.put(RoleManagement.getRoleByMemberId(remainingPlayersList, voter.getId()), target.getId());
    }

    /**
     * Get the answer of a vote
     *
     * @return the person with the maximum of vote
     * @throws ProcessingException if two player have the same amount of vote, if the target disappear
     */
    public ArrayList<Role> getResult() throws ProcessingException {
        Map<String, Long> numberOfVoteByMember = playerTargetMap
                .values()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(),
                        HashMap::new,
                        Collectors.counting()));
        long maxNumberOfVote = numberOfVoteByMember.values().stream().max(Double::compare).orElse(0L);
        List<String> tiedList = numberOfVoteByMember.entrySet().stream().filter(key -> maxNumberOfVote == key.getValue()).map(Map.Entry::getKey).toList();
        if (tiedList.size() == 1) {
            ArrayList<Role> ans = new ArrayList<>();
            ans.add(RoleManagement.getRoleByMemberId(remainingPlayersList, tiedList.get(0)));
            return ans;
        }
        throw new ProcessingException("The choice is not unanimous no one will be kill");
    }
}
