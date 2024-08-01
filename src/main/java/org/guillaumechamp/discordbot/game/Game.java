package org.guillaumechamp.discordbot.game;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.guillaumechamp.discordbot.game.mechanism.*;
import org.guillaumechamp.discordbot.game.roles.*;
import org.guillaumechamp.discordbot.io.ChannelManager;
import org.guillaumechamp.discordbot.io.ScriptReader;
import org.guillaumechamp.discordbot.io.UserIntendedException;
import org.guillaumechamp.discordbot.io.listener.GuildManager;
import org.guillaumechamp.discordbot.io.listener.Waiter;

import java.util.ArrayList;
import java.util.Collection;
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
    private final ScriptReader.SupportedLanguage gameLanguage;

    /**
     * Start a game
     *
     * @param id      of the pending game used by the Interface to route event
     * @param members all player for this game
     * @param channel public channel of the game
     */
    public Game(Integer id, List<Member> members, TextChannel channel) {
        this.id = id;
        this.activePlayers = Composition.assignRoles(members);
        this.deadPlayers = new ArrayList<>(members.size());
        this.channel = channel;
        this.gameLanguage = ScriptReader.SupportedLanguage.EN;
        if (channel == null) {
            this.currentServer = null;
        } else {
            this.currentServer = channel.getGuild();
            sendPublicMessage(ScriptReader.KeyEntry.START_GAME);
            ChannelManager.createRestrictedChannel(currentServer, RoleManagement.getAllByRoleType(this.activePlayers, RoleType.WEREWOLF), getGameChannelNameByIndexAndStatus(id, true));
        }
        tellRoles();
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
                case INIT -> beforeSeer();
                case SEER -> {
                    afterSeer();
                    beforeWolf();
                }
                case WOLF_VOTE -> beforeWitch();
                case WITCH -> {
                    afterNight();
                    beforeVillagerVote();
                }
                case VILLAGE_VOTE -> {
                    afterVillagerVote();
                    beforeSeer();
                }
            }
        } catch (EndOfGameException endOfGame) {
            this.terminateGame(endOfGame);
        }
    }

    private void beforeSeer() {
        previousAction = playerTurn.SEER;
        Collection<Role> everyone = CollectionUtils.union(activePlayers, deadPlayers);
        if (RoleManagement.roleIsNotIn(everyone, EnhanceRoleType.SEER)) {
            playNextAction();
            return;
        }

        sendPublicMessage(ScriptReader.KeyEntry.START_SEER);
        if (RoleManagement.roleIsNotIn(activePlayers, EnhanceRoleType.SEER)) {
            registerDummyTurn(SeerTurn.DEFAULT_DURATION);
            return;
        }
        Member seerOwner = RoleManagement.getByRole(activePlayers, EnhanceRoleType.SEER).getOwner();
        sendPrivateMessage(seerOwner, ScriptReader.KeyEntry.START_SEER_PRIVATE);

        SeerTurn nextAction = new SeerTurn(activePlayers);
        startAction(nextAction);
    }

    private void afterSeer() {
        Member seerOwner = RoleManagement.getByRole(activePlayers, EnhanceRoleType.SEER).getOwner();
        try {
            Role target = action.getResult().get(0);
            sendPrivateMessage(seerOwner, ScriptReader.KeyEntry.SEER_SPEC, Pair.of(ScriptReader.Tag.ROLE, target.getRealRole().toString()));
        } catch (UserIntendedException e) {
            sendExceptionMessagePrivately(seerOwner, e);
        }
    }

    private void beforeWolf() {
        this.previousAction = playerTurn.WOLF_VOTE;
        Vote vote = new Vote(Vote.VoteType.WEREWOLF, activePlayers);
        sendPublicMessage(ScriptReader.KeyEntry.WOLF_START);
        startAction(vote);
    }

    private void beforeVillagerVote() {
        this.previousAction = playerTurn.VILLAGE_VOTE;
        Vote vote = new Vote(Vote.VoteType.ALL, activePlayers);
        sendPublicMessage(ScriptReader.KeyEntry.VOTE);
        startAction(vote);
    }

    private void afterVillagerVote() throws EndOfGameException {
        try {
            Role eliminated = action.getResult().get(0);
            manageDeath(eliminated);
            RoleManagement.checkWin(activePlayers);
        } catch (UserIntendedException e) {
            sendExceptionMessagePublicly(e);
        }
    }

    private void beforeWitch() {
        previousAction = playerTurn.WITCH;
        Collection<Role> everyone = CollectionUtils.union(activePlayers, deadPlayers);
        if (RoleManagement.roleIsNotIn(everyone, EnhanceRoleType.WITCH)) {
            playNextAction();
            return;
        }
        sendPublicMessage(ScriptReader.KeyEntry.WITCH_PUBLIC);

        if (RoleManagement.roleIsNotIn(activePlayers, EnhanceRoleType.WITCH)) {
            registerDummyTurn(WitchTurn.DEFAULT_DURATION);
            return;
        }

        Role eliminated;
        try {
            eliminated = action.getResult().get(0);
        } catch (UserIntendedException ignored) {
            eliminated = null;
        }
        WitchRole witch = (WitchRole) RoleManagement.getByRole(activePlayers, EnhanceRoleType.WITCH);

        if (!witch.isHealingAvailable() && !witch.isKillingAvailable()) {
            return;
        }
        //according to official rules witch don't know who will die if already used healing potion
        if (witch.isHealingAvailable() && eliminated != null) {
            sendPrivateMessage(witch.getOwner(), ScriptReader.KeyEntry.WITCH_SAVE, Pair.of(ScriptReader.Tag.NAME, eliminated.getOwner().getEffectiveName()));
        }
        if (witch.isKillingAvailable()) {
            sendPrivateMessage(witch.getOwner(), ScriptReader.KeyEntry.WITCH_SAVE);
        }
        WitchTurn newAction = new WitchTurn(EnhanceRoleType.WITCH, activePlayers, eliminated);
        startAction(newAction);
    }

    private void afterNight() throws EndOfGameException {
        try {
            List<Role> death = action.getResult();
            for (Role role : death) {
                this.manageDeath(role);
            }
            RoleManagement.checkWin(activePlayers);

        } catch (UserIntendedException e) {
            sendPublicMessage(ScriptReader.KeyEntry.NO_DEATH);
        }
    }

    /**
     * Remove the person, tell it and check win
     *
     * @param eliminated the person to remove
     */
    private void manageDeath(Role eliminated) {
        // handle double death if a player died from different ways
        if (deadPlayers.contains(eliminated)) {
            return;
        }

        activePlayers.remove(eliminated);
        deadPlayers.add(eliminated);

        muteAMember(eliminated.getOwner());
        sendPublicMessage(ScriptReader.KeyEntry.ELIMINATED,
                Pair.of(ScriptReader.Tag.NAME, eliminated.getOwner().getUser().getName()),
                Pair.of(ScriptReader.Tag.ROLE, eliminated.getRealRole().toString()));
    }

    /**
     * End a game
     *
     * @param endOfGameException passing exception to allow to have the remaining roles
     */
    private void terminateGame(EndOfGameException endOfGameException) {
        this.isActive = false;
        sendExceptionMessagePublicly(endOfGameException);
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

    private void sendExceptionMessagePublicly(Exception e) {
        ChannelManager.sendPublicMessage(channel, e.getMessage());
    }

    @SafeVarargs
    public final void sendPublicMessage(ScriptReader.KeyEntry key, Pair<ScriptReader.Tag, String>... wards) {
        ChannelManager.sendPublicMessage(channel, ScriptReader.readLineAndParse(key, gameLanguage, wards));
    }

    private void sendPublicMessage(ScriptReader.KeyEntry key) {
        ChannelManager.sendPublicMessage(channel, ScriptReader.readLine(key, gameLanguage));
    }

    private void sendPrivateMessage(Member destination, ScriptReader.KeyEntry key) {
        ChannelManager.sendPrivateMessage(destination, ScriptReader.readLine(key, gameLanguage));
    }

    @SafeVarargs
    private void sendPrivateMessage(Member destination, ScriptReader.KeyEntry key, Pair<ScriptReader.Tag, String>... wards) {
        ChannelManager.sendPrivateMessage(destination, ScriptReader.readLineAndParse(key, gameLanguage, wards));
    }

    private void sendExceptionMessagePrivately(Member destination, Exception e) {
        ChannelManager.sendPrivateMessage(destination, e.getMessage());
    }

    private void registerDummyTurn(int durationInSecond) {
        DummyTurn dummyTurn = new DummyTurn(durationInSecond);
        GuildManager.getInterface(currentServer).registerAction(id, dummyTurn);
        Waiter.register(this, dummyTurn);
    }

    private void startAction(AbstractTurn action) {
        this.action = action;
        GuildManager.getInterface(currentServer).registerAction(id, action);
        Waiter.register(this, action);
    }
}
