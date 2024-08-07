package org.guillaumechamp.discordbot.game.roles;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.EndOfGameException;
import org.guillaumechamp.discordbot.util.DiscordTestUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PlayerDataUtilTest {

    @Test
    void shouldVillagerWinByEliminateAllWolfAndSolo() {
        // --Given
        List<PlayerData> roles = createSampleComposition();
        // -- When
        roles.remove(1);
        roles.remove(0);
        // --Then
        assertThatNoException().isThrownBy(() -> PlayerDataUtil.checkWin(roles));
        roles.remove(0);
        assertThatThrownBy(() -> PlayerDataUtil.checkWin(roles))
                .isInstanceOf(EndOfGameException.class)
                .hasMessageContaining("Villager win !");
    }

    @Test
    void shouldWolfWinByEliminateAllVillagerAndSolo() {
        // --Given
        List<PlayerData> roles = createSampleComposition();
        // -- When
        List<PlayerData> remaining = roles.subList(0, 3);
        // --Then
        assertThatNoException().isThrownBy(() -> PlayerDataUtil.checkWin(roles));
        remaining.remove(2);
        assertThatThrownBy(() -> PlayerDataUtil.checkWin(remaining))
                .isInstanceOf(EndOfGameException.class)
                .hasMessageContaining("Werewolf win !");
    }

    @Test
    void shouldSoloWinByEliminateEveryOne() {
        // --Given
        List<PlayerData> roles = createSampleComposition();
        // -- When
        List<PlayerData> remaining = roles.subList(1, 3);
        // --Then
        assertThatNoException().isThrownBy(() -> PlayerDataUtil.checkWin(roles));
        remaining.remove(0);
        assertThatThrownBy(() -> PlayerDataUtil.checkWin(remaining))
                .isInstanceOf(EndOfGameException.class)
                .hasMessageContaining("A player win alone !");
    }

    @Test
    void shouldIsNotInReturnTrueIfMemberIsNotInList() {
        // --Given
        List<PlayerData> roles = createSampleComposition();
        Member notInMember = DiscordTestUtil.getAMember(3);
        // --Then
        assertThat(PlayerDataUtil.isMemberNotIn(roles,notInMember)).isTrue();
    }

    @Test
    void shouldIsNotInReturnFalseIfMemberIsInList() {
        // --Given
        List<PlayerData> roles = createSampleComposition();
        Member inMember = DiscordTestUtil.getAMember(0);
        // --Then
        assertThat(PlayerDataUtil.isMemberNotIn(roles,inMember)).isFalse();
    }

    @Test
    void shouldIsNotInReturnTrueIfMemberIsNull() {
        // --Given
        List<PlayerData> roles = createSampleComposition();
        // --Then
        assertThat(PlayerDataUtil.isMemberNotIn(roles,null)).isTrue();
    }

    @Test
    void shouldIsNotInReturnTrueIfListIsNull() {
        // --Given
        Member inMember = DiscordTestUtil.getAMember(0);
        // --Then
        assertThat(PlayerDataUtil.isMemberNotIn(null,inMember)).isTrue();
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
