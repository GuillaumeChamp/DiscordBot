package org.guillaumechamp.discordbot.game.turn;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.roles.*;
import org.guillaumechamp.discordbot.io.UserIntendedException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Vote extends AbstractTurn {
    private final Map<PlayerData, String> playerTargetMap = new HashMap<>();

    /**
     * Create a new vote
     *
     * @param voteType macro to design who can vote
     * @param roles    all player remaining in the game
     */
    public Vote(PlayerTurn voteType, List<PlayerData> roles) {
        super(RoleType.ALL, roles, Collections.singletonList(ActionType.VOTE));
        for (PlayerData role : roles) {
            playerTargetMap.put(role, null);
        }
        if (voteType!=PlayerTurn.VILLAGE_VOTE && voteType!=PlayerTurn.WOLF_VOTE){
            throw new IllegalArgumentException("Vote can only handle WOLF_VOTE or VILLAGE_VOTE");
        }
        this.playerTurn = voteType;

    }

    /**
     * Add a vote
     *
     * @param voter  the personne who want to vote
     * @param target the personne for whom it wants to vote
     * @param action must be "vote" or the action will be rejected
     * @throws UserIntendedException an exception that explain why nothing happen
     */
    @Override
    public void handleAction(Member voter, Member target, ActionType action) throws UserIntendedException {
        super.handleAction(voter, target, action);
        if (target == null) {
            throw new UserIntendedException("No player targeted");
        }
        if (PlayerTurn.WOLF_VOTE.equals(playerTurn) && !PlayerDataUtil.isMemberInThatSide(remainingPlayersList, target, RoleSide.WEREWOLF)) {
            throw new UserIntendedException("You cannot vote");
        }
        playerTargetMap.put(PlayerDataUtil.getRoleByMemberId(remainingPlayersList, voter.getId()), target.getId());
    }

    /**
     * Get the answer of a vote
     *
     * @return a List holding only one element, the most voted person
     * @throws UserIntendedException if the vote is tied (including 0 vote)
     */
    public List<PlayerData> getResult() throws UserIntendedException {
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
            ArrayList<PlayerData> ans = new ArrayList<>();
            ans.add(PlayerDataUtil.getRoleByMemberId(remainingPlayersList, tiedList.get(0)));
            return ans;
        }
        throw new UserIntendedException("The choice is not unanimous no one will be kill");
    }
}
