package org.guillaumechamp.discordbot.testUtil;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.guillaumechamp.discordbot.io.listener.CommandListener;

import java.util.List;

public class DiscordTestUtil {
    private static JDA api;

    /**
     * Allow to retrieve a member of the first server for test purposes
     * Warning : there is no guarantee that two different numbers mean two different members.
     *
     * @param number position of the member in the server list.
     *               If there n is number of member connected, for any number greater or equal to n will give the member n-1
     * @return guaranteed to have at least a member (the bot itself)
     */
    public static Member getAMember(int number) {
        initialize();
        List<Member> members = api.getGuilds().get(0).getMembers();
        if (members.size() <= number) {
            System.out.println("There are only " + members.size() + " member(s) on this server, return the last");
            return members.get(members.size() - 1);
        }
        return members.get(number);
    }

    public static TextChannel createTestChannel() {
        initialize();
        Category category = api.getCategoriesByName("MUTE_ME", false).get(0);
        List<TextChannel> oldChannel = api.getGuilds().get(0).getTextChannelsByName("test", true);
        return oldChannel.isEmpty() ? api.getGuilds().get(0).createTextChannel("test", category).complete() : oldChannel.get(0);
    }

    private static void initialize() {
        if (api == null) {
            String botToken = System.getenv("BOT_TOKEN");
            api = JDABuilder.createDefault(botToken, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)
                    .setChunkingFilter(ChunkingFilter.ALL) // load all user of all guilds matching memberCache policy
                    .setMemberCachePolicy(MemberCachePolicy.ONLINE) // cache only connected users
                    .enableCache(CacheFlag.ONLINE_STATUS)
                    .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
                    .addEventListeners(new CommandListener())
                    .build();
            try {
                api.awaitReady();
            } catch (InterruptedException e) {
                throw new RuntimeException("Exception occurred during init" + e);
            }
        }
    }

}
