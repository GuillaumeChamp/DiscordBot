package util;

import bot.game.mechanism.Action;
import bot.game.roles.EnhanceRoleType;
import bot.game.roles.Role;

import java.util.Collections;
import java.util.List;

import static util.AbstractDiscordTest.testMember;

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
