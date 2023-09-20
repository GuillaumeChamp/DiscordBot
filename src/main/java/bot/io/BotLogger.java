package bot.io;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class BotLogger {
    private static BotLogger instance;
    Logger logger;

    private BotLogger() {
        logger = Logger.getLogger(BotLogger.class.getName());
        try {
            logger.addHandler(generateFileHandler());
        } catch (IOException e) {
            logger.warning("Unable to create file for bot log");
        }
    }

    private FileHandler generateFileHandler() throws IOException {
        String generatedName = "botLog" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm")) + ".txt";
        FileHandler fileHandler = new FileHandler(generatedName, 524288000, // 500 MB max size
                1, // one log file at a time
                true // if it exists: append, don't overwrite
        );
        fileHandler.setFormatter(new SimpleFormatter());
        return fileHandler;
    }

    public static void log(Level level, String message) {
        if (BotConfig.noLog) {
            return;
        }
        if (instance == null) {
            instance = new BotLogger();
        }
        if (Level.SEVERE.equals(level)) {
            throw new RuntimeException("A fatal error has occurred, please check log");
        }
        instance.logger.log(level, message);
    }
}
