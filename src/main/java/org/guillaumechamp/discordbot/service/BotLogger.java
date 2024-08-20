package org.guillaumechamp.discordbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotLogger {
    private static final Logger logger = LoggerFactory.getLogger(BotLogger.class);

    private BotLogger() {
    }

    // copy log level to ensure single responsibility and hide logging lib from caller
    public static void info(String message) {
        logger.info(message);
    }

    public static void error(String message) {
        logger.warn(message);
    }

    public static void fatal(String message) {
        logger.error(message);
    }
}
