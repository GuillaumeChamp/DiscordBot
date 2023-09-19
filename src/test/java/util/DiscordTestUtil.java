package util;

import bot.io.listener.CommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.List;

public class DiscordTestUtil {
    private static JDA api;

    /**
     * Allow to retrieve the first member of the first server
     * Be careful to not send message or do not get the bot itself
     *
     * @param number position of the member in the server list
     */
    public static Member getAMember(int number) {
        initialize();
        List<Member> members = api.getGuilds().get(0).getMembers();
        if (members.size() < number) {
            System.out.println("There are only " + members.size() + "member on this server, return the last");
            return members.get(members.size() - 1);
        }
        return members.get(number);
    }

    /**
     * Allow to retrieve the discord server owner of the first server
     */
    public static Member getOwner() {
        initialize();
        return api.getGuilds().get(0).retrieveOwner().complete();
    }

    public static TextChannel createTestChannel() {
        initialize();
        Category category = api.getCategoriesByName("MUTE_ME", false).get(0);
        List<TextChannel> oldChannel = api.getGuilds().get(0).getTextChannelsByName("test", true);
        return oldChannel.isEmpty() ? api.getGuilds().get(0).createTextChannel("test", category).complete() : oldChannel.get(0);
    }

    private static void initialize() {
        if (api == null) {
            //String botToken = PropertyReader.getBotProperty("bot-token");
            String botToken = System.getenv("bot-token");
            api = JDABuilder.createLight(botToken, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                    .addEventListeners(new CommandListener())
                    .build();
            try {
                api.awaitReady();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
