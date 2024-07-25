package org.guillaumechamp.discordbot.game;

import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.game.mechanism.ActionType;
import org.guillaumechamp.discordbot.game.mechanism.Vote;
import org.guillaumechamp.discordbot.game.roles.EnhanceRoleType;
import org.guillaumechamp.discordbot.game.roles.Role;
import org.guillaumechamp.discordbot.io.ProcessingException;
import org.guillaumechamp.discordbot.util.DiscordTestUtil;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


class VoteTest {
    @Test
    void shouldGetResultNotThrowingExceptionIfUnanimousVote() {
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1), DiscordTestUtil.getAMember(2));
        List<Role> testList = memberList.stream().map(member -> new Role(member, EnhanceRoleType.SIMPLE_VILLAGER)).toList();
        Vote vote = new Vote(Vote.VoteType.ALL, testList);
        // --When
        assertThatCode(() -> {
            vote.handleAction(memberList.get(0), memberList.get(0), ActionType.VOTE);
            vote.handleAction(memberList.get(1), memberList.get(0), ActionType.VOTE);
            vote.handleAction(memberList.get(2), memberList.get(0), ActionType.VOTE);
        }).doesNotThrowAnyException();
        // --Then
        assertThat(assertDoesNotThrow(vote::getResult))
                .hasSize(1)
                .element(0)
                .extracting(Role::getOwner)
                .isEqualTo(memberList.get(0));
    }

    @Test
    void shouldGetResultThrowExceptionIfNoUnanimousVote() {
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1));
        List<Role> testList = memberList.stream().map(member -> new Role(member, EnhanceRoleType.SIMPLE_VILLAGER)).toList();
        Vote vote = new Vote(Vote.VoteType.ALL, testList);
        // --When
        assertThatCode(() -> {
            vote.handleAction(memberList.get(0), memberList.get(0), ActionType.VOTE);
            vote.handleAction(memberList.get(1), memberList.get(1), ActionType.VOTE);
        }).doesNotThrowAnyException();
        // --Then
        assertThatThrownBy(vote::getResult).isInstanceOf(ProcessingException.class)
                .hasMessage("The choice is not unanimous no one will be kill");
    }

    @Test
    void shouldGetResultThrowExceptionIfNoVote() {
        // --Given
        List<Member> memberList = List.of(DiscordTestUtil.getAMember(0), DiscordTestUtil.getAMember(1));
        List<Role> testList = memberList.stream().map(member -> new Role(member, EnhanceRoleType.SIMPLE_VILLAGER)).toList();
        Vote vote = new Vote(Vote.VoteType.ALL, testList);
        // --When

        // --Then
        assertThatThrownBy(vote::getResult)
                .isInstanceOf(ProcessingException.class)
                .hasMessage("The choice is not unanimous no one will be kill");
    }

    @Test
    void shouldThrowExceptionIfVoteForNoOne() {
        // --Given
        Member member = DiscordTestUtil.getAMember(0);
        List<Role> testList = Collections.singletonList(new Role(member, EnhanceRoleType.SIMPLE_VILLAGER));
        Vote vote = new Vote(Vote.VoteType.ALL, testList);
        // --Then
        assertThatThrownBy(() -> vote.handleAction(member, null, ActionType.VOTE))
                .isInstanceOf(ProcessingException.class)
                .hasMessage("No player targeted");
    }

    @Test
    void shouldThrowExceptionIfVillagerVoteWhileWerewolfTurn() {
        // --Given
        Member member = DiscordTestUtil.getAMember(0);
        List<Role> testList = Collections.singletonList(new Role(member, EnhanceRoleType.SIMPLE_VILLAGER));
        Vote vote = new Vote(Vote.VoteType.WEREWOLF, testList);
        // --Then
        assertThatThrownBy(() -> vote.handleAction(member, member, ActionType.VOTE))
                .isInstanceOf(ProcessingException.class)
                .hasMessage("You cannot vote");
    }
}
