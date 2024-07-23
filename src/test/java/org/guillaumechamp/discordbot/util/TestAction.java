package org.guillaumechamp.discordbot.util;

import org.guillaumechamp.discordbot.game.mechanism.Action;
import org.guillaumechamp.discordbot.game.roles.EnhanceRoleType;
import org.guillaumechamp.discordbot.game.roles.Role;

import java.util.Collections;
import java.util.List;

import static org.guillaumechamp.discordbot.util.AbstractDiscordTest.testMember;

public class TestAction extends Action {
    private static final Role role = new Role(testMember, EnhanceRoleType.simpleVillager);

    public TestAction(int durationInSecond) {
        super(EnhanceRoleType.simpleVillager, Collections.singletonList(role), durationInSecond);
    }

    @Override
    public List<Role> getResult() {
        return null;
    }
}
