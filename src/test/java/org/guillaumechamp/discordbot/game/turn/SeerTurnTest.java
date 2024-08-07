package org.guillaumechamp.discordbot.game.turn;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.roles.ActionType;
import org.guillaumechamp.discordbot.game.roles.RoleType;
import org.guillaumechamp.discordbot.game.roles.PlayerData;
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
        List<PlayerData> testList = List.of(new PlayerData(memberList.get(0), RoleType.SEER), new PlayerData(memberList.get(1), RoleType.SIMPLE_VILLAGER));
        SeerTurn turn = new SeerTurn(testList);
        // --When
        assertThatNoException().isThrownBy(() -> turn.handleAction(memberList.get(0), memberList.get(0), ActionType.SEER_SEE));
        // --Then
        assertThatThrownBy(() -> turn.handleAction(memberList.get(0), memberList.get(0), ActionType.SEER_SEE))
                .isInstanceOf(UserIntendedException.class)
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_ACTION_EXPIRED);
    }

    @Test
    void shouldGetResultThrowExceptionIfNoUse() {
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1));
        List<PlayerData> testList = List.of(new PlayerData(memberList.get(0), RoleType.SEER), new PlayerData(memberList.get(1), RoleType.SIMPLE_VILLAGER));
        SeerTurn turn = new SeerTurn(testList);
        // --When

        // --Then
        assertThatThrownBy(turn::getResult)
                .isInstanceOf(UserIntendedException.class)
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_SEER_NO_SPEC);
    }

    @Test
    void shouldGetResultWorkIfReceiveOneRequest() throws UserIntendedException {
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1));
        List<PlayerData> testList = List.of(new PlayerData(memberList.get(0), RoleType.SEER), new PlayerData(memberList.get(1), RoleType.SIMPLE_VILLAGER));
        SeerTurn turn = new SeerTurn(testList);
        // --When
        assertThatNoException().isThrownBy(() -> turn.handleAction(memberList.get(0), memberList.get(0), ActionType.SEER_SEE));
        // --Then
        assertThat(turn.getResult())
                .singleElement()
                .isEqualTo(testList.get(0));
    }
}
