package org.guillaumechamp.discordbot.game.turn;

import org.guillaumechamp.discordbot.io.UserIntendedException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DummyTurnTest {
    @Test
    void shouldDummyActionHaveNoResult() {
        // --Given
        DummyTurn turn = new DummyTurn(30,PlayerTurn.SEER);
        // --Then
        assertThat(turn.getResult()).isEmpty();
    }

    @Test
    void shouldDummyActionHandleAlwaysResponseActionNotAllowed() {
        // --Given
        DummyTurn turn = new DummyTurn(30,PlayerTurn.SEER);
        // --Then
        assertThatThrownBy(()->turn.handleAction(null,null,null))
                .isInstanceOf(UserIntendedException.class)
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_ACTION_NOT_ALLOWED);
    }

}
