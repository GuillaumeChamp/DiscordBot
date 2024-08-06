package org.guillaumechamp.discordbot.io;

public class UserIntendedException extends Exception {
    public static final String EXCEPTION_MESSAGE_AUTHOR_NOT_IN_THE_GAME = "You are not in the game";
    public static final String EXCEPTION_MESSAGE_TARGET_NOT_IN_THE_GAME = "The target is not in the game";
    public static final String EXCEPTION_MESSAGE_ACTION_NOT_ALLOWED = "You are not authorized to use this action at this moment";
    public static final String EXCEPTION_MESSAGE_ACTION_EXPIRED = "This is too late, this action is no longer authorized";
    public static final String EXCEPTION_MESSAGE_WRONG_COMMAND = "You can use command now but not this one";
    public static final String EXCEPTION_MESSAGE_SEER_NO_SPEC = "You have spec no one";

    public UserIntendedException(String message) {
        super(message);
    }
}
