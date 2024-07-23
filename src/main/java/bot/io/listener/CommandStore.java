package bot.io.listener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Arrays;
import java.util.List;

public class CommandStore {
    public static final String KILL_COMMAND = "kill";
    public static final String SAVE_COMMAND = "save";
    public static final String VOTE_COMMAND = "vote";
    public static final String SEE_COMMAND = "see";
    public static final List<String> GAME_COMMAND = Arrays.asList(KILL_COMMAND, SAVE_COMMAND, VOTE_COMMAND, SEE_COMMAND);
    public static final String CREATE_GAME_COMMAND = "create";
    public static final String CREATE_GAME_COMMAND_ARG_1 = "max";
    public static final String STOP_GAME_COMMAND = "stop";
    public static final String JOIN_GAME_COMMAND = "join";
    public static final String START_GAME_COMMAND = "start";
    public static final String ARGUMENT_ID = "id";
    public static final String ARGUMENT_USER = "user";
    public static final String ARGUMENT_PASSWORD = "password";
    public static final String DISCONNECT_BOT_COMMAND = "disconnect";
    public static final List<String> DEFAULT_COMMAND = Arrays.asList(CREATE_GAME_COMMAND, STOP_GAME_COMMAND, JOIN_GAME_COMMAND, START_GAME_COMMAND, DISCONNECT_BOT_COMMAND);


    public static void create(JDA jda) {
        jda.updateCommands().addCommands(
                Commands.slash(CREATE_GAME_COMMAND, "Create a new open party")
                        .addOption(OptionType.INTEGER, CREATE_GAME_COMMAND_ARG_1, "number of player allowed"),
                Commands.slash(STOP_GAME_COMMAND, "stop the indicated game")
                        .addOption(OptionType.INTEGER, ARGUMENT_ID, "game id to stop"),
                Commands.slash(JOIN_GAME_COMMAND, "make you join the pending game")
                        .addOption(OptionType.INTEGER, ARGUMENT_ID, "id of the game you want join"),
                Commands.slash(START_GAME_COMMAND, "launch a game")
                        .addOption(OptionType.INTEGER, ARGUMENT_ID, "game id to start"),
                Commands.slash(DISCONNECT_BOT_COMMAND, "kill the bot")
                        .addOption(OptionType.STRING, ARGUMENT_PASSWORD, "Pass Phrase"),
                Commands.slash(VOTE_COMMAND, "vote for someone")
                        .addOption(OptionType.USER, ARGUMENT_USER, "target"),
                Commands.slash(KILL_COMMAND, "[Witch] use it to kill someone (once a game)")
                        .addOption(OptionType.USER, ARGUMENT_USER, "target"),
                Commands.slash(SAVE_COMMAND, "[Witch] use it to save the target (once a game)")
                        .addOption(OptionType.USER, ARGUMENT_USER, "target"),
                Commands.slash(SEE_COMMAND, "[Seer] use it to see a role (once a night)")
                        .addOption(OptionType.USER, ARGUMENT_USER, "target")
        ).queue();
    }
}
