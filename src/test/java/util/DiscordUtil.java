package util;

import bot.io.PropertyReader;
import bot.io.listener.CommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;
import java.util.List;

public class DiscordUtil {
    private static JDA api;

    /**
     * Allow to retrieve the first member of the first server
     * Be careful to not send message or do not get the bot itself
     * @param number position of the member in the server list
     */
    public static Member getAMember(int number) throws InterruptedException {
        try {
            initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<Member> members = api.getGuilds().get(0).getMembers();
        if (members.size()<number){
            System.out.println("There are only "+ members.size() + "member on this server, return the last");
            return members.get(members.size()-1);
        }
        return members.get(number);
    }
    /**
     * Allow to retrieve the discord server owner of the first server
     */
    public static Member getOwner() throws InterruptedException {
        try {
            initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return api.getGuilds().get(0).retrieveOwner().complete();
    }

    private static void initialize() throws InterruptedException, IOException {
        if (api==null){
            String botToken = PropertyReader.getBotProperty("bot-token");
            api = JDABuilder.createLight(botToken, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                    .addEventListeners(new CommandListener())
                    .build();
            api.awaitReady();
        }
    }

}
