package org.guillaumechamp.discordbot.io.listener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Arrays;
import java.util.List;

/**
 * Store commands and expose a method to register all commands in the api
 */
public class CommandStore {
    public static final String KILL_COMMAND = "kill";
    public static final String SAVE_COMMAND = "save";
    public static final String VOTE_COMMAND = "vote";
    public static final String SEE_COMMAND = "see";
    static final List<String> GAME_COMMAND = Arrays.asList(KILL_COMMAND, SAVE_COMMAND, VOTE_COMMAND, SEE_COMMAND);
    public static final String CREATE_GAME_COMMAND = "create";
    public static final String CREATE_GAME_COMMAND_ARG_1 = "max";
    public static final String STOP_GAME_COMMAND = "stop";
    public static final String JOIN_GAME_COMMAND = "join";
    public static final String START_GAME_COMMAND = "start";
    public static final String ARGUMENT_ID = "id";
    public static final String ARGUMENT_USER = "user";
    private static final String USER_ARGUMENT_DESCRIPTION = "target";
    public static final String ARGUMENT_PASSWORD = "password";
    public static final String DISCONNECT_BOT_COMMAND = "disconnect";
    protected static final List<String> DEFAULT_COMMAND = Arrays.asList(CREATE_GAME_COMMAND, STOP_GAME_COMMAND, JOIN_GAME_COMMAND, START_GAME_COMMAND, DISCONNECT_BOT_COMMAND);

    private CommandStore() {
    }

    public static void registerCommand(JDA jda) {
        jda.updateCommands().addCommands(
                Commands.slash(CREATE_GAME_COMMAND, "Create a new open party")
                        .addOptions(new OptionData(OptionType.INTEGER, CREATE_GAME_COMMAND_ARG_1, "number of player allowed")
                                .setDescriptionLocalization(DiscordLocale.FRENCH, "Nombre maximum de joueurs"))
                        .setDescriptionLocalization(DiscordLocale.FRENCH, "Crée une partie de Loup Garou en attente"),
                Commands.slash(STOP_GAME_COMMAND, "stop the indicated game")
                        .addOption(OptionType.INTEGER, ARGUMENT_ID, "game id to stop")
                        .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                        .setDescriptionLocalization(DiscordLocale.FRENCH, "Stop une partie en cours"),
                Commands.slash(JOIN_GAME_COMMAND, "make you join the pending game")
                        .addOption(OptionType.INTEGER, ARGUMENT_ID, "id of the game you want join")
                        .setDescriptionLocalization(DiscordLocale.FRENCH, "Rejoindre une nouvelle partie"),
                Commands.slash(START_GAME_COMMAND, "launch a game")
                        .addOption(OptionType.INTEGER, ARGUMENT_ID, "game id to start")
                        .setDescriptionLocalization(DiscordLocale.FRENCH, "Lance une partie en attente"),
                Commands.slash(DISCONNECT_BOT_COMMAND, "kill the bot")
                        .addOption(OptionType.STRING, ARGUMENT_PASSWORD, "Pass Phrase")
                        .setDescriptionLocalization(DiscordLocale.FRENCH, "Stop le bot"),
                Commands.slash(VOTE_COMMAND, "vote for someone")
                        .addOption(OptionType.USER, ARGUMENT_USER, USER_ARGUMENT_DESCRIPTION)
                        .setDescriptionLocalization(DiscordLocale.FRENCH, "Vote pour quelqu'un"),
                Commands.slash(KILL_COMMAND, "[Witch] use it to kill someone (once a game)")
                        .addOption(OptionType.USER, ARGUMENT_USER, USER_ARGUMENT_DESCRIPTION)
                        .setDescriptionLocalization(DiscordLocale.FRENCH, "[sorcière] Uue un joueur (une fois par partie)"),
                Commands.slash(SAVE_COMMAND, "[Witch] use it to save the target (once a game)")
                        .addOption(OptionType.USER, ARGUMENT_USER, USER_ARGUMENT_DESCRIPTION)
                        .setDescriptionLocalization(DiscordLocale.FRENCH, "[sorcière] Sauve une pesonne des loups (une fois par partie)"),
                Commands.slash(SEE_COMMAND, "[Seer] use it to see a role (once a night)")
                        .addOption(OptionType.USER, ARGUMENT_USER, USER_ARGUMENT_DESCRIPTION)
                        .setDescriptionLocalization(DiscordLocale.FRENCH, "[Voyante] Permet de voir le role d'un joueur")
        ).queue();
    }
}
