package org.guillaumechamp.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.guillaumechamp.discordbot.io.BotLogger;
import org.guillaumechamp.discordbot.io.ChannelManager;
import org.guillaumechamp.discordbot.io.listener.CommandListener;
import org.guillaumechamp.discordbot.io.listener.CommandStore;

public class Main {
    static JDA api;

    public static void main(String[] args) throws InterruptedException {
        String botToken = System.getenv("BOT_TOKEN");
        api = JDABuilder.createLight(botToken, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(new CommandListener())
                .build();
        api.awaitReady();
        BotLogger.log(BotLogger.INFO, "Bot started and start listening...\nConnected to : " + api.getGuilds());
        CommandStore.create(api);
        ChannelManager.clearAllCreatedChannelsFromGuild(api.getGuilds().get(0));
    }
    //TODO : revoir héritage
    //TODO : implémenter agitateur
    //perm = 2646829136
}
