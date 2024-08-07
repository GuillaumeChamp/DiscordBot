package org.guillaumechamp.discordbot.game.turn;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.roles.ActionType;
import org.guillaumechamp.discordbot.game.roles.RoleType;
import org.guillaumechamp.discordbot.game.roles.PlayerData;
import org.guillaumechamp.discordbot.io.UserIntendedException;
import org.guillaumechamp.discordbot.util.DiscordTestUtil;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


class VoteTurnTest {

    @Test
    void shouldVoteWorkOnlyForAllRoleAndWolf(){
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1), DiscordTestUtil.getAMember(2));
        List<PlayerData> testList = memberList.stream().map(member -> new PlayerData(member, RoleType.SIMPLE_VILLAGER)).toList();
        // --Then
        assertThatNoException().isThrownBy(()->new Vote(PlayerTurn.VILLAGE_VOTE, testList));
        assertThatNoException().isThrownBy(()->new Vote(PlayerTurn.WOLF_VOTE, testList));
        assertThatThrownBy(()->new Vote(PlayerTurn.SEER, testList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Vote can only handle WOLF_VOTE or VILLAGE_VOTE");

    }
    @Test
    void shouldGetResultNotThrowingExceptionIfUnanimousVote() throws UserIntendedException {
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1), DiscordTestUtil.getAMember(2));
        List<PlayerData> testList = memberList.stream().map(member -> new PlayerData(member, RoleType.SIMPLE_VILLAGER)).toList();
        Vote vote = new Vote(PlayerTurn.VILLAGE_VOTE, testList);
        // --When
        assertThatNoException().isThrownBy(() -> {
            vote.handleAction(memberList.get(0), memberList.get(0), ActionType.VOTE);
            vote.handleAction(memberList.get(1), memberList.get(0), ActionType.VOTE);
            vote.handleAction(memberList.get(2), memberList.get(0), ActionType.VOTE);
        });
        // --Then
        assertThat(vote.getResult())
                .hasSize(1)
                .element(0)
                .extracting(PlayerData::getOwner)
                .isEqualTo(memberList.get(0));
    }

    @Test
    void shouldGetResultThrowExceptionIfNoUnanimousVote() {
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1));
        List<PlayerData> testList = memberList.stream().map(member -> new PlayerData(member, RoleType.SIMPLE_VILLAGER)).toList();
        Vote vote = new Vote(PlayerTurn.VILLAGE_VOTE, testList);
        // --When
        assertThatNoException().isThrownBy(() -> {
            vote.handleAction(memberList.get(0), memberList.get(0), ActionType.VOTE);
            vote.handleAction(memberList.get(1), memberList.get(1), ActionType.VOTE);
        });
        // --Then
        assertThatThrownBy(vote::getResult).isInstanceOf(UserIntendedException.class)
                .hasMessage("The choice is not unanimous no one will be kill");
    }

    @Test
    void shouldGetResultThrowExceptionIfNoVote() {
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1));
        List<PlayerData> testList = memberList.stream().map(member -> new PlayerData(member, RoleType.SIMPLE_VILLAGER)).toList();
        Vote vote = new Vote(PlayerTurn.VILLAGE_VOTE, testList);
        // --When

        // --Then
        assertThatThrownBy(vote::getResult)
                .isInstanceOf(UserIntendedException.class)
                .hasMessage("The choice is not unanimous no one will be kill");
    }

    @Test
    void shouldThrowExceptionIfVoteForNoOne() {
        // --Given
        Member member = DiscordTestUtil.getAMember(0);
        List<PlayerData> testList = Collections.singletonList(new PlayerData(member, RoleType.SIMPLE_VILLAGER));
        Vote vote = new Vote(PlayerTurn.VILLAGE_VOTE, testList);
        // --Then
        assertThatThrownBy(() -> vote.handleAction(member, null, ActionType.VOTE))
                .isInstanceOf(UserIntendedException.class)
                .hasMessage("No player targeted");
    }

    @Test
    void shouldThrowExceptionIfVillagerVoteWhileWerewolfTurn() {
        // --Given
        Member member = DiscordTestUtil.getAMember(0);
        List<PlayerData> testList = Collections.singletonList(new PlayerData(member, RoleType.SIMPLE_VILLAGER));
        Vote vote = new Vote(PlayerTurn.WOLF_VOTE, testList);
        // --Then
        assertThatThrownBy(() -> vote.handleAction(member, member, ActionType.VOTE))
                .isInstanceOf(UserIntendedException.class)
                .hasMessage("You cannot vote");
    }
}
