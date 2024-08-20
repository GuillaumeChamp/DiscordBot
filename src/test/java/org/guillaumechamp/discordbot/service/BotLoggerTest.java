package org.guillaumechamp.discordbot.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.guillaumechamp.discordbot.testUtil.TestAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

class BotLoggerTest {
    private static TestAppender logMemory;

    @BeforeAll
    public static void setup() {
        Logger logger = (Logger) LoggerFactory.getLogger(BotLogger.class);
        logMemory = new TestAppender();
        logMemory.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(Level.DEBUG);
        logger.addAppender(logMemory);
        logMemory.start();
    }

    @Test
    void shouldInfoLogMatchToInfoLevel() {
        String logString = "info log";
        // --When
        BotLogger.info(logString);
        // --Then
        assertThat(logMemory.contains(logString, Level.INFO)).isTrue();
    }

    @Test
    void shouldErrorLogMatchToWarnLevel() {
        String logString = "error log";
        // --When
        BotLogger.error(logString);
        // --Then
        assertThat(logMemory.contains(logString, Level.WARN)).isTrue();
    }

    @Test
    void shouldFatalLogMatchToErrorLevel() {
        String logString = "fatal log";
        // --When
        BotLogger.fatal(logString);
        // --Then
        assertThat(logMemory.contains(logString, Level.ERROR)).isTrue();
    }

    @AfterEach
    void cleanup(){
        logMemory.reset();
    }
}
