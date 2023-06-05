package bot;

import bot.io.ChannelManager;
import bot.io.PropertyReader;
import bot.io.listener.MyCommand;
import bot.io.listener.CommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;

public class Main {
    static JDA api;

    public static void main(String[] args) throws InterruptedException, IOException {
        String botToken = PropertyReader.getBotProperty("bot-token");
        api = JDABuilder.createLight(botToken, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(new CommandListener())
                .build();
        api.awaitReady();
        MyCommand.create(api);
        ChannelManager.clearAll(api.getGuilds().get(0));
    }
    //TODO : Message loader
    //TODO : revoir héritage
    //TODO : implémenter agitateur
    //perm = 2646829136
}
