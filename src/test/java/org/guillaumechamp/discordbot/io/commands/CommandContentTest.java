package org.guillaumechamp.discordbot.io.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.guillaumechamp.discordbot.testUtil.DiscordTestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CommandContentTest {

    @Test
    void shouldValueBeNullIfNotSpecified(){
        CommandContent commandContent = CommandContent.builder().build();
        assertThat(commandContent.getApi()).isNull();
        assertThat(commandContent.getGuild()).isNull();
        assertThat(commandContent.getAuthor()).isNull();
        assertThat(commandContent.getCommandType()).isNull();
        assertThat(commandContent.getStringArgument()).isNull();
        assertThat(commandContent.getIntArgument()).isNull();
        assertThat(commandContent.getGameIndex()).isNull();
        assertThat(commandContent.getTarget()).isNull();
    }

    @Test
    void shouldGeneralCommandBeParsedCorrectly(){
        Member testMember = DiscordTestUtil.getAMember(0);
        Guild guild = DiscordTestUtil.getTestChannel().getGuild();
        CommandContent commandContent = CommandContent.builder()
                .api(DiscordTestUtil.getApi())
                .commandType("create")
                .guild(guild)
                .author(testMember)
                .build();

        assertThat(commandContent.toString())
                .hasToString(testMember.getEffectiveName() + " [" +guild.getName()+"] used create");
    }

    @Test
    void shouldCommandContentBeParsedInTheRightOrder(){
        Member testMember = DiscordTestUtil.getAMember(0);
        CommandContent commandContent = CommandContent.builder()
                .api(DiscordTestUtil.getApi())
                .commandType("create")
                .guild(DiscordTestUtil.getTestChannel().getGuild())
                .author(testMember)
                .stringArgument("dummy")
                .intArgument(42)
                .gameIndex(-1)
                .target(testMember)
                .build();

        assertThat(commandContent.toString())
                .hasToString(testMember.getEffectiveName() + " [Des Ingé et des jeux] used create dummy 42 [game -1] against " + testMember.getEffectiveName());
    }

}
