package org.guillaumechamp.discordbot.testUtil;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public class TestAppender extends ListAppender<ILoggingEvent> {

    public void reset() {
        this.list.clear();
    }

    public boolean contains(String string, Level level) {
        return this.list
                .stream()
                .anyMatch(event -> event.toString().contains(string) && event.getLevel().equals(level));
    }

}
