package game;

import bot.game.Composition;

public class CompositionTest {
    public static void main(String[] args) {
        for (int i = 2; i < 20; i++) {
            System.out.println(i+" joueurs " + new Composition(i));
        }
    }
}
