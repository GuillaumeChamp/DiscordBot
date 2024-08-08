package org.guillaumechamp.discordbot.game.roles;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.testUtil.DiscordTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CompositionTest {

    @ParameterizedTest(name = "should composition of {arguments} be random")
    @ValueSource(ints = {4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20})
    void shouldDistributionBeRandom(int i) {
        List<Member> members = Stream.iterate(0, k -> k + 1).limit(i).map(integer -> DiscordTestUtil.getAMember(0)).toList();
        List<PlayerData> first = new ArrayList<>(i * 10);
        List<PlayerData> second = new ArrayList<>(i * 10);
        for (int j = 0; j < 30; j++) { // probability to be wolf is 0.3
            List<PlayerData> players = Composition.assignRoles(members);
            first.add(players.get(0));
            second.add(players.get(1));
        }
        assertThat(first).extracting(PlayerData::getRole).contains(RoleType.SIMPLE_VILLAGER, RoleType.SIMPLE_WOLF);
        assertThat(second).extracting(PlayerData::getRole).contains(RoleType.SIMPLE_VILLAGER, RoleType.SIMPLE_WOLF);

    }

    @Test
    void shouldDistributionIncludeWitchAndSeerIfMoreThan4Players() {
        List<Member> members = Stream.iterate(0, i -> i + 1).limit(5).map(integer -> DiscordTestUtil.getAMember(0)).toList();
        List<PlayerData> players = Composition.assignRoles(members);
        assertThat(players).filteredOn(playerData -> playerData.getRole().equals(RoleType.SEER)).hasSize(1);
        assertThat(players).filteredOn(playerData -> playerData.getRole().equals(RoleType.WITCH)).hasSize(1);
    }
}
