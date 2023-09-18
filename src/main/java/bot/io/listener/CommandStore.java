package bot.io.listener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Arrays;
import java.util.List;

public class CommandStore {
    public static final List<String> GameCommand = Arrays.asList("kill", "save", "vote", "see");

    public static void create(JDA jda) {
        jda.updateCommands().addCommands(
                Commands.slash("create", "Create a new open party")
                        .addOption(OptionType.INTEGER, "max", "number of player allowed"),
                Commands.slash("stop", "stop the indicated game")
                        .addOption(OptionType.INTEGER, "id", "game id to stop"),
                Commands.slash("join", "make you join the pending game")
                        .addOption(OptionType.INTEGER, "id", "id of the game you want join"),
                Commands.slash("start", "launch a game")
                        .addOption(OptionType.INTEGER, "id", "game id to start"),
                Commands.slash("disconnect", "kill the bot")
                        .addOption(OptionType.STRING, "password", "Pass Phrase"),
                Commands.slash("vote", "vote for someone")
                        .addOption(OptionType.USER, "user", "target"),
                Commands.slash("kill", "[Witch] use it to kill someone (once a game)")
                        .addOption(OptionType.USER, "user", "target"),
                Commands.slash("save", "[Witch] use it to save the target (once a game)")
                        .addOption(OptionType.USER, "user", "target"),
                Commands.slash("see", "[Seer] use it to see a role (once a night)")
                        .addOption(OptionType.USER, "user", "target")
        ).queue();
    }
}
