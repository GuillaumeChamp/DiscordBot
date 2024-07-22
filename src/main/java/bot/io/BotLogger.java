package bot.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public class BotLogger {
    private static final Logger logger = LoggerFactory.getLogger(BotLogger.class);
    // copy log level to ensure single responsibility and hide logging lib from caller
    public static final Level INFO = Level.INFO;
    public static final Level DEBUG = Level.DEBUG;
    public static final Level ERROR = Level.ERROR;
    public static final Level WARN = Level.WARN;
    public static final Level FATAL = null;

    private BotLogger() {
    }

    /**
     * Log a message with bot logger as source.
     *
     * @param level   log level, use log level from BotLogger to hide logging lib
     * @param message message to log
     */
    public static void log(Level level, String message) {
        if (level == FATAL) {
            throw new RuntimeException("A fatal error has occurred, please check log");
        }
        if (BotConfig.noLog) {
            return;
        }
        logger.atLevel(level).log(message);
    }
}
