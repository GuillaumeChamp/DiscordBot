package org.guillaumechamp.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.guillaumechamp.discordbot.service.BotLogger;
import org.guillaumechamp.discordbot.io.manager.ChannelManager;
import org.guillaumechamp.discordbot.io.listener.CommandListener;
import org.guillaumechamp.discordbot.io.listener.CommandStore;
import org.guillaumechamp.discordbot.service.WaiterService;

public class Main {
    static JDA api;

    public static void main(String[] args) throws InterruptedException {
        String botToken = System.getenv("BOT_TOKEN");
        api = JDABuilder.createLight(botToken, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(new CommandListener())
                .build();
        api.awaitReady();
        BotLogger.log(BotLogger.INFO, "Connected to : " + api.getGuilds());
        CommandStore.registerCommand(api);
        api.getGuilds().forEach(ChannelManager::clearAllCreatedChannelsFromGuild);
        WaiterService.initWaiter();
    }
    //perm = 2646829136
}
