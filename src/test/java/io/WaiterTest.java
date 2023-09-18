package io;

import bot.game.Game;
import bot.game.game.mechanism.Vote;
import bot.game.game.mechanism.VoteType;
import bot.game.roles.EnhanceRoleType;
import bot.game.roles.Role;
import bot.io.listener.Waiter;
import net.dv8tion.jda.api.entities.Member;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import util.AbstractDiscordTest;
import util.DiscordTestUtil;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

class WaiterTest extends AbstractDiscordTest {
    @Test
    void shouldWaiterAwakeGameProperly() {
        Member testMember = DiscordTestUtil.getOwner();
        Game game = new Game(0, Collections.singletonList(testMember), testChannel);
        Role role = new Role(testMember, EnhanceRoleType.simpleVillager);
        Vote vote = new Vote(VoteType.all, Collections.singletonList(role));
        Waiter.register(game, vote);
        Awaitility.await().atLeast(59, TimeUnit.SECONDS).atMost(62, TimeUnit.SECONDS).until(() -> !vote.isActive());
    }

    @Test
    void shouldWaiterHoldFewActions() {
        Member testMember = DiscordTestUtil.getOwner();
        Game game = new Game(0, Collections.singletonList(testMember), testChannel);
        Game game1 = new Game(0, Collections.singletonList(testMember), testChannel);
        Role role = new Role(testMember, EnhanceRoleType.simpleVillager);
        Vote vote = new Vote(VoteType.all, Collections.singletonList(role));
        Vote vote1 = new Vote(VoteType.werewolf, Collections.singletonList(role));
        Waiter.register(game, vote);
        Waiter.register(game1, vote1);
        Awaitility.await()
                .atLeast(59, TimeUnit.SECONDS)
                .atMost(62, TimeUnit.SECONDS)
                .until(() -> !vote.isActive() && !vote1.isActive());
    }

}
