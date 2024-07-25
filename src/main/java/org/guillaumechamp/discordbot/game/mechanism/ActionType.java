package org.guillaumechamp.discordbot.game.mechanism;

import static org.guillaumechamp.discordbot.io.listener.CommandStore.*;

public enum ActionType {
    VOTE, SEER_SEE, WITCH_KILL, WITCH_SAVE;

    public static ActionType stringToActionType(String string) {
        switch (string) {
            case VOTE_COMMAND -> {
                return VOTE;
            }
            case SEE_COMMAND -> {
                return SEER_SEE;
            }
            case KILL_COMMAND -> {
                return WITCH_KILL;
            }
            case SAVE_COMMAND -> {
                return WITCH_SAVE;
            }
            default -> throw new IllegalArgumentException("ActionType string [" + string + "] not recognized.");
        }
    }
}
