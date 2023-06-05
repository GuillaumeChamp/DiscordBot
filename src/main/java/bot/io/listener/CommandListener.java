package bot.io.listener;

import bot.io.ChannelManager;
import bot.io.ProcessingException;
import bot.io.PropertyReader;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

public class CommandListener extends ListenerAdapter{
    Logger logger = Logger.getLogger(CommandListener.class.getName());

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.getAuthor().isBot()) return;
        // We don't want to respond to other bot accounts, including ourselves
        Message message = event.getMessage();
        String content = message.getContentRaw(); 
        // getContentRaw() is an atomic getter
        // getContentDisplay() is a lazy getter which modifies the content for e.g. console view (strip discord formatting)
        if (content.contains("quoi ?"))
        {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Feur").queue(); // Important to call .queue() on the RestAction returned by sendMessage(...)
        }
        if (content.contains("ça gaze")){
            MessageChannel channel = event.getChannel();
            channel.sendMessage("ça gaze").queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (MyCommand.GameCommand.contains(event.getName())){
            handleAction(event);
        }
        switch (event.getName()){
            case "create" :
                handleCreation(event);
                break;
            case "join" :
                handleJoin(event);
                break;
            case "stop":
                handleStop(event);
                break;
            case "start" :
                handleStart(event);
                break;
            case "disconnect":
                handleDisconnect(event);
                break;
            default:
                event.reply("unknown command").queue();
                break;
        }
    }

    /**
     * ephemeral answer
     */
    private void handleDisconnect(SlashCommandInteractionEvent event) {
        String password = "admin";
        try {
            password = PropertyReader.getBotProperty("password");
        }catch (IOException e){
            e.printStackTrace();
            logger.warning("FILE NOT FOUND");
        }
        if (password.equals(Objects.requireNonNull(event.getOption("password")).getAsString())){
            event.reply("bye").setEphemeral(true).queue();
            logger.info(event.getUser().getName() + " has shutdown the bot");
            ChannelManager.clearAll(event.getGuild());
            event.getJDA().shutdown();
        }
        else event.reply("you are not allow to shutdown the bot").queue();
    }

    private void handleAction(SlashCommandInteractionEvent event) {
        if (event.getOption("user")==null) event.reply("You forget the user").setEphemeral(true).queue();
        Member target = Objects.requireNonNull(event.getOption("user")).getAsMember();
        TextChannel channel = event.getChannel().asTextChannel();
        event.deferReply().queue();
        if (!channel.getName().contains("game")) {
            event.getHook().sendMessage("You are not in a game channel").setEphemeral(true).queue();
            return;
        }
        int gameIndex = Integer.parseInt(channel.getName().replace("game",""));
        try {
            GuildManager.getInterface(event.getGuild()).performAction(gameIndex,event.getMember(),target,event.getName());
            event.getHook().sendMessage(event.getName() + " registered against " +target.getEffectiveName()).setEphemeral(true).queue();
        }catch (ProcessingException e){
            event.getHook().editOriginal(e.getMessage()).queue();
        }

    }

    private void handleStop(SlashCommandInteractionEvent event) {
        try {
            int option = Objects.requireNonNull(event.getOption("id")).getAsInt();
            GuildManager.getInterface(event.getGuild()).stop(option);
            event.reply("game deleted").queue();
        }catch (NullPointerException ignored){
            event.reply("this game do not exist").setEphemeral(true).queue();
        }
    }

    /**
     * Handle start of a game
     * @param event commandEvent
     */
    private void handleStart(SlashCommandInteractionEvent event) {
        int option = 0;
        event.reply("starting . . .").setEphemeral(true).queue();
        try {
            option = Objects.requireNonNull(event.getOption("id")).getAsInt();
        }catch (NullPointerException ignored){}
        try {
            GuildManager.getInterface(event.getGuild()).start(option);
        } catch (ProcessingException e) {
            event.getHook().editOriginal(e.getMessage()).queue();
        }
    }

    /**
     * Handle the join event with the optional game id.
     * If no optional, add it to the first game available
     * @param event commandEvent
     */
    private void handleJoin(SlashCommandInteractionEvent event) {
        int option = 0;
        try{
            option=Objects.requireNonNull(event.getOption("id")).getAsInt();
        }catch (NullPointerException ignore){}
        try {
            GuildManager.getInterface(event.getGuild()).addPlayer(event.getMember(),option);
            event.reply(Objects.requireNonNull(event.getMember()).getEffectiveName()+", You have been added to the game " + option).setEphemeral(true).queue();
        }catch (ProcessingException exception){
            event.reply(exception.getMessage()).setEphemeral(true).queue();
        }
    }

    /**
     * Handle Creation of a party
     * @param event commandEvent
     */
    private void handleCreation(SlashCommandInteractionEvent event) {
        event.deferReply().setEphemeral(true).queue();
        int option = 512;
        try {
            option = Objects.requireNonNull(event.getOption("max")).getAsInt();
        }catch (NullPointerException ignored){}
        try {
            GuildManager.getInterface(event.getGuild()).createGame(option);
            event.getHook().sendMessage("the game have been create").setEphemeral(true).queue();
        } catch (ProcessingException e) {
            event.getHook().sendMessage(e.getMessage()).setEphemeral(true).queue();
        }

    }
}
