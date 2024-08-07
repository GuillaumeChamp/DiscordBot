package org.guillaumechamp.discordbot.game.roles;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActionTypeTest {

    @Test
    void shouldStringToActionTypeRaiseExceptionForBadInput() {
        String randomString = "dqEA0VXL";
        assertThatThrownBy(()->ActionType.stringToActionType(randomString))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(randomString);
    }
}