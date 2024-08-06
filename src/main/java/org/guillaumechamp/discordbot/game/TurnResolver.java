package org.guillaumechamp.discordbot.game;

import org.guillaumechamp.discordbot.game.turn.PlayerTurn;

public class TurnResolver {
    private TurnResolver(){}

    public static void triggerNextAction(Game game, PlayerTurn playerTurn) throws EndOfGameException {
        switch (playerTurn) {
            case SEER -> {
                game.afterSeer();
                game.beforeWolf();
            }
            case WOLF_VOTE -> game.beforeWitch();
            case WITCH -> {
                game.afterNight();
                game.beforeVillagerVote();
            }
            case VILLAGE_VOTE -> {
                game.afterVillagerVote();
                game.beforeSeer();
            }
            default -> //invalid state because NONE is a placeholder
                    throw new IllegalStateException("An Incomplete action enter the work flow");
        }
    }
}
