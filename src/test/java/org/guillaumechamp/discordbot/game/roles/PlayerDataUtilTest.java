package org.guillaumechamp.discordbot.game.roles;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.EndOfGameException;
import org.guillaumechamp.discordbot.testUtil.DiscordTestUtil;
import org.junit.jupiter.api.Test;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PlayerDataUtilTest {

    @Test
    void shouldVillagerWinByEliminateAllWolfAndSolo() {
        // --Given
        List<PlayerData> players = createSampleComposition();
        // -- When
        players.remove(1);
        players.remove(0);
        // --Then
        assertThatNoException().isThrownBy(() -> PlayerDataUtil.checkWin(players));
        players.remove(0);
        assertThatThrownBy(() -> PlayerDataUtil.checkWin(players))
                .isInstanceOf(EndOfGameException.class)
                .hasMessageContaining("Villager win !");
    }

    @Test
    void shouldWolfWinByEliminateAllVillagerAndSolo() {
        // --Given
        List<PlayerData> players = createSampleComposition();
        // -- When
        List<PlayerData> remaining = players.subList(0, 3);
        // --Then
        assertThatNoException().isThrownBy(() -> PlayerDataUtil.checkWin(players));
        remaining.remove(2);
        assertThatThrownBy(() -> PlayerDataUtil.checkWin(remaining))
                .isInstanceOf(EndOfGameException.class)
                .hasMessageContaining("Werewolf win !");
    }

    @Test
    void shouldSoloWinByEliminateEveryOne() {
        // --Given
        List<PlayerData> players = createSampleComposition();
        // -- When
        List<PlayerData> remaining = players.subList(1, 3);
        // --Then
        assertThatNoException().isThrownBy(() -> PlayerDataUtil.checkWin(players));
        remaining.remove(0);
        assertThatThrownBy(() -> PlayerDataUtil.checkWin(remaining))
                .isInstanceOf(EndOfGameException.class)
                .hasMessageContaining("A player win alone !");
    }

    @Test
    void shouldIsNotInReturnTrueIfMemberIsNotInList() {
        // --Given
        PlayerData playerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.SIMPLE_VILLAGER);
        List<PlayerData> players = Collections.singletonList(playerData);
        Member notInMember = DiscordTestUtil.getAMember(1);
        // --Then
        assertThat(PlayerDataUtil.isMemberNotIn(players, notInMember)).isTrue();
    }

    @Test
    void shouldIsNotInReturnFalseIfMemberIsInList() {
        // --Given
        PlayerData playerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.SIMPLE_VILLAGER);
        List<PlayerData> players = Collections.singletonList(playerData);
        // --Then
        assertThat(PlayerDataUtil.isMemberNotIn(players, playerData.getOwner())).isFalse();
    }

    @Test
    void shouldIsNotInReturnTrueIfMemberIsNull() {
        // --Given
        PlayerData playerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.SIMPLE_VILLAGER);
        List<PlayerData> players = Collections.singletonList(playerData);
        // --Then
        assertThat(PlayerDataUtil.isMemberNotIn(players, null)).isTrue();
    }

    @Test
    void shouldIsNotInReturnTrueIfListIsNull() {
        // --Given
        Member inMember = DiscordTestUtil.getAMember(0);
        // --Then
        assertThat(PlayerDataUtil.isMemberNotIn(null, inMember)).isTrue();
    }

    @Test
    void shouldIsMemberAReturnTrueIfMemberIsA() {
        // --Given
        PlayerData witchPlayerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.SEER);
        List<PlayerData> players = Collections.singletonList(witchPlayerData);
        // --Then
        assertThat(PlayerDataUtil.isMemberA(players, witchPlayerData.getOwner(), RoleType.SEER)).isTrue();
    }

    @Test
    void shouldIsMemberAReturnFalseIfMemberIsNotA() {
        // --Given
        WitchPlayerData witchPlayerData = new WitchPlayerData(DiscordTestUtil.getAMember(0));
        List<PlayerData> players = Collections.singletonList(witchPlayerData);
        // --Then
        assertThat(PlayerDataUtil.isMemberA(players, witchPlayerData.getOwner(), RoleType.SIMPLE_VILLAGER)).isFalse();
    }

    @Test
    void shouldIsMemberAReturnTrueIfMemberIsALL() {
        // --Given
        PlayerData playerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.SIMPLE_VILLAGER);
        List<PlayerData> players = Collections.singletonList(playerData);
        // --Then
        assertThat(PlayerDataUtil.isMemberA(players, playerData.getOwner(), RoleType.ALL)).isTrue();
    }

    @Test
    void shouldIsMemberAThrowExceptionIfNotFound() {
        // --Given
        PlayerData playerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.ALL);
        List<PlayerData> players = Collections.singletonList(playerData);
        Member notInMember = DiscordTestUtil.getAMember(1);
        // --Then
        assertThatThrownBy(() -> PlayerDataUtil.isMemberA(players, notInMember, RoleType.SIMPLE_VILLAGER))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("player not found");
    }

    @Test
    void shouldIsMemberAThrowExceptionIfMemberIsNull() {
        // --Given
        PlayerData playerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.ALL);
        List<PlayerData> players = Collections.singletonList(playerData);
        // --Then
        assertThatThrownBy(() -> PlayerDataUtil.isMemberA(players, null, RoleType.SIMPLE_VILLAGER))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("players or member is null");
    }

    @Test
    void shouldIsMemberAThrowExceptionIfPlayersIsNull() {
        // --Given
        Member member = DiscordTestUtil.getAMember(0);
        // --Then
        assertThatThrownBy(() -> PlayerDataUtil.isMemberA(null, member, RoleType.SIMPLE_VILLAGER))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("players or member is null");
    }

    @Test
    void shouldIsMemberInThatSideReturnTrueIfMemberIs() {
        // --Given
        PlayerData playerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.SIMPLE_VILLAGER);
        List<PlayerData> players = Collections.singletonList(playerData);
        // --Then
        assertThat(PlayerDataUtil.isMemberInThatSide(players, playerData.getOwner(), RoleSide.VILLAGER)).isTrue();
    }

    @Test
    void shouldIsMemberInThatSideReturnFalseIfMemberIsNot() {
        // --Given
        PlayerData playerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.SIMPLE_VILLAGER);
        List<PlayerData> players = Collections.singletonList(playerData);
        // --Then
        assertThat(PlayerDataUtil.isMemberInThatSide(players, playerData.getOwner(), RoleSide.WEREWOLF)).isFalse();
    }

    @Test
    void shouldIsMemberInThatSideReturnFalseIfMemberIsNotInList() {
        // --Given
        PlayerData playerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.SIMPLE_VILLAGER);
        List<PlayerData> players = Collections.singletonList(playerData);
        Member notInMember = DiscordTestUtil.getAMember(1);
        // --Then
        assertThat(PlayerDataUtil.isMemberInThatSide(players, notInMember, RoleSide.WEREWOLF)).isFalse();
    }

    @Test
    void shouldIsMemberInThatSideReturnFalseIfMemberIsNull() {
        // --Given
        PlayerData playerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.SIMPLE_VILLAGER);
        List<PlayerData> players = Collections.singletonList(playerData);
        // --Then
        assertThat(PlayerDataUtil.isMemberInThatSide(players, null, RoleSide.WEREWOLF)).isFalse();
    }

    @Test
    void shouldIsMemberInThatSideReturnFalseIfListIsNull() {
        // --Given
        Member notInMember = DiscordTestUtil.getAMember(0);
        // --Then
        assertThat(PlayerDataUtil.isMemberInThatSide(null, notInMember, RoleSide.WEREWOLF)).isFalse();
    }

    @Test
    void shouldIsRoleNotInReturnFalseIfRoleIsIn() {
        // --Given
        PlayerData playerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.SIMPLE_VILLAGER);
        List<PlayerData> players = Collections.singletonList(playerData);
        // --Then
        assertThat(PlayerDataUtil.isRoleNotIn(players, RoleType.SIMPLE_VILLAGER)).isFalse();
    }

    @Test
    void shouldIsRoleNotInReturnTrueIfRoleIsNotInList() {
        // --Given
        PlayerData playerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.SIMPLE_VILLAGER);
        List<PlayerData> players = Collections.singletonList(playerData);
        // --Then
        assertThat(PlayerDataUtil.isRoleNotIn(players, RoleType.WITCH)).isTrue();
    }

    @Test
    void shouldIsRoleNotInReturnTrueIfListIsNull() {
        // --Then
        assertThat(PlayerDataUtil.isRoleNotIn(null, RoleType.WITCH)).isTrue();
    }

    @Test
    void shouldGetRoleByMemberIdReturnValue() {
        // --Given
        PlayerData playerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.SIMPLE_VILLAGER);
        List<PlayerData> players = Collections.singletonList(playerData);
        // --Then
        assertThat(PlayerDataUtil.getRoleByMemberId(players, playerData.getOwner().getId()))
                .isEqualTo(playerData);
    }

    @Test
    void shouldGetRoleByMemberIdThrowExceptionIfNotFound() {
        // --Given
        PlayerData playerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.SIMPLE_VILLAGER);
        List<PlayerData> players = Collections.singletonList(playerData);
        // --When
        String notInMemberId = DiscordTestUtil.getAMember(1).getId();
        // --Then
        assertThatThrownBy(() -> PlayerDataUtil.getRoleByMemberId(players, notInMemberId))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("This player is not in this list");
    }

    @Test
    void shouldGetRoleByMemberIdThrowExceptionIfPlayersIsNull() {
        // --Given
        String memberId = DiscordTestUtil.getAMember(0).getId();
        // --Then
        assertThatThrownBy(() -> PlayerDataUtil.getRoleByMemberId(null, memberId))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("null parameter not allowed");
    }

    @Test
    void shouldGetRoleByMemberIdThrowExceptionIfMemberIdIsNull() {
        // --Given
        PlayerData playerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.SIMPLE_VILLAGER);
        List<PlayerData> players = Collections.singletonList(playerData);
        // --Then
        assertThatThrownBy(() -> PlayerDataUtil.getRoleByMemberId(players, null))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("null parameter not allowed");
    }

    @Test
    void shouldGetAllMembersBySideReturnAListOfMatchingPlayers() {
        // --Given
        PlayerData playerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.SIMPLE_VILLAGER);
        PlayerData wolfData = new PlayerData(DiscordTestUtil.getAMember(1), RoleType.SIMPLE_WOLF);
        List<PlayerData> players = List.of(playerData, playerData, wolfData, wolfData, wolfData, wolfData);
        // --Then
        assertThat(PlayerDataUtil.getAllMembersBySide(players, RoleSide.WEREWOLF))
                .hasSize(4)
                .allSatisfy(input -> assertThat(input).isEqualTo(wolfData.getOwner()));
    }

    @Test
    void shouldGetAllMembersBySideReturnEmptyListIfContainNone() {
        // --Given
        PlayerData playerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.SIMPLE_VILLAGER);
        List<PlayerData> players = List.of(playerData, playerData);
        // --Then
        assertThat(PlayerDataUtil.getAllMembersBySide(players, RoleSide.WEREWOLF)).isEmpty();
    }

    @Test
    void shouldGetAllMembersBySideReturnEmptyListIfPlayersIsNull() {
        // --Then
        assertThat(PlayerDataUtil.getAllMembersBySide(null, RoleSide.WEREWOLF)).isEmpty();
    }

    //
    @Test
    void shouldGetPlayerDataByRoleReturnThePlayerFound() {
        // --Given
        PlayerData playerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.SIMPLE_VILLAGER);
        PlayerData wolfData = new PlayerData(DiscordTestUtil.getAMember(1), RoleType.SIMPLE_WOLF);
        List<PlayerData> players = List.of(playerData, wolfData);
        // --Then
        assertThat(PlayerDataUtil.getPlayerDataByRole(players, RoleType.SIMPLE_WOLF))
                .isEqualTo(wolfData);
    }

    @Test
    void shouldGetPlayerDataByRoleThrowExceptionIfNotFound() {
        // --Given
        PlayerData playerData = new PlayerData(DiscordTestUtil.getAMember(0), RoleType.SIMPLE_VILLAGER);
        List<PlayerData> players = Collections.singletonList(playerData);
        // --Then
        assertThatThrownBy(() -> PlayerDataUtil.getPlayerDataByRole(players, RoleType.SIMPLE_WOLF))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("this role is not in the list");
    }

    @Test
    void shouldGetPlayerDataByRoleThrowExceptionIfNullCollection() {
        // --Then
        assertThatThrownBy(() -> PlayerDataUtil.getPlayerDataByRole(null, RoleType.SIMPLE_WOLF))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("null collection of player not allowed");
    }

    /**
     * Create an 8 players composition with two wolf at first position
     *
     * @return the composition
     */
    public List<PlayerData> createSampleComposition() {
        List<PlayerData> ans = new ArrayList<>(8);
        ans.add(new PlayerData(DiscordTestUtil.getAMember(0), RoleType.SIMPLE_WOLF));
        ans.add(new PlayerData(DiscordTestUtil.getAMember(1), RoleType.SIMPLE_WOLF));
        ans.add(new PlayerData(DiscordTestUtil.getAMember(1), RoleType.NONE));
        for (int i = 0; i < 5; i++) {
            ans.add(new PlayerData(DiscordTestUtil.getAMember(2), RoleType.SIMPLE_VILLAGER));
        }
        return ans;
    }
}
