package org.guillaumechamp.discordbot.util;

import org.guillaumechamp.discordbot.game.mechanism.BaseAction;
import org.guillaumechamp.discordbot.game.roles.EnhanceRoleType;
import org.guillaumechamp.discordbot.game.roles.Role;

import java.util.Collections;
import java.util.List;

import static org.guillaumechamp.discordbot.util.AbstractDiscordTest.testMember;

public class TestAction extends BaseAction {
    private static final Role role = new Role(testMember, EnhanceRoleType.SIMPLE_VILLAGER);

    public TestAction(int durationInSecond) {
        super(EnhanceRoleType.SIMPLE_VILLAGER, Collections.singletonList(role), null);
        this.durationInSecond = durationInSecond;
    }

    @Override
    public List<Role> getResult() {
        return null;
    }
}
