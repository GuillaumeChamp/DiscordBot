package org.guillaumechamp.discordbot.game.Turn;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.mechanism.ActionType;
import org.guillaumechamp.discordbot.game.mechanism.SeerTurn;
import org.guillaumechamp.discordbot.game.roles.EnhanceRoleType;
import org.guillaumechamp.discordbot.game.roles.Role;
import org.guillaumechamp.discordbot.io.UserIntendedException;
import org.guillaumechamp.discordbot.util.DiscordTestUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class SeerTurnTest {
    @Test
    void shouldSeerCanActOnlyOnce() {
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1));
        List<Role> testList = List.of(new Role(memberList.get(0), EnhanceRoleType.SEER), new Role(memberList.get(1), EnhanceRoleType.SIMPLE_VILLAGER));
        SeerTurn turn = new SeerTurn(testList);
        // --When
        assertThatNoException().isThrownBy(() -> turn.handleAction(memberList.get(0), memberList.get(0), ActionType.SEER_SEE));
        // --Then
        assertThatThrownBy(() -> turn.handleAction(memberList.get(0), memberList.get(0), ActionType.SEER_SEE))
                .hasMessage("This is too late, this action is no longer authorized");
    }

    @Test
    void shouldGetResultThrowExceptionIfNoUse() {
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1));
        List<Role> testList = List.of(new Role(memberList.get(0), EnhanceRoleType.SEER), new Role(memberList.get(1), EnhanceRoleType.SIMPLE_VILLAGER));
        SeerTurn turn = new SeerTurn(testList);
        // --When

        // --Then
        assertThatThrownBy(turn::getResult).isInstanceOf(UserIntendedException.class)
                .hasMessage("You have spec no one");
    }

    @Test
    void shouldGetResultWorkIfReceiveOneRequest() throws UserIntendedException {
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1));
        List<Role> testList = List.of(new Role(memberList.get(0), EnhanceRoleType.SEER), new Role(memberList.get(1), EnhanceRoleType.SIMPLE_VILLAGER));
        SeerTurn turn = new SeerTurn(testList);
        // --When
        assertThatNoException().isThrownBy(() -> turn.handleAction(memberList.get(0), memberList.get(0), ActionType.SEER_SEE));
        // --Then
        assertThat(turn.getResult())
                .singleElement()
                .isEqualTo(testList.get(0));
    }
}
