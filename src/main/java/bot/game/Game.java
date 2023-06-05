package bot.game;

import bot.game.game.mechanism.*;
import bot.game.roles.*;
import bot.io.ChannelManager;
import bot.io.ProcessingException;
import bot.io.listener.GuildManager;
import bot.io.listener.Waiter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.List;

public class Game extends Thread implements GameType{
    private boolean isActive = true;
    private int nbTurn = 0;
    private final TextChannel channel;
    private final int id;
    private final List<Role> roles;

    /**
     * Start a game
     * @param id of the pending game used by the Interface to route event
     * @param members all player for this game
     * @param channel public channel of the game
     */
    public Game(int id, List<Member> members, TextChannel channel){
        this.id= id;
        int size = members.size();
        List<Role> temporaryRoleList = new ArrayList<>(size);
        Composition compo = new Composition(size);
        for (Member member : members) {
            temporaryRoleList.add(compo.getARole(member));
        }
        this.roles=temporaryRoleList;
        this.channel = channel;
        this.tellRoles();
        if (channel!=null){
            ChannelManager.createRestrictedChannel(channel.getGuild(),RoleManagement.getAll(temporaryRoleList, RoleType.werewolf),"game"+id+"wolf");
            channel.sendMessage("The game has started !").queue();
        }
        this.start();
    }

    /**
     * Make the village vote for someone and the werewolf then
     * @throws GameException if the game is over
     * @throws InterruptedException in case of thread error
     */
    private void playTurn() throws GameException, InterruptedException {
        nbTurn++;
        channel.sendMessage("You have 30 seconds to vote for someone (/vote)").queue();
        playDayTime();
        channel.sendMessage("This is now the night be careful to not die").queue();
        playNightTime();
    }

    private void playNightTime() throws InterruptedException, GameException {
        if (RoleManagement.isRoleIn(roles,EnhanceRoleType.seer)){
            channel.sendMessage("Seer turn for 10 sec").queue();
            try {
                ChannelManager.sendPrivateMessage(RoleManagement.getByRole(roles,EnhanceRoleType.seer).getOwner(),"You can spec someone (/see name) you have 10 s");
                SeerAction action = new SeerAction(roles);
                GuildManager.getInterface(channel.getGuild()).registerAction(id,action);
                Thread.sleep(10000);
                ChannelManager.sendPrivateMessage(RoleManagement.getByRole(roles,EnhanceRoleType.seer).getOwner(),"You have spec " + action.getResult().get(0));
            } catch (ProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        channel.sendMessage("It's time for the hungry boys (30 sec)").queue();
        Vote vote = new Vote(VoteType.werewolf,roles);
        GuildManager.getInterface(channel.getGuild()).registerAction(id,vote);
        Thread.sleep(30000); //DO NOT USE IN THE MAIN THREAD
        try {
            Role eliminated = vote.getResult().get(0);
            if (RoleManagement.isRoleIn(roles,EnhanceRoleType.witch)) playWitch(eliminated);
            else manageDeath(eliminated);
        }catch (ProcessingException e) {
            if (e.getClass()==GameException.class) throw (GameException) e;
            channel.sendMessage(e.getMessage()).queue();
            if (RoleManagement.isRoleIn(roles,EnhanceRoleType.witch)) playWitch(null);
        }
    }

    private void playDayTime() throws InterruptedException,GameException {
        Vote vote = new Vote(VoteType.all,roles);
        GuildManager.getInterface(channel.getGuild()).registerAction(id,vote);
        Thread.sleep(30000); //DO NOT USE IN THE MAIN THREAD
        vote.terminate();
        try {
            Role eliminated = vote.getResult().get(0);
            manageDeath(eliminated);
        } catch (ProcessingException e) {
            if (e.getClass()==GameException.class) throw (GameException) e;
            channel.sendMessage(e.getMessage()).queue();
        }
    }

    private void playWitch(Role eliminated) throws GameException {
        try {
            channel.sendMessage("The magic can appear ...(15 sec)").queue();
            WitchRole witch = (WitchRole) RoleManagement.getByRole(roles, EnhanceRoleType.witch);
            if (!witch.isHealingAvailable() && !witch.isKillingAvailable()){
                if (witch.isHealingAvailable()||eliminated!=null) ChannelManager.sendPrivateMessage(witch.getOwner(), "The wolf are about to eat "+eliminated.getOwner().getEffectiveName());
                ChannelManager.sendPrivateMessage(witch.getOwner(),"You can still kill or save someone (/kill name or /save name) you have 15 s");
                WitchAction action = new WitchAction(EnhanceRoleType.witch,roles,eliminated);
                GuildManager.getInterface(channel.getGuild()).registerAction(id,action);
                Waiter.register(this,action);
                this.wait();
                ArrayList<Role> death = action.getResult();
                for (Role dead : death){
                    manageDeath(dead);
                }
            }
        }catch (ProcessingException ignored){
            manageDeath(eliminated);
            System.out.println("This is not possible if well coded ^^");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Remove the person, tell it and check win
     * @param eliminated the personne to remove
     * @throws GameException if the game is over
     */
    private void manageDeath(Role eliminated) throws GameException {
        roles.remove(eliminated);
        try {
            eliminated.getOwner().mute(true).queue();
        }catch (Exception ignored){}
        channel.sendMessage("The village is now smaller ... : "+eliminated.getOwner().getUser().getName() + " has disappear !").queue();
        channel.sendMessage("Its role was : "+eliminated.getRealRole()).queue();
        RoleManagement.checkWin(roles);
    }

    /**
     * End a game
     * @param e passing exception to allow to have the remaining roles
     */
    private void endOfGame(GameException e){
        this.isActive = false;
        channel.sendMessage("End Of Game !\n"+ e.getMessage()).queue();
        GuildManager.getInterface(channel.getGuild()).terminateGame(this);
    }

    /**
     * On usage methode use to tell to each player their roles in private
     */
    private void tellRoles() {
        for (Role m : roles)
            ChannelManager.sendPrivateMessage(m.getOwner(), "your role is :"+m.getRealRole());
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
    public int getGameId() {
        return id;
    }

    /**
     * bot.Main threat methode. Basically an endless loop calling play turn that is stopped by an endOfGameException
     */
    @Override
    public void run() {
        try {
            while (isActive){
                playTurn();
            }
        }catch (GameException e){
            endOfGame(e);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }

    }
}
