package org.guillaumechamp.discordbot.io.commands;

import org.guillaumechamp.discordbot.io.UserIntendedException;
import org.guillaumechamp.discordbot.io.manager.ChannelUtils;
import org.guillaumechamp.discordbot.io.manager.GuildManager;
import org.guillaumechamp.discordbot.io.reader.PropertyReader;

public class GeneralCommandHandler {
    private GeneralCommandHandler(){}

    public static String handleDisconnect(CommandContent commandContent){
        String expectedPassword = PropertyReader.getBotPropertyFromFile(CommandStore.ARGUMENT_PASSWORD);
        if (expectedPassword.equals(commandContent.getStringArgument())) {
            commandContent.getApi().getGuilds().forEach(ChannelUtils::clearAllCreatedChannelsFromGuild);
            commandContent.getApi().shutdown();
            return "bye";
        }else {
            return "you are not allow to shutdown the bot";
        }
    }

    public static String handleCreation(CommandContent commandContent){
        try {
            GuildManager.getGameManager(commandContent.getGuild()).createGame(commandContent.getIntArgument());
            return "the game have been create";
        } catch (UserIntendedException exception) {
            return exception.getMessage();
        }
    }

    public static String handleJoin(CommandContent commandContent){
        try {
            GuildManager.getGameManager(commandContent.getGuild()).addPlayer(commandContent.getAuthor(), commandContent.getIntArgument());
            return commandContent.getAuthor().getEffectiveName() + ", You have been added to the game " + commandContent.getIntArgument();
        } catch (UserIntendedException exception) {
           return exception.getMessage();
        }
    }

    public static String handleStart(CommandContent commandContent){
        try {
            GuildManager.getGameManager(commandContent.getGuild()).start(commandContent.getIntArgument());
            return "starting ...";
        } catch (UserIntendedException exception) {
            return exception.getMessage();
        }
    }

    public static String handleStop(CommandContent commandContent){
        try {
            GuildManager.getGameManager(commandContent.getGuild()).stop(commandContent.getIntArgument());
            return "game stopped";
        } catch (UserIntendedException exception) {
            return exception.getMessage();
        }
    }

    public static String handleGameCommand(CommandContent commandContent){
        // at this moment, every game action need a target as argument
        if (commandContent.getTarget() == null) {
            return "You forget the user";
        }
        try {
            GuildManager.getGameManager(commandContent.getGuild()).transferCommandToTheAction(commandContent.getGameIndex(),
                    commandContent.getAuthor(),
                    commandContent.getTarget(),
                    commandContent.getCommandType());
            return commandContent.getCommandType() + " registered against " + commandContent.getTarget().getEffectiveName();
        } catch (UserIntendedException e) {
            return e.getMessage();
        }
    }
}
