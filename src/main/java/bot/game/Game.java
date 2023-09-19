package bot.game;

import bot.game.game.mechanism.*;
import bot.game.roles.*;
import bot.io.BotConfig;
import bot.io.ChannelManager;
import bot.io.ProcessingException;
import bot.io.listener.GuildManager;
import bot.io.listener.Waiter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class Game extends Thread implements GameType {
    private boolean isActive = true;
    private int nbTurn = 0;
    private final TextChannel channel;
    private final Integer id;
    private final List<Role> roles;
    private final Guild currentServer;

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
            if (!BotConfig.isSilence) {
                ChannelManager.createRestrictedChannel(currentServer, RoleManagement.getAll(temporaryRoleList, RoleType.werewolf), "game" + id + "wolf");
                channel.sendMessage("The game has started !").queue();
            }
        }
        this.tellRoles();
        this.start();
    }

    /**
     * Make the village vote for someone and the werewolf then
     *
     * @throws GameException        if the game is over
     * @throws InterruptedException in case of thread error
     */
    private void playTurn() throws GameException, InterruptedException {
        nbTurn++;
        sendPublicMessage("You have 30 seconds to vote for someone (/vote)");
        playDayTime();
        sendPublicMessage("This is now the night be careful to not die");
        playNightTime();
    }

    private void playNightTime() throws InterruptedException, GameException {
        if (RoleManagement.isRoleIn(roles, EnhanceRoleType.seer)) {
            sendPublicMessage("Seer turn for 10 sec");
            try {
                if (!BotConfig.isSilence)
                    ChannelManager.sendPrivateMessage(RoleManagement.getByRole(roles, EnhanceRoleType.seer).getOwner(), "You can spec someone (/see name) you have 10 s");
                SeerAction action = new SeerAction(roles);
                GuildManager.getInterface(currentServer).registerAction(id, action);
                Thread.sleep(10000);
                if (!BotConfig.isSilence)
                    ChannelManager.sendPrivateMessage(RoleManagement.getByRole(roles, EnhanceRoleType.seer).getOwner(), "You have spec " + action.getResult().get(0));
            } catch (ProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        sendPublicMessage("It's time for the hungry boys (30 sec)");
        Vote vote = new Vote(VoteType.werewolf, roles);
        GuildManager.getInterface(currentServer).registerAction(id, vote);
        Thread.sleep(30000); //DO NOT USE IN THE MAIN THREAD
        try {
            Role eliminated = vote.getResult().get(0);
            if (RoleManagement.isRoleIn(roles, EnhanceRoleType.witch)) playWitch(eliminated);
            else manageDeath(eliminated);
        } catch (ProcessingException e) {
            if (e.getClass() == GameException.class) throw (GameException) e;
            sendPublicMessage(e.getMessage());
            if (RoleManagement.isRoleIn(roles, EnhanceRoleType.witch)) playWitch(null);
        }
    }

    private void playDayTime() throws InterruptedException, GameException {
        Vote vote = new Vote(VoteType.all, roles);
        if (!BotConfig.isSilence) GuildManager.getInterface(currentServer).registerAction(id, vote);
        Thread.sleep(30000); //DO NOT USE IN THE MAIN THREAD
        vote.terminate();
        try {
            Role eliminated = vote.getResult().get(0);
            manageDeath(eliminated);
        } catch (ProcessingException e) {
            if (e.getClass() == GameException.class) throw (GameException) e;
            sendPublicMessage(e.getMessage());
        }
    }

    private void playWitch(Role eliminated) throws GameException {
        try {
            sendPublicMessage("The magic can appear ...(15 sec)");
            WitchRole witch = (WitchRole) RoleManagement.getByRole(roles, EnhanceRoleType.witch);
            if (!witch.isHealingAvailable() && !witch.isKillingAvailable()) {
                if (witch.isHealingAvailable() || eliminated != null) {
                    if (!BotConfig.isSilence) {
                        ChannelManager.sendPrivateMessage(witch.getOwner(), "The wolf are about to eat " + eliminated.getOwner().getEffectiveName());
                    }
                }
                if (!BotConfig.isSilence)
                    ChannelManager.sendPrivateMessage(witch.getOwner(), "You can still kill or save someone (/kill name or /save name) you have 15 s");
                WitchAction action = new WitchAction(EnhanceRoleType.witch, roles, eliminated);
                GuildManager.getInterface(currentServer).registerAction(id, action);
                Waiter.register(this, action);
                this.wait();
                ArrayList<Role> death = action.getResult();
                for (Role dead : death) {
                    manageDeath(dead);
                }
            }
        } catch (ProcessingException ignored) {
            manageDeath(eliminated);
            System.out.println("This is not possible if well coded ^^");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
            eliminated.getOwner().mute(true).queue();
        } catch (Exception ignored) {
        }
        sendPublicMessage("The village is now smaller ... : " + eliminated.getOwner().getUser().getName() + " has disappear !");
        sendPublicMessage("Its role was : " + eliminated.getRealRole());
        RoleManagement.checkWin(roles);
    }

    /**
     * End a game
     *
     * @param e passing exception to allow to have the remaining roles
     */
    private void endOfGame(GameException e) {
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

    /**
     * bot.Main threat methode. Basically an endless loop calling play turn that is stopped by an endOfGameException
     */
    @Override
    public void run() {
        try {
            while (isActive) {
                playTurn();
            }
        } catch (GameException e) {
            endOfGame(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void sendPublicMessage(String message) {
        if (!BotConfig.isSilence) {
            channel.sendMessage(message).queue();
        }
    }
}
