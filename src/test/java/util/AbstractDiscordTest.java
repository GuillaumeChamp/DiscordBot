package util;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractDiscordTest {
    protected static TextChannel testChannel;

    @BeforeAll
    public static void setup() {
        testChannel = DiscordTestUtil.createTestChannel();
    }

    @AfterAll
    public static void tearsDown() {
        testChannel.delete().queue();
    }
}
