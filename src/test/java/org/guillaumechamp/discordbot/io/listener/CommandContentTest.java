package org.guillaumechamp.discordbot.io.listener;

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
        CommandContent commandContent = CommandContent.builder()
                .api(DiscordTestUtil.getApi())
                .commandType("create")
                .guild(DiscordTestUtil.getTestChannel().getGuild())
                .author(DiscordTestUtil.getAMember(0))
                .stringArgument("dummy")
                .intArgument(42)
                .target(DiscordTestUtil.getAMember(0))
                .build();

        assertThat(commandContent.toString())
                .hasToString("Wolfy of Thiercelieux [Des Ingé et des jeux] used create");
    }

}
