package org.guillaumechamp.discordbot.io.listener;

import org.guillaumechamp.discordbot.game.roles.ActionType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommandStoreTest {

    @Test
    void shouldRegisteredCommandMatchWithActionType(){
        for(String gameCommand : CommandStore.GAME_COMMAND){
            assertThat(ActionType.stringToActionType(gameCommand)).isNotNull();
        }
    }
}
