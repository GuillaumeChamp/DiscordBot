package org.guillaumechamp.discordbot.game;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.apache.commons.lang3.tuple.Pair;
import org.guillaumechamp.discordbot.game.mechanism.*;
import org.guillaumechamp.discordbot.game.roles.*;
import org.guillaumechamp.discordbot.io.BotLogger;
import org.guillaumechamp.discordbot.io.ChannelManager;
import org.guillaumechamp.discordbot.io.ScriptReader;
import org.guillaumechamp.discordbot.io.UserIntendedException;
import org.guillaumechamp.discordbot.io.listener.GuildManager;
import org.guillaumechamp.discordbot.io.listener.Waiter;

import java.util.ArrayList;
import java.util.List;

import static org.guillaumechamp.discordbot.io.ChannelManager.getGameChannelNameByIndexAndStatus;
import static org.guillaumechamp.discordbot.io.ChannelManager.muteAMember;

public class Game implements GameInterface {
    private enum playerTurn {INIT, SEER, WOLF_VOTE, WITCH, VILLAGE_VOTE}

    private AbstractTurn action;
    private boolean isActive = true;
    private final TextChannel channel;
    private final Integer id;
    private final List<Role> activePlayers;
    private final List<Role> deadPlayers;
    private final Guild currentServer;
    private playerTurn previousAction = playerTurn.INIT;
    private static final ScriptReader.TextLanguage gameLanguage = ScriptReader.TextLanguage.EN;

    /**
     * Start a game
     *
     * @param id      of the pending game used by the Interface to route event
     * @param members all player for this game
     * @param channel public channel of the game
     */
    public Game(Integer id, List<Member> members, TextChannel channel) {
        this.id = id;
        this.activePlayers = assignRoles(members);
        this.deadPlayers = new ArrayList<>(members.size());
        this.channel = channel;
        if (channel == null) {
            this.currentServer = null;
        } else {
            this.currentServer = channel.getGuild();
            sendPublicMessage("start", gameLanguage);
            ChannelManager.createRestrictedChannel(currentServer, RoleManagement.getAllByRoleType(this.activePlayers, RoleType.WEREWOLF), getGameChannelNameByIndexAndStatus(id, true));
        }
        tellRoles();
    }

    private List<Role> assignRoles(List<Member> members) {
        int size = members.size();
        Composition compo = new Composition(size);
        List<Role> roleList = new ArrayList<>(size);

        for (Member member : members) {
            roleList.add(compo.getARole(member));
        }
        return roleList;
    }

    /**
     * Make the game move to the next step
     */
    public void playNextAction() {
        if (!this.isActive) {
            return;
        }
        try {
            switch (previousAction) {
                case INIT -> playSeer();
                case SEER -> {
                    resolveSeer();
                    playWolf();
                }
                case WOLF_VOTE -> playWitch();
                case WITCH -> playVote();
                case VILLAGE_VOTE -> {
                    resolveVillagerVote();
                    if (isActive) {
                        playSeer();
                    }
                }
            }
        } catch (UserIntendedException e) {
            BotLogger.log(BotLogger.WARN, "Processing Error in " + e);
        }
    }

    private void resolveSeer() {
        Member seerOwner = RoleManagement.getByRole(activePlayers, EnhanceRoleType.SEER).getOwner();
        try {
            if (action.getResult().isEmpty()) {
                ChannelManager.sendPrivateMessage(seerOwner, "You have spec no one");
            }
            Role target = action.getResult().get(0);
            ChannelManager.sendPrivateMessage(seerOwner, "You have spec " + target.getRealRole());
        } catch (UserIntendedException e) {
            ChannelManager.sendPrivateMessage(seerOwner, e.getMessage());
        }

    }

    private void playSeer() {
        if (RoleManagement.roleIsNotIn(activePlayers, EnhanceRoleType.SEER)) {
            playWolf();
            return;
        }
        sendPublicMessage("seerStart", gameLanguage);

        ChannelManager.sendPrivateMessage(RoleManagement.getByRole(activePlayers, EnhanceRoleType.SEER).getOwner(), "You can spec someone (/see name) you have 10 s");

        SeerTurn nextAction = new SeerTurn(activePlayers);
        previousAction = playerTurn.SEER;
        startAction(nextAction);
    }

    private void playWolf() {
        sendPublicMessage("wolfStart", gameLanguage);
        Vote vote = new Vote(Vote.VoteType.WEREWOLF, activePlayers);
        this.previousAction = playerTurn.WOLF_VOTE;
        startAction(vote);
    }

    private void playVote() {
        sendPublicMessage("vote", ScriptReader.TextLanguage.EN);
        Vote vote = new Vote(Vote.VoteType.WEREWOLF, activePlayers);
        previousAction = playerTurn.VILLAGE_VOTE;
        startAction(vote);
    }

    private void resolveVillagerVote() throws GameException {
        try {
            Role eliminated = action.getResult().get(0);
            manageDeath(eliminated);
        } catch (UserIntendedException e) {
            if (e.getClass() == GameException.class) throw (GameException) e;
            sendPublicMessage(e.getMessage());
        }
    }

    private void playWitch() throws UserIntendedException {
        previousAction = playerTurn.WITCH;
        if (RoleManagement.roleIsNotIn(activePlayers, EnhanceRoleType.WITCH)) {
            try {
                Role eliminated = action.getResult().get(0);
                manageDeath(eliminated);
                playNextAction();
                return;
            } catch (UserIntendedException e) {
                sendPublicMessage(e.getMessage());
                playNextAction();
                return;
            }
        }
        try {
            Role eliminated = action.getResult().get(0);
            sendPublicMessage("witchAction", gameLanguage);
            WitchRole witch = (WitchRole) RoleManagement.getByRole(activePlayers, EnhanceRoleType.WITCH);

            if (!witch.isHealingAvailable() && !witch.isKillingAvailable()) {
                return;
            }
            //according to official rules witch don't know who will die if already used healing potion
            if (witch.isHealingAvailable() && eliminated != null) {
                ChannelManager.sendPrivateMessage(witch.getOwner(), "The wolf are about to eat " + eliminated.getOwner().getEffectiveName() + " you can save it /save name");
            }
            if (witch.isKillingAvailable()) {
                ChannelManager.sendPrivateMessage(witch.getOwner(), "You can kill someone (/kill name) you have 15 s");
            }
            WitchTurn newtAction = new WitchTurn(EnhanceRoleType.WITCH, activePlayers, eliminated);
            startAction(newtAction);
            ArrayList<Role> death = newtAction.getResult();
            for (Role dead : death) {
                manageDeath(dead);
            }
        } catch (UserIntendedException e) {
            BotLogger.log(BotLogger.WARN, "Processing Error in " + e);
        }
    }

    /**
     * Remove the person, tell it and check win
     *
     * @param eliminated the person to remove
     * @throws GameException if the game is over
     */
    private void manageDeath(Role eliminated) throws GameException {
        // handle double death if a player died from different ways
        if (deadPlayers.contains(eliminated)) {
            return;
        }

        activePlayers.remove(eliminated);
        deadPlayers.add(eliminated);

        muteAMember(eliminated.getOwner());

        sendPublicMessage("The village is now smaller ... : " + eliminated.getOwner().getUser().getName() + " has disappear !");
        sendPublicMessage("Its role was : " + eliminated.getRealRole());
        try {
            RoleManagement.checkWin(activePlayers);
        } catch (GameException endOfGame) {
            this.terminateGame(endOfGame);
        }
    }

    /**
     * End a game
     *
     * @param gameException passing exception to allow to have the remaining roles
     */
    private void terminateGame(GameException gameException) {
        this.isActive = false;
        sendPublicMessage("End Of Game !\n" + gameException.getMessage());
        GuildManager.getInterface(currentServer).stop(this.id);
    }

    /**
     * On usage methode use to tell to each player their roles in private
     */
    private void tellRoles() {
        for (Role player : activePlayers) {
            ChannelManager.sendPrivateMessage(player.getOwner(), "your role is :" + player.getRealRole());
        }
    }

    @Override
    public GameInterface startGame() throws UserIntendedException {
        throw new UserIntendedException("The game is already start");
    }

    @Override
    public void addPlayer(Member member) throws UserIntendedException {
        throw new UserIntendedException("The game is already start");
    }

    private void sendPublicMessage(String message) {
        ChannelManager.sendPublicMessage(channel, message);
    }

    private void sendPublicMessage(String key, ScriptReader.TextLanguage language) {
        sendPublicMessage(ScriptReader.readLine(key, language));
    }

    @SafeVarargs
    private void sendPublicMessage(String key, ScriptReader.TextLanguage language, Pair<String, String>... wards) {
        String rawText = ScriptReader.readLine(key, language);
        for (Pair<String, String> ward : wards) {
            rawText = ScriptReader.parse(rawText, ward.getLeft(), ward.getRight());
        }
        sendPublicMessage(rawText);
    }

    private void startAction(AbstractTurn action) {
        this.action = action;
        GuildManager.getInterface(currentServer).registerAction(id, action);
        Waiter.register(this, action);
    }
}
