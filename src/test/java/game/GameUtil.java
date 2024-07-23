package game;

import org.guillaumechamp.discordbot.game.roles.EnhanceRoleType;
import org.guillaumechamp.discordbot.game.roles.Role;
import util.DiscordTestUtil;

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
        ans.add(new Role(DiscordTestUtil.getAMember(0), EnhanceRoleType.simpleWolf));
        ans.add(new Role(DiscordTestUtil.getAMember(0), EnhanceRoleType.simpleWolf));
        for (int i = 0; i < 6; i++) {
            ans.add(new Role(DiscordTestUtil.getAMember(0), EnhanceRoleType.simpleVillager));
        }
        return ans;
    }
}
