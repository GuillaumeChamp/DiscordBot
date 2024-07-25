package org.guillaumechamp.discordbot.game;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.apache.commons.lang3.tuple.Pair;
import org.guillaumechamp.discordbot.game.mechanism.*;
import org.guillaumechamp.discordbot.game.roles.*;
import org.guillaumechamp.discordbot.io.*;
import org.guillaumechamp.discordbot.io.listener.GuildManager;
import org.guillaumechamp.discordbot.io.listener.Waiter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Game implements GameType {
    private enum actionType {NULL, SEER, WOLF_VOTE, WITCH, VILLAGE_VOTE}

    private BaseAction action;
    private boolean isActive = true;
    private int nbTurn = 0;
    private final TextChannel channel;
    private final Integer id;
    private final List<Role> roles;
    private final Guild currentServer;
    private actionType previousAction = actionType.NULL;
    private final ScriptReader.TextLanguage gameLanguage = ScriptReader.TextLanguage.EN;

    /**
     * Start a game
     *
     * @param id      of the pending game used by the Interface to route event
     * @param members all player for this game
     * @param channel public channel of the game
     */
    public Game(Integer id, List<Member> members, TextChannel channel) {
        this.id = id;
        int size = members.size();
        List<Role> temporaryRoleList = new ArrayList<>(size);
        Composition compo = new Composition(size);
        for (Member member : members) {
            temporaryRoleList.add(compo.getARole(member));
        }
        this.roles = temporaryRoleList;
        this.channel = channel;
        if (channel == null) {
            this.currentServer = null;
        } else {
            this.currentServer = channel.getGuild();
            sendPublicMessage("start", gameLanguage);
            if (!BotConfig.isSilence) {
                ChannelManager.createRestrictedChannel(currentServer, RoleManagement.getAll(temporaryRoleList, RoleType.WEREWOLF), "game" + id + "wolf");
            }
        }
        this.tellRoles();
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
                case NULL:
                    playSeer();
                    break;
                case SEER:
                    resolveSeer();
                    playWolf();
                    break;
                case WOLF_VOTE:
                    playWitch();
                    break;
                case WITCH:
                    playVote();
                    break;
                case VILLAGE_VOTE:
                    resolveVillagerVote();
                    if (isActive) playSeer();
                    break;
            }
        } catch (ProcessingException e) {
            BotLogger.log(BotLogger.WARN, "Processing Error in " + e);
        }
    }

    public void interruptGame() {
        if (action != null) {
            if (!Waiter.removeAction(this)) {
                throw new RuntimeException("Tryed to interrupt a game but the game have no action");
            }
            this.isActive = false;
        }
    }

    private void resolveSeer() throws ProcessingException {
        if (!BotConfig.isSilence) {
            try {
                Role target = action.getResult().get(0);
                ChannelManager.sendPrivateMessage(RoleManagement.getByRole(roles, EnhanceRoleType.SEER).getOwner(), "You have spec " + target.getRealRole());
            } catch (ProcessingException e) {
                ChannelManager.sendPrivateMessage(RoleManagement.getByRole(roles, EnhanceRoleType.SEER).getOwner(), "You have spec nothing");
            }
        }
    }

    private void playSeer() {
        if (RoleManagement.roleIsNotIn(roles, EnhanceRoleType.SEER)) {
            playWolf();
            return;
        }
        sendPublicMessage("seerStart", gameLanguage);
        try {
            if (!BotConfig.isSilence) {
                ChannelManager.sendPrivateMessage(RoleManagement.getByRole(roles, EnhanceRoleType.SEER).getOwner(), "You can spec someone (/see name) you have 10 s");
            }
            SeerAction action = new SeerAction(roles);
            previousAction = actionType.SEER;
            startAction(action);
        } catch (ProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void playWolf() {
        sendPublicMessage("wolfStart", gameLanguage);
        Vote vote = new Vote(Vote.VoteType.WEREWOLF, roles);
        this.previousAction = actionType.WOLF_VOTE;
        startAction(vote);
    }

    private void playVote() throws GameException {
        sendPublicMessage("vote", ScriptReader.TextLanguage.EN);
        Vote vote = new Vote(Vote.VoteType.WEREWOLF, roles);
        previousAction = actionType.VILLAGE_VOTE;
        startAction(vote);
    }

    private void resolveVillagerVote() throws GameException {
        try {
            Role eliminated = action.getResult().get(0);
            manageDeath(eliminated);
        } catch (ProcessingException e) {
            if (e.getClass() == GameException.class) throw (GameException) e;
            sendPublicMessage(e.getMessage());
        }
    }

    private void playWitch() throws ProcessingException {
        previousAction = actionType.WITCH;
        if (RoleManagement.roleIsNotIn(roles, EnhanceRoleType.WITCH)) {
            try {
                Role eliminated = action.getResult().get(0);
                manageDeath(eliminated);
                playNextAction();
                return;
            } catch (ProcessingException e) {
                sendPublicMessage(e.getMessage());
                playNextAction();
                return;
            }
        }
        try {
            Role eliminated = null;
            try {
                eliminated = action.getResult().get(0);
                manageDeath(eliminated);
            } catch (ProcessingException ignored) {
            }
            sendPublicMessage("witchAction", gameLanguage);
            WitchRole witch = (WitchRole) RoleManagement.getByRole(roles, EnhanceRoleType.WITCH);
            if (!witch.isHealingAvailable() && !witch.isKillingAvailable()) {
                if (witch.isHealingAvailable() && eliminated != null) {
                    if (!BotConfig.isSilence) {
                        ChannelManager.sendPrivateMessage(witch.getOwner(), "The wolf are about to eat " + eliminated.getOwner().getEffectiveName());
                    }
                }
                if (!BotConfig.isSilence)
                    ChannelManager.sendPrivateMessage(witch.getOwner(), "You can still kill or save someone (/kill name or /save name) you have 15 s");
                WitchAction action = new WitchAction(EnhanceRoleType.WITCH, roles, eliminated);
                startAction(action);
                ArrayList<Role> death = action.getResult();
                for (Role dead : death) {
                    manageDeath(dead);
                }
            }
        } catch (ProcessingException e) {
            BotLogger.log(BotLogger.WARN, "Processing Error in " + e);
        }
    }

    /**
     * Remove the person, tell it and check win
     *
     * @param eliminated the personne to remove
     * @throws GameException if the game is over
     */
    private void manageDeath(Role eliminated) throws GameException {
        roles.remove(eliminated);
        try {
            if (Objects.requireNonNull(eliminated.getOwner().getVoiceState()).inAudioChannel())
                eliminated.getOwner().mute(true).queue();
        } catch (Exception ignored) {

        }
        sendPublicMessage("The village is now smaller ... : " + eliminated.getOwner().getUser().getName() + " has disappear !");
        sendPublicMessage("Its role was : " + eliminated.getRealRole());
        try {
            RoleManagement.checkWin(roles);
        } catch (GameException endOfGame) {
            this.terminateGame(endOfGame);
        }
    }

    /**
     * End a game
     *
     * @param e passing exception to allow to have the remaining roles
     */
    private void terminateGame(GameException e) {
        this.isActive = false;
        sendPublicMessage("End Of Game !\n" + e.getMessage());
        GuildManager.getInterface(currentServer).terminateGame(this);
    }

    /**
     * On usage methode use to tell to each player their roles in private
     */
    private void tellRoles() {
        for (Role player : roles) {
            if (!BotConfig.isSilence) {
                ChannelManager.sendPrivateMessage(player.getOwner(), "your role is :" + player.getRealRole());
            }
        }
    }

    @Override
    public GameType startGame() throws ProcessingException {
        throw new ProcessingException("The game is already start");
    }

    @Override
    public void addPlayer(Member member) throws ProcessingException {
        throw new ProcessingException("The game is already start");
    }

    @Override
    public Integer getGameId() {
        return id;
    }

    private void sendPublicMessage(String message) {
        if (Boolean.FALSE.equals(BotConfig.isSilence)) {
            channel.sendMessage(message).queue();
        }
    }

    private void sendPublicMessage(String key, ScriptReader.TextLanguage language) {
        sendPublicMessage(ScriptReader.readLine(key, language));
    }

    @SafeVarargs
    private final void sendPublicMessage(String key, ScriptReader.TextLanguage language, Pair<String, String>... wards) {
        String rawText = ScriptReader.readLine(key, language);
        for (Pair<String, String> ward : wards) {
            rawText = ScriptReader.parse(rawText, ward.getLeft(), ward.getRight());
        }
        sendPublicMessage(rawText);
    }

    private void startAction(BaseAction action) {
        this.action = action;
        GuildManager.getInterface(currentServer).registerAction(id, action);
        Waiter.register(this, action);
    }
}
