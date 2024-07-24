package org.guillaumechamp.discordbot.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public class BotLogger {
    private static final Logger logger = LoggerFactory.getLogger(BotLogger.class);
    // copy log level to ensure single responsibility and hide logging lib from caller
    public static final Level INFO = Level.INFO;
    public static final Level DEBUG = Level.DEBUG;
    public static final Level WARN = Level.WARN;
    public static final Level FATAL = Level.ERROR;

    private BotLogger() {
    }

    /**
     * Log a message with bot logger as source.
     *
     * @param level   log level, use log level from BotLogger to hide logging lib
     * @param message message to log
     */
    public static void log(Level level, String message) {
        logger.atLevel(level).log(message);
    }
}
