package org.guillaumechamp.discordbot.io;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import org.apache.commons.lang3.StringUtils;
import org.guillaumechamp.discordbot.io.listener.Interface;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;


public class ChannelManager {

    private ChannelManager() {
    }

    /**
     * Clear all the game channels
     */
    public static void clearAllCreatedChannelsFromGuild(Guild server) {
        if (server == null) {
            return;
        }
        for (int i = 0; i < Interface.MAX_GAME_PER_GUILD; i++) {
            server.getTextChannelsByName(getGameChannelNameByIndexAndStatus(i, true), true).forEach(ChannelManager::deleteOldChannel);
            server.getTextChannelsByName(getGameChannelNameByIndexAndStatus(i, false), true).forEach(ChannelManager::deleteOldChannel);
        }
    }

    /**
     * resolve game channel name using the parameters.
     * Also check if the index is in bound.
     *
     * @param index  index of the game
     * @param isWolf append wolf prefix if true
     * @return the name of the channel
     * @see Interface#MAX_GAME_PER_GUILD
     */
    public static String getGameChannelNameByIndexAndStatus(int index, boolean isWolf) {
        if (index < 0 || index >= Interface.MAX_GAME_PER_GUILD) {
            throw new InvalidParameterException("Index out of bound : index must be positive and lower than " + Interface.MAX_GAME_PER_GUILD);
        }
        StringBuilder builder = new StringBuilder().append("game").append(index);
        if (isWolf) {
            builder.append("wolf");
        }
        return builder.toString();
    }

    /**
     * Create a new channel deleting older one
     *
     * @param name channel name
     * @return the channel
     */
    public static TextChannel createChannelForAGuild(Guild server, String name) {
        deleteOldChannel(server.getTextChannelsByName(name, true).get(0));
        return server.createTextChannel(name).complete();
    }

    /**
     * Create channel only visible for a white list of user. Delete the older one
     *
     * @param members whitelist (cannot hide it from the owner)
     * @param name    channel name
     */
    public static void createRestrictedChannel(Guild server, List<Member> members, String name) {
        if (Boolean.TRUE.equals(BotConfig.isSilence())) {
            return;
        }
        deleteOldChannel(server.getTextChannelsByName(name, true).get(0));

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

    public static void sendPublicMessage(TextChannel channel, String message) {
        if (Boolean.TRUE.equals(BotConfig.isSilence())) {
            return;
        }
        if (channel==null){
            BotLogger.log(BotLogger.WARN,"Tried to send a message but channel is null");
            throw new InvalidParameterException("Channel is null");
        }
        channel.sendMessage(message).queue();
    }

    /**
     * Send a message in private to a member, checking if the bot is in mute mod
     *
     * @param member  member to send a message to
     * @param message text to send
     */
    public static void sendPrivateMessage(Member member, String message) {
        if (Boolean.TRUE.equals(BotConfig.isSilence())) {
            return;
        }
        member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue());
    }

    /**
     * extract game index from a channel name
     *
     * @param channel channel to parse to game index
     * @return game index
     * @throws InvalidParameterException if it cannot parse to integer
     */
    public static Integer resolveGameIndex(Channel channel) throws InvalidParameterException {
        if (!StringUtils.contains(channel.getName(), "game")) {
            throw new InvalidParameterException("This is not a game channel");
        }
        return channel.getName().charAt(4) - '0';
    }

    /**
     * Mute a person.
     * Check if the player is in an audio channel
     *
     * @param member jda member
     */
    public static void muteAMember(Member member) {
        if (member.getVoiceState() != null && member.getVoiceState().inAudioChannel()) {
            member.mute(true).queue();
        }
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
}
