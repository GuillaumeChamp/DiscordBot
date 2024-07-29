package org.guillaumechamp.discordbot.io.listener;

import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;

/**
 * Allow to map an interface to a discord guild (i.e. a server)
 */
public class GuildManager {
    private static final HashMap<Guild, Interface> interfaceList = new HashMap<>();

    private GuildManager() {
    }

    public static Interface getInterface(Guild guild) {
        if (interfaceList.containsKey(guild)) {
            return interfaceList.get(guild);
        }
        interfaceList.put(guild, new Interface(guild));
        return interfaceList.get(guild);
    }
}
