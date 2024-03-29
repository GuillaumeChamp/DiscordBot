package bot;

import bot.io.BotLogger;
import bot.io.ChannelManager;
import bot.io.listener.CommandListener;
import bot.io.listener.CommandStore;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.logging.Level;

public class Main {
    static JDA api;

    public static void main(String[] args) throws InterruptedException {
        String botToken = System.getenv("BOT_TOKEN");
        api = JDABuilder.createLight(botToken, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(new CommandListener())
                .build();
        api.awaitReady();
        BotLogger.log(Level.INFO, "Bot started and start listening...");
        BotLogger.log(Level.INFO, "Connected to : " + api.getGuilds());
        CommandStore.create(api);
        ChannelManager.clearAll(api.getGuilds().get(0));
    }
    //TODO : revoir héritage
    //TODO : implémenter agitateur
    //perm = 2646829136
}
