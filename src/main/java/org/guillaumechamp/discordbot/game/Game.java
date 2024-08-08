package org.guillaumechamp.discordbot.game;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.guillaumechamp.discordbot.game.turn.*;
import org.guillaumechamp.discordbot.game.roles.*;
import org.guillaumechamp.discordbot.io.manager.ChannelManager;
import org.guillaumechamp.discordbot.io.reader.ScriptReader;
import org.guillaumechamp.discordbot.io.UserIntendedException;
import org.guillaumechamp.discordbot.io.manager.GuildManager;
import org.guillaumechamp.discordbot.service.WaiterService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.guillaumechamp.discordbot.io.manager.ChannelManager.getGameChannelNameByIndexAndStatus;
import static org.guillaumechamp.discordbot.io.manager.ChannelManager.muteAMember;

public class Game implements GameInterface {
    // Discord Related Data
    private final Integer id;
    private final Guild currentServer;
    private final TextChannel channel;
    private final ScriptReader.SupportedLanguage gameLanguage;

    // Game Related Data
    private boolean isActive = true;
    private final List<PlayerData> activePlayers;
    private final List<PlayerData> deadPlayers;
    private AbstractTurn currentTurn;


    /**
     * Start a game
     *
     * @param id      of the pending game used by the GameManager to route event
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
            ChannelManager.createRestrictedChannel(currentServer, PlayerDataUtil.getAllMembersBySide(this.activePlayers, RoleSide.WEREWOLF), getGameChannelNameByIndexAndStatus(id, true));
        }
        this.initGame();
    }

    private void initGame() {
        sendPublicMessage(ScriptReader.KeyEntry.START_GAME);

        for (PlayerData player : activePlayers) {
            sendPrivateMessage(player.getOwner(), ScriptReader.KeyEntry.ROLE_GIVEN, Pair.of(ScriptReader.Tag.ROLE,player.getRole().toString()));
        }
    }

    /**
     * Make the game move to the next step
     */
    public void playNextAction() {
        if (!this.isActive) {
            return;
        }
        try {
            TurnResolver.triggerNextAction(this, currentTurn.getPlayerTurn());
        } catch (EndOfGameException endOfGame) {
            this.terminateGame(endOfGame);
        }
    }

    /**
     * Make the game move to the next step
     */
    private void playNextAction(PlayerTurn playerTurn) {
        if (!this.isActive) {
            return;
        }
        try {
            TurnResolver.triggerNextAction(this, playerTurn);
        } catch (EndOfGameException endOfGame) {
            this.terminateGame(endOfGame);
        }
    }
    void beforeSeer() {
        if (shortCircuitActionIfNeeded(RoleType.SEER,PlayerTurn.SEER, ScriptReader.KeyEntry.START_SEER, SeerTurn.DEFAULT_DURATION)){
            return;
        }

        Member seerOwner = PlayerDataUtil.getPlayerDataByRole(activePlayers, RoleType.SEER).getOwner();
        sendPrivateMessage(seerOwner, ScriptReader.KeyEntry.START_SEER_PRIVATE);

        SeerTurn nextAction = new SeerTurn(activePlayers);
        startAction(nextAction);
    }

    void afterSeer() {
        if (PlayerDataUtil.isRoleNotIn(activePlayers, RoleType.SEER)) {
            return;
        }
        Member seerOwner = PlayerDataUtil.getPlayerDataByRole(activePlayers, RoleType.SEER).getOwner();
        try {
            PlayerData target = currentTurn.getResult().get(0);
            sendPrivateMessage(seerOwner, ScriptReader.KeyEntry.SEER_SPEC, Pair.of(ScriptReader.Tag.ROLE, target.getRole().toString()));
        } catch (UserIntendedException e) {
            sendExceptionMessagePrivately(seerOwner, e);
        }
    }

    void beforeWolf() {
        Vote vote = new Vote(PlayerTurn.WOLF_VOTE, activePlayers);
        sendPublicMessage(ScriptReader.KeyEntry.WOLF_START);
        startAction(vote);
    }

    void beforeVillagerVote() {
        Vote vote = new Vote(PlayerTurn.VILLAGE_VOTE, activePlayers);
        sendPublicMessage(ScriptReader.KeyEntry.VOTE);
        startAction(vote);
    }

    void afterVillagerVote() throws EndOfGameException {
        try {
            PlayerData eliminated = currentTurn.getResult().get(0);
            manageDeath(eliminated);
            PlayerDataUtil.checkWin(activePlayers);
        } catch (UserIntendedException e) {
            sendExceptionMessagePublicly(e);
        }
    }

    void beforeWitch() {
        if (shortCircuitActionIfNeeded(RoleType.WITCH,PlayerTurn.WITCH, ScriptReader.KeyEntry.WITCH_PUBLIC, WitchTurn.DEFAULT_DURATION)){
            return;
        }

        PlayerData eliminated;
        try {
            eliminated = currentTurn.getResult().get(0);
        } catch (UserIntendedException ignored) {
            eliminated = null;
        }
        WitchPlayerData witch = (WitchPlayerData) PlayerDataUtil.getPlayerDataByRole(activePlayers, RoleType.WITCH);

        if (!witch.isHealingAvailable() && !witch.isKillingAvailable()) {
            sendPrivateMessage(witch.getOwner(), ScriptReader.KeyEntry.WITCH_NOTHING);
        }
        //according to official rules witch don't know who will die if already used healing potion
        if (witch.isHealingAvailable() && eliminated != null) {
            sendPrivateMessage(witch.getOwner(), ScriptReader.KeyEntry.WITCH_SAVE, Pair.of(ScriptReader.Tag.NAME, eliminated.getOwner().getEffectiveName()));
        }
        if (witch.isKillingAvailable()) {
            sendPrivateMessage(witch.getOwner(), ScriptReader.KeyEntry.WITCH_SAVE);
        }
        WitchTurn newAction = new WitchTurn(activePlayers, eliminated);
        startAction(newAction);
    }

    void afterNight() throws EndOfGameException {
        try {
            List<PlayerData> death = currentTurn.getResult();
            for (PlayerData role : death) {
                this.manageDeath(role);
            }
            PlayerDataUtil.checkWin(activePlayers);

        } catch (UserIntendedException e) {
            sendPublicMessage(ScriptReader.KeyEntry.NO_DEATH);
        }
    }

    /**
     * Remove the person, tell it and check win
     *
     * @param eliminated the person to remove
     */
    private void manageDeath(PlayerData eliminated) {
        // handle double death if a player died from different ways
        if (deadPlayers.contains(eliminated)) {
            return;
        }

        activePlayers.remove(eliminated);
        deadPlayers.add(eliminated);

        muteAMember(eliminated.getOwner());
        sendPublicMessage(ScriptReader.KeyEntry.ELIMINATED,
                Pair.of(ScriptReader.Tag.NAME, eliminated.getOwner().getUser().getName()),
                Pair.of(ScriptReader.Tag.ROLE, eliminated.getRole().toString()));
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


    @Override
    public GameInterface startGame() throws UserIntendedException {
        throw new UserIntendedException("The game is already start");
    }

    @Override
    public void addPlayer(Member member) throws UserIntendedException {
        throw new UserIntendedException("The game is already start");
    }

    @Override
    public void terminate() {
        this.isActive=false;
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

    private void registerDummyTurn(int durationInSecond, PlayerTurn replacedTurn) {
        DummyTurn dummyTurn = new DummyTurn(durationInSecond, replacedTurn);
        GuildManager.getInterface(currentServer).registerAction(id, dummyTurn);
        WaiterService.register(this, dummyTurn);
    }

    private boolean shortCircuitActionIfNeeded(RoleType roleType, PlayerTurn turn, ScriptReader.KeyEntry publicMessageKey, int duration){
        Collection<PlayerData> everyone = CollectionUtils.union(activePlayers, deadPlayers);
        if (PlayerDataUtil.isRoleNotIn(everyone, roleType)) {
            playNextAction(turn);
            return true;
        }

        sendPublicMessage(publicMessageKey);
        if (PlayerDataUtil.isRoleNotIn(activePlayers, roleType)) {
            registerDummyTurn(duration, turn);
            return true;
        }
        return false;
    }
    private void startAction(AbstractTurn action) {
        this.currentTurn = action;
        GuildManager.getInterface(currentServer).registerAction(id, action);
        WaiterService.register(this, action);
    }
}
