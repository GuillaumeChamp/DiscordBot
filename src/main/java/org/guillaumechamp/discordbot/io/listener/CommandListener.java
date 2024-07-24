package org.guillaumechamp.discordbot.io.listener;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.guillaumechamp.discordbot.io.BotLogger;
import org.guillaumechamp.discordbot.io.ChannelManager;
import org.guillaumechamp.discordbot.io.ProcessingException;
import org.guillaumechamp.discordbot.io.PropertyReader;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Handle all commands
 *
 * @see ListenerAdapter
 */
public class CommandListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // don't read message
    }

    /**
     * Override the command handler to customize actions.
     *
     * @param event command slash event
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (CommandStore.GAME_COMMAND.contains(event.getName())) {
            handleGameAction(event);
        }

        String logString = new StringJoiner(" ")
                .add(Objects.requireNonNull(event.getMember()).getEffectiveName())
                .add("perform the following command")
                .add(event.getName())
                .add(event.getOptions().toString())
                .toString();
        BotLogger.log(BotLogger.INFO, logString);

        switch (event.getName()) {
            case CommandStore.CREATE_GAME_COMMAND -> handleCreation(event);
            case CommandStore.JOIN_GAME_COMMAND -> handleJoin(event);
            case CommandStore.STOP_GAME_COMMAND -> handleStop(event);
            case CommandStore.START_GAME_COMMAND -> handleStart(event);
            case CommandStore.DISCONNECT_BOT_COMMAND -> handleDisconnect(event);
            default -> BotLogger.log(BotLogger.FATAL, "Command registered but not handled");
        }
    }

    private void handleDisconnect(SlashCommandInteractionEvent event) {
        String expectedPassword = PropertyReader.getBotPropertyFromFile(CommandStore.ARGUMENT_PASSWORD);
        String providedPassword = event.getOption(CommandStore.ARGUMENT_PASSWORD, OptionMapping::getAsString);
        if (expectedPassword.equals(providedPassword)) {
            event.reply("bye").setEphemeral(true).queue();
            BotLogger.log(BotLogger.INFO, event.getUser().getName() + " has shutdown the bot");
            ChannelManager.clearAllCreatedChannelsFromGuild(event.getGuild());
            event.getJDA().shutdown();
        } else event.reply("you are not allow to shutdown the bot").queue();
    }

    private void handleCreation(SlashCommandInteractionEvent event) {
        event.deferReply().setEphemeral(true).queue();
        int maximumPlayers = event.getOption(CommandStore.CREATE_GAME_COMMAND_ARG_1, 512, OptionMapping::getAsInt);
        try {
            GuildManager.getInterface(event.getGuild()).createGame(maximumPlayers);
            event.getHook().sendMessage("the game have been create").setEphemeral(true).queue();
        } catch (ProcessingException e) {
            event.getHook().sendMessage(e.getMessage()).setEphemeral(true).queue();
        }
    }

    private void handleJoin(SlashCommandInteractionEvent event) {
        int option = event.getOption(CommandStore.ARGUMENT_ID, 0, OptionMapping::getAsInt);
        try {
            GuildManager.getInterface(event.getGuild()).addPlayer(event.getMember(), option);
            event.reply(Objects.requireNonNull(event.getMember()).getEffectiveName() + ", You have been added to the game " + option)
                    .setEphemeral(true)
                    .queue();
        } catch (ProcessingException exception) {
            event.reply(exception.getMessage()).setEphemeral(true).queue();
        }
    }

    private void handleStart(SlashCommandInteractionEvent event) {
        int option = event.getOption(CommandStore.ARGUMENT_ID, 0, OptionMapping::getAsInt);
        try {
            event.reply("starting . . .").setEphemeral(true).queue();
            GuildManager.getInterface(event.getGuild()).start(option);
        } catch (ProcessingException e) {
            event.getHook().editOriginal(e.getMessage()).queue();
        }
    }

    private void handleStop(SlashCommandInteractionEvent event) {
        int option = event.getOption(CommandStore.ARGUMENT_ID, 0, OptionMapping::getAsInt);
        try {
            GuildManager.getInterface(event.getGuild()).stop(option);
            event.reply("game deleted").queue();
        } catch (NullPointerException ignored) {
            event.reply("this game do not exist").setEphemeral(true).queue();
        }
    }

    /**
     * This method handle command relative to a game.
     * Will later be enriched and moved to a dedicated interface or util
     *
     * @param event SlashCommandInteractionEvent
     */
    private void handleGameAction(SlashCommandInteractionEvent event) {
        if (event.getOption(CommandStore.ARGUMENT_USER) == null) {
            event.reply("You forget the user").setEphemeral(true).queue();
        }
        event.deferReply().queue();

        Member target = event.getOption(CommandStore.ARGUMENT_USER, OptionMapping::getAsMember);
        Channel channel = event.getChannel();

        try {
            int gameIndex = ChannelManager.resolveGameIndex(channel);
            GuildManager.getInterface(event.getGuild()).performAction(gameIndex, event.getMember(), target, event.getName());
            assert target != null;
            event.getHook().editOriginal(event.getName() + " registered against " + target.getEffectiveName()).queue();
        } catch (ProcessingException | InvalidParameterException e) {
            event.getHook().editOriginal(e.getMessage()).queue();
        }
    }

}
