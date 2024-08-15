package org.guillaumechamp.discordbot.io.manager;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import org.apache.commons.lang3.StringUtils;
import org.guillaumechamp.discordbot.service.BotConfig;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;


public class ChannelManager {

    private ChannelManager() {
    }

    /**
     * Clear all game's related channels
     */
    public static void clearAllCreatedChannelsFromGuild(Guild server) {
        if (server == null) {
            return;
        }
        for (int i = 0; i < GameManager.MAX_GAME_PER_GUILD; i++) {
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
     * @return the name of the channel (game+n°+suffix)
     * @throws IllegalArgumentException if game index is negative or higher than the max number of game allowed
     * @see GameManager#MAX_GAME_PER_GUILD
     */
    public static String getGameChannelNameByIndexAndStatus(int index, boolean isWolf) {
        if (index < 0 || index >= GameManager.MAX_GAME_PER_GUILD) {
            throw new IllegalArgumentException("Index out of bound : index must be positive and lower than " + GameManager.MAX_GAME_PER_GUILD);
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
     * @param channelName channel name
     * @return the newly created channel
     * @throws IllegalArgumentException if server is null or if name is empty (or null)
     */
    public static TextChannel createChannelForAGuild(Guild server, String channelName) {
        if (server == null) {
            throw new IllegalArgumentException("server must not be null");
        }
        if (StringUtils.isEmpty(channelName)) {
            throw new IllegalArgumentException("channel name must not be empty");
        }
        server.getTextChannelsByName(channelName, true)
                .forEach(ChannelManager::deleteOldChannel);
        return server.createTextChannel(channelName).complete();
    }

    /**
     * Create channel only visible for a whitelist of users.
     * Delete the older one.
     *
     * @param members whitelist (cannot hide it from the owner)
     * @param name    channel name
     */
    public static void createRestrictedChannel(Guild server, List<Member> members, String name) {
        if (server == null) {
            throw new IllegalArgumentException("server must not be null");
        }
        if (!StringUtils.isNotEmpty(name)) {
            throw new IllegalArgumentException("channel name must not be empty");
        }

        server.getTextChannelsByName(name, true).forEach(ChannelManager::deleteOldChannel);

        Collection<Permission> grant = EnumSet.of(Permission.MESSAGE_SEND, Permission.MESSAGE_HISTORY);
        Collection<Permission> revoked = EnumSet.of(Permission.MESSAGE_ATTACH_FILES);

        TextChannel channel = server.createTextChannel(name)
                .addMemberPermissionOverride(server.getJDA().getSelfUser().getIdLong(), Permission.MANAGE_PERMISSIONS.getRawValue(), 0)
                .addRolePermissionOverride(server.getPublicRole().getIdLong(), Collections.singleton(Permission.UNKNOWN), grant)
                .complete();
        TextChannelManager channelManager = channel.getManager();
        for (Member member : members) {
            channelManager.putMemberPermissionOverride(member.getIdLong(), grant, revoked).queue();
        }
    }

    public static void sendMessageToAChannel(TextChannel channel, String message) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel is null");
        }
        if (StringUtils.isEmpty(message)){
            throw new IllegalArgumentException("message is null or empty");
        }
        if (Boolean.TRUE.equals(BotConfig.isSilence())) {
            return;
        }
        channel.sendMessage(message).queue();
    }

    /**
     * Send a message in private to a member, checking if the bot is in mute mod
     *
     * @param member  member to send a message to
     * @param message text to send
     */
    public static void sendPrivateMessageToAMember(Member member, String message) {
        if (member==null){
            throw new IllegalArgumentException("member is null");
        }
        if (StringUtils.isEmpty(message)){
            throw new IllegalArgumentException("message is null or empty");
        }
        if (Boolean.TRUE.equals(BotConfig.isSilence())) {
            return;
        }
        member.getUser().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(message).queue());

    }

    /**
     * extract game index from a channel name
     *
     * @param channelName channel to parse to game index
     * @return game index
     * @throws IllegalArgumentException if it cannot parse to integer
     */
    public static Integer resolveGameIndexFromChannelName(String channelName) {
        if (!StringUtils.startsWith(channelName, "game")) {
            throw new IllegalArgumentException("This is not a game channel");
        }
        int index = channelName.charAt(4) - '0';
        if (index >= GameManager.MAX_GAME_PER_GUILD || index < 0) {
            throw new IllegalArgumentException("This is not a valid pattern name, expected game#suffix");
        }
        return index;
    }

    /**
     * Mute a person.
     * Check if the player is in an audio channel
     *
     * @param member jda member
     */
    public static void muteAMember(Member member) {
        updateMuteStatus(member, true);
    }

    /**
     * Unmute a person.
     * Check if the player is in an audio channel
     *
     * @param member jda member
     */
    public static void unmuteAMember(Member member) {
        updateMuteStatus(member, false);
    }

    private static void updateMuteStatus(Member member, boolean newValue) {
        if (member == null) {
            throw new IllegalArgumentException("member is null");
        }
        if (member.getVoiceState() != null && member.getVoiceState().inAudioChannel()) {
            member.mute(newValue).queue();
        }
    }

    private static void deleteOldChannel(TextChannel channel) {
        if (channel == null) {
            return;
        }
        channel.delete().queue();
    }
}
