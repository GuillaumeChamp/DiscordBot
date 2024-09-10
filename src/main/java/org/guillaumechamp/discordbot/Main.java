package org.guillaumechamp.discordbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.guillaumechamp.discordbot.service.BotLogger;
import org.guillaumechamp.discordbot.io.manager.ChannelUtils;
import org.guillaumechamp.discordbot.io.commands.CommandListener;
import org.guillaumechamp.discordbot.io.commands.CommandStore;
import org.guillaumechamp.discordbot.service.WaiterService;

public class Main {
    static JDA api;

    public static void main(String[] args) throws InterruptedException {
        String botToken = System.getenv("BOT_TOKEN");
        api = JDABuilder.createLight(botToken, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .addEventListeners(new CommandListener())
                .build();
        api.awaitReady();
        BotLogger.info("Connected to : " + api.getGuilds());
        CommandStore.registerCommand(api);
        api.getGuilds().forEach(ChannelUtils::clearAllCreatedChannelsFromGuild);
        WaiterService.initWaiter();
    }
    //perm = 2646829136
}
