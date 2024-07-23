package org.guillaumechamp.discordbot.game;

import org.guillaumechamp.discordbot.game.roles.Role;
import org.guillaumechamp.discordbot.game.roles.RoleManagement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class WinConditionTest {

    @Test
    void shouldVillagerWinByEliminateAllWolf() {
        List<Role> roles = GameUtil.createSampleComposition();
        assertDoesNotThrow(() -> RoleManagement.checkWin(roles));
        roles.remove(1);
        roles.remove(0);
        assertThatThrownBy(() -> RoleManagement.checkWin(roles))
                .isNotNull()
                .extracting(Throwable::getMessage)
                .asString()
                .contains("Villager win !");
    }

    @Test
    void shouldWolfWinByEliminateAllVillager() {
        List<Role> roles = GameUtil.createSampleComposition();
        assertDoesNotThrow(() -> RoleManagement.checkWin(roles));
        List<Role> remaining = roles.subList(0, 1);
        assertThatThrownBy(() -> RoleManagement.checkWin(remaining))
                .isNotNull()
                .extracting(Throwable::getMessage)
                .asString()
                .contains("Werewolf win !");
    }
}
