package bot.io;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;


public class ChannelManager {

    private ChannelManager() {
    }

    /**
     * Clear discord function
     *
     * @param channel channel to delete
     */
    private static void deleteOldChannel(TextChannel channel) {
        if (channel == null) return;
        channel.delete().queue();
    }

    /**
     * Clear all the game channels
     */
    public static void clearAll(Guild server) {
        if (server == null)
            return;
        server.getTextChannelsByName("game0", true).forEach(ChannelManager::deleteOldChannel);
        server.getTextChannelsByName("game0wolf", true).forEach(ChannelManager::deleteOldChannel);
        server.getTextChannelsByName("game1", true).forEach(ChannelManager::deleteOldChannel);
        server.getTextChannelsByName("game1wolf", true).forEach(ChannelManager::deleteOldChannel);
        server.getTextChannelsByName("game2", true).forEach(ChannelManager::deleteOldChannel);
        server.getTextChannelsByName("game2wolf", true).forEach(ChannelManager::deleteOldChannel);
    }

    /**
     * Create a new channel deleting older one
     *
     * @param name channel name
     * @return the channel
     */
    public static TextChannel createChannel(Guild server, String name) {
        try {
            deleteOldChannel(server.getTextChannelsByName(name, true).get(0));
        } catch (Exception ignored) {
        }
        return server.createTextChannel(name).complete();
    }

    /**
     * Create channel only visible for a white list of user. Delete the older one
     *
     * @param members whitelist (cannot hide it from the owner)
     * @param name    channel name
     */
    public static void createRestrictedChannel(Guild server, List<Member> members, String name) {
        try {
            deleteOldChannel(server.getTextChannelsByName(name, true).get(0));
        } catch (Exception ignored) {
        }

        Collection<Permission> grant = EnumSet.of(Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY);
        Collection<Permission> revoked = EnumSet.of(Permission.MESSAGE_ATTACH_FILES);

        TextChannel channel = server.createTextChannel(name)
                .addMemberPermissionOverride(server.getJDA().getSelfUser().getIdLong(), Permission.MANAGE_PERMISSIONS.getRawValue(), 0)
                .addRolePermissionOverride(server.getPublicRole().getIdLong(), Collections.singleton(Permission.UNKNOWN), grant).complete();
        TextChannelManager m = channel.getManager();
        for (Member member : members) {
            m.putMemberPermissionOverride(member.getIdLong(), grant, revoked).queue();
        }
    }

    public static void sendPrivateMessage(Member m, String message) {
        m.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue());
    }

}
