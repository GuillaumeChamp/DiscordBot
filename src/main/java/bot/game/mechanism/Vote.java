package bot.game.mechanism;

import bot.game.roles.EnhanceRoleType;
import bot.game.roles.Role;
import bot.game.roles.RoleManagement;
import bot.game.roles.RoleType;
import bot.io.ProcessingException;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vote extends Action {
    private static final int DEFAULT_DURATION = 60;
    private final RoleType[] rolesToVote;
    private final List<Role> roles;
    private final List<String> targetsId;

    /**
     * Create a new vote
     *
     * @param rolesToVote macro to design who can vote
     * @param roles       all player remaining in the game
     */
    public Vote(VoteType rolesToVote, List<Role> roles) {
        super(EnhanceRoleType.simpleWolf, null, DEFAULT_DURATION);

        if (rolesToVote == VoteType.werewolf) {
            this.rolesToVote = new RoleType[]{RoleType.werewolf};
        } else {
            this.rolesToVote = new RoleType[]{RoleType.werewolf, RoleType.villager};
        }
        this.roles = roles;
        this.targetsId = new ArrayList<>(roles.size());
        for (int i = 0; i < roles.size(); i++) {
            targetsId.add(null);
        }
        isActive = true;
    }

    /**
     * Add a vote
     *
     * @param voter  the personne who want to vote
     * @param target the personne for whom it wants to vote
     * @param action must be "vote" or the action will be rejected
     * @throws ProcessingException if the voter is not in the game,
     *                             if the target is not in the game, if the vote is close,
     *                             if the voter role is not allowed for this vote
     */
    @Override
    public void handleAction(Member voter, Member target, String action) throws ProcessingException {
        if (!action.equals("vote")) throw new ProcessingException("This is not the time for this, this is vote time");
        if (!isActive) throw new ProcessingException(voter.getEffectiveName() + " : the vote is close");
        if (RoleManagement.isNotIn(roles, target))
            throw new ProcessingException(target.getEffectiveName() + " this player is not in the game");
        if (RoleManagement.isNotIn(roles, voter))
            throw new ProcessingException(voter.getEffectiveName() + " : You seem to not be in the game");
        if (!RoleManagement.isA(roles, voter, this.rolesToVote))
            throw new ProcessingException("It's not your turn to vote");
        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).getOwner().getId().equals(voter.getId())) {
                targetsId.set(i, target.getId());
                return;
            }
        }
    }

    /**
     * Get the answer of a vote
     *
     * @return the personne with the maximum of vote
     * @throws ProcessingException if two player have the same amount of vote, if the target disappear
     */
    public ArrayList<Role> getResult() throws ProcessingException {
        String first = null;
        int max = 0;
        int max2 = 0;
        Map<String, Integer> hm = new HashMap<>();
        for (String i : targetsId) {
            Integer j = hm.get(i);
            hm.put(i, (j == null) ? 1 : j + 1);
        }
        for (Map.Entry<String, Integer> val : hm.entrySet()) {
            if (val.getValue() > max) {
                max = val.getValue();
                first = val.getKey();
                continue;
            }
            if (val.getValue() == max) {
                max2 = val.getValue();
            }
        }
        if (max == max2) throw new ProcessingException("The choice is not unanimous no one will be kill");
        ArrayList<Role> ans = new ArrayList<>();
        if (first == null)
            throw new ProcessingException("The village was quiet and no one wanted to vote for some one\nIt's so sad, it's always funny to see someone burn !");
        ans.add(RoleManagement.getRoleOf(roles, first));
        return ans;
    }
}
