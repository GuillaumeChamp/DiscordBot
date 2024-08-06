package org.guillaumechamp.discordbot.game.turn;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.roles.ActionType;
import org.guillaumechamp.discordbot.game.roles.EnhanceRoleType;
import org.guillaumechamp.discordbot.game.roles.Role;
import org.guillaumechamp.discordbot.game.roles.WitchRole;
import org.guillaumechamp.discordbot.io.UserIntendedException;
import org.guillaumechamp.discordbot.util.DiscordTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AbstractTurnTest {
    List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1), DiscordTestUtil.getAMember(2));
    AbstractTurn testAbstractTurn;

    @BeforeEach
    void initAction() {
        List<Role> testList = List.of(new Role(memberList.get(0), EnhanceRoleType.SEER), new WitchRole(memberList.get(1)));
        testAbstractTurn = new AbstractTurn(EnhanceRoleType.WITCH, testList, Collections.singletonList(ActionType.WITCH_SAVE)) {
            @Override
            public List<Role> getResult() {
                return null;
            }
        };
    }

    @Test
    void shouldActionFirstExceptionThrowBeAuthorNotInTheGame() {
        testAbstractTurn.terminate();
        assertThatThrownBy(() -> testAbstractTurn.handleAction(memberList.get(2), memberList.get(2), ActionType.VOTE))
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_AUTHOR_NOT_IN_THE_GAME);
    }

    @Test
    void shouldActionSecondExceptionThrowBeForbiddenAction() {
        testAbstractTurn.terminate();
        assertThatThrownBy(() -> testAbstractTurn.handleAction(memberList.get(0), memberList.get(2), ActionType.VOTE))
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_ACTION_NOT_ALLOWED);
    }

    @Test
    void shouldActionThirdExceptionThrowBeTargetNotInTheGame() {
        testAbstractTurn.terminate();
        assertThatThrownBy(() -> testAbstractTurn.handleAction(memberList.get(1), memberList.get(2), ActionType.VOTE))
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_TARGET_NOT_IN_THE_GAME);
    }

    @Test
    void shouldActionFourthExceptionThrowBeTargetNotInTheGame() {
        testAbstractTurn.terminate();
        assertThatThrownBy(() -> testAbstractTurn.handleAction(memberList.get(1), memberList.get(1), ActionType.VOTE))
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_ACTION_EXPIRED);
    }
    @Test
    void shouldActionFifthExceptionThrowBeWrongCommand() {
        assertThatThrownBy(() -> testAbstractTurn.handleAction(memberList.get(1), memberList.get(1), ActionType.VOTE))
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_WRONG_COMMAND);
    }
    @Test
    void shouldGetterWorkProperly(){
        assertThat(testAbstractTurn.getDuration()).isEqualTo(60);
        assertThat(testAbstractTurn.getPlayerTurn()).isEqualTo(PlayerTurn.NONE);
    }
}
