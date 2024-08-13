package org.guillaumechamp.discordbot.game.turn;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.roles.*;
import org.guillaumechamp.discordbot.game.roles.PlayerData;
import org.guillaumechamp.discordbot.game.roles.WitchPlayerData;
import org.guillaumechamp.discordbot.io.UserIntendedException;
import org.guillaumechamp.discordbot.testUtil.DiscordTestUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class WitchTurnTest {

    @Test
    void shouldWitchCanUseBothAction() {
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1), DiscordTestUtil.getAMember(2));
        List<PlayerData> testList = List.of(new WitchPlayerData(memberList.get(0)), new PlayerData(memberList.get(1), RoleType.SIMPLE_VILLAGER), new PlayerData(memberList.get(2), RoleType.SIMPLE_VILLAGER));
        WitchPlayerData witch = (WitchPlayerData) testList.get(0);
        WitchTurn turn = new WitchTurn(testList, testList.get(1));
        // --Then
        assertThatNoException().isThrownBy(() -> turn.handleAction(memberList.get(0), memberList.get(2), ActionType.WITCH_KILL));
        assertThat(witch.isKillingAvailable()).isFalse();
        assertThatNoException().isThrownBy(() -> turn.handleAction(memberList.get(0), memberList.get(1), ActionType.WITCH_SAVE));
        assertThat(witch.isHealingAvailable()).isFalse();
    }

    @Test
    void shouldWitchCannotUseHealIfAlreadyUsed() {
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1));
        List<PlayerData> testList = List.of(new WitchPlayerData(memberList.get(0)), new PlayerData(memberList.get(1), RoleType.SIMPLE_VILLAGER));
        WitchPlayerData witch = (WitchPlayerData) testList.get(0);
        WitchTurn turn = new WitchTurn(testList, testList.get(1));
        // --When
        witch.useHeal();
        // --Then
        assertThatThrownBy(() -> turn.handleAction(memberList.get(0), null, ActionType.WITCH_SAVE))
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_POWER_ALREADY_USED);
    }

    @Test
    void shouldWitchCanNotSaveIfNoOneIsKill() {
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1));
        List<PlayerData> testList = List.of(new WitchPlayerData(memberList.get(0)), new PlayerData(memberList.get(1), RoleType.SIMPLE_VILLAGER));
        WitchTurn turn = new WitchTurn(testList, null);
        // --Then
        assertThatThrownBy(() -> turn.handleAction(memberList.get(0), null, ActionType.WITCH_SAVE))
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_WITCH_NO_DEAD);
    }

    @Test
    void shouldWitchHealNeedToPreciseTarget() {
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1));
        List<PlayerData> testList = List.of(new WitchPlayerData(memberList.get(0)), new PlayerData(memberList.get(1), RoleType.SIMPLE_VILLAGER));
        WitchTurn turn = new WitchTurn(testList, testList.get(1));
        // --When
        // --Then
        assertThatThrownBy(() -> turn.handleAction(memberList.get(0), null, ActionType.WITCH_SAVE))
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_NO_TARGET);
    }

    @Test
    void shouldWitchHealNeedToPreciseAValidTarget() {
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1));
        List<PlayerData> testList = List.of(new WitchPlayerData(memberList.get(0)), new PlayerData(memberList.get(1), RoleType.SIMPLE_VILLAGER));
        WitchTurn turn = new WitchTurn(testList, testList.get(1));
        // --When
        // --Then
        assertThatThrownBy(() -> turn.handleAction(memberList.get(0), memberList.get(0), ActionType.WITCH_SAVE))
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_WITCH_WRONG_SAVE);
    }

    @Test
    void shouldWitchHealRemoveAPlayerFromResultList() {
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1));
        List<PlayerData> testList = List.of(new WitchPlayerData(memberList.get(0)), new PlayerData(memberList.get(1), RoleType.SIMPLE_VILLAGER));
        WitchTurn turn = new WitchTurn(testList, testList.get(1));
        // --When
        assertThatNoException().isThrownBy(() -> turn.handleAction(memberList.get(0), memberList.get(1), ActionType.WITCH_SAVE));
        // --Then
        assertThat(turn.getResult()).isEmpty();
    }

    @Test
    void shouldWitchCannotUseKillIfAlreadyUsed() {
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1));
        List<PlayerData> testList = List.of(new WitchPlayerData(memberList.get(0)), new PlayerData(memberList.get(1), RoleType.SIMPLE_VILLAGER));
        WitchPlayerData witch = (WitchPlayerData) testList.get(0);
        WitchTurn turn = new WitchTurn(testList, testList.get(1));
        // --When
        witch.useKill();
        // --Then
        assertThatThrownBy(() -> turn.handleAction(memberList.get(0), memberList.get(0), ActionType.WITCH_KILL))
                .hasMessage(UserIntendedException.EXCEPTION_MESSAGE_POWER_ALREADY_USED);
    }

    @Test
    void shouldWitchKillAddAPlayerFromResultList() {
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1));
        List<PlayerData> testList = List.of(new WitchPlayerData(memberList.get(0)), new PlayerData(memberList.get(1), RoleType.SIMPLE_VILLAGER));
        WitchTurn turn = new WitchTurn(testList, testList.get(0));
        // --When
        assertThatNoException().isThrownBy(() -> turn.handleAction(memberList.get(0), memberList.get(1), ActionType.WITCH_KILL));
        // --Then
        assertThat(turn.getResult()).hasSize(2);
    }
}
