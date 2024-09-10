package org.guillaumechamp.discordbot.io.commands;

import org.guillaumechamp.discordbot.game.roles.ActionType;
import org.guillaumechamp.discordbot.testUtil.DiscordTestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

class CommandStoreTest {

    @Test
    void shouldRegisteredCommandMatchWithActionType(){
        for(String gameCommand : CommandStore.GAME_COMMAND){
            assertThat(ActionType.stringToActionType(gameCommand)).isNotNull();
        }
    }

    @Test
    void shouldRegisterCommandWorkProperly(){
        assertThatNoException().isThrownBy(()-> CommandStore.registerCommand(DiscordTestUtil.getApi()));
    }
}
