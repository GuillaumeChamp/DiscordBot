package org.guillaumechamp.discordbot.io.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.guillaumechamp.discordbot.io.manager.ChannelUtils;
import org.guillaumechamp.discordbot.service.BotLogger;
import org.jetbrains.annotations.NotNull;

/**
 * Service that listen all text message (command and regular message)
 *
 * @see ListenerAdapter
 */
public class CommandListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        // don't read messages
    }

    /**
     * Override the command handler to customize actions.
     *
     * @param event command slash event
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getMember() == null) {
            event.reply("This command is not allow in private channel, use a server channel instead").setEphemeral(true).queue();
            return;
        }

        event.deferReply().setEphemeral(true).queue();

        if (CommandStore.GAME_COMMAND.contains(event.getName())) {
            handleGameAction(event);
        } else if (CommandStore.DEFAULT_COMMAND.contains(event.getName())) {
            handleGeneralCommands(event);
        } else {
            BotLogger.fatal("The command : " + event.getName() + "not belong to any category");
        }
    }

    private void handleGeneralCommands(SlashCommandInteractionEvent event) {
        CommandContent commandContent;
        String answer;
        switch (event.getName()) {
            case CommandStore.CREATE_GAME_COMMAND -> {
                commandContent = parseCreation(event);
                answer = GeneralCommandHandler.handleCreation(commandContent);
            }
            case CommandStore.JOIN_GAME_COMMAND -> {
                commandContent = parseJoinStartStop(event);
                answer = GeneralCommandHandler.handleJoin(commandContent);
            }
            case CommandStore.STOP_GAME_COMMAND -> {
                commandContent = parseJoinStartStop(event);
                answer = GeneralCommandHandler.handleStop(commandContent);
            }
            case CommandStore.START_GAME_COMMAND -> {
                commandContent = parseJoinStartStop(event);
                answer = GeneralCommandHandler.handleStart(commandContent);
            }
            case CommandStore.DISCONNECT_BOT_COMMAND -> {
                commandContent = parseDisconnect(event);
                answer = GeneralCommandHandler.handleDisconnect(commandContent);
            }
            default -> {
                BotLogger.fatal("Command registered but not handled");
                return;
            }
        }
        logAndAnswer(event, answer, commandContent);
    }

    private CommandContent parseDisconnect(SlashCommandInteractionEvent event) {
        return generateBaseCommandContent(event)
                .stringArgument(event.getOption(CommandStore.ARGUMENT_PASSWORD, OptionMapping::getAsString))
                .build();
    }

    private CommandContent parseCreation(SlashCommandInteractionEvent event) {
        Integer maximumPlayers = event.getOption(CommandStore.CREATE_GAME_COMMAND_ARG_1, OptionMapping::getAsInt);
        return generateBaseCommandContent(event)
                .intArgument(maximumPlayers)
                .build();
    }

    private CommandContent parseJoinStartStop(SlashCommandInteractionEvent event) {
        Integer option = event.getOption(CommandStore.ARGUMENT_ID, OptionMapping::getAsInt);
        return generateBaseCommandContent(event)
                .intArgument(option)
                .build();
    }

    private void handleGameAction(SlashCommandInteractionEvent event) {
        Member target = event.getOption(CommandStore.ARGUMENT_USER, OptionMapping::getAsMember);
        CommandContent commandContent = generateBaseCommandContent(event)
                .target(target)
                .gameIndex(ChannelUtils.resolveGameIndexFromChannelName(event.getChannel().getName()))
                .build();
        event.getHook().sendMessage(GeneralCommandHandler.handleGameCommand(commandContent)).queue();
    }

    private CommandContent.CommandContentBuilder generateBaseCommandContent(SlashCommandInteractionEvent event) {
        return CommandContent.builder()
                .api(event.getJDA())
                .guild(event.getGuild())
                .commandType(event.getName())
                .author(event.getMember());
    }

    private void logAndAnswer(SlashCommandInteractionEvent event, String answer, CommandContent commandContent) {
        BotLogger.info(commandContent.toString());
        event.getHook().sendMessage(answer).complete();
    }

}
