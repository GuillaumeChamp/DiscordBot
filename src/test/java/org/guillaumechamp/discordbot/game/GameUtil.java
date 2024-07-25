package org.guillaumechamp.discordbot.game;

import org.guillaumechamp.discordbot.game.roles.EnhanceRoleType;
import org.guillaumechamp.discordbot.game.roles.Role;
import org.guillaumechamp.discordbot.util.DiscordTestUtil;

import java.util.ArrayList;
import java.util.List;

public class GameUtil {
    /**
     * Create an 8 players composition with two wolf at first position
     *
     * @return the composition
     */
    public static List<Role> createSampleComposition() {
        List<Role> ans = new ArrayList<>(8);
        ans.add(new Role(DiscordTestUtil.getAMember(0), EnhanceRoleType.SIMPLE_WOLF));
        ans.add(new Role(DiscordTestUtil.getAMember(0), EnhanceRoleType.SIMPLE_WOLF));
        for (int i = 0; i < 6; i++) {
            ans.add(new Role(DiscordTestUtil.getAMember(0), EnhanceRoleType.SIMPLE_VILLAGER));
        }
        return ans;
    }
}
