package game;

import org.guillaumechamp.discordbot.game.Composition;
import org.guillaumechamp.discordbot.game.roles.EnhanceRoleType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CompositionTest {
    public static void main(String[] args) {
        for (int i = 4; i < 20; i++) {
            System.out.println(i + " joueurs " + new Composition(i));
        }
    }

    @Test
    public void shouldDistributionBeRandom() {
        //Assert for game size from 3 to 20
        for (int i = 4; i < 20; i++) {
            //roll 20 times
            List<EnhanceRoleType> first = new ArrayList<>(i * 10);
            List<EnhanceRoleType> second = new ArrayList<>(i * 10);
            for (int j = 0; j < i * 10; j++) {
                Composition composition = new Composition(i);
                first.add(composition.drawARole());
                second.add(composition.drawARole());
            }
            assertThat(first).contains(EnhanceRoleType.simpleVillager, EnhanceRoleType.simpleWolf);
            assertThat(second).contains(EnhanceRoleType.simpleVillager, EnhanceRoleType.simpleWolf);
        }
    }
}
