package org.guillaumechamp.discordbot.io.manager;

import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

/**
 * Allow to map an interface to a discord guild (i.e. a server)
 */
public class GuildManager {
    private static final Map<Guild, GameManager> interfaceList = new HashMap<>();

    private GuildManager() {
    }

    public static GameManager getGameManager(Guild guild) {
        if (interfaceList.containsKey(guild)) {
            return interfaceList.get(guild);
        }
        interfaceList.put(guild, new GameManager(guild));
        return interfaceList.get(guild);
    }
}
