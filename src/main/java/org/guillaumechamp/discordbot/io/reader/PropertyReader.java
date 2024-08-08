package org.guillaumechamp.discordbot.io.reader;

import org.guillaumechamp.discordbot.service.BotLogger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Properties;

public class PropertyReader {
    private static final String BOT_DATA_PATH = "src/main/resources/botConfiguration.properties";

    private PropertyReader() {
    }

    /**
     * Allow to retrieve value of a key in botConfiguration.properties
     *
     * @param key key to read
     * @return null if property not found
     */
    public static String getBotPropertyFromFile(String key) {
        return getPropertyFromFile(BOT_DATA_PATH, key);
    }

    /**
     * Read a property from a file of the given path
     *
     * @param path file path
     * @param key  key to read
     * @return null if property not found or else the value
     */
    public static String getPropertyFromFile(String path, String key) {
        try (InputStream stream = Files.newInputStream(Paths.get(path))) {
            Properties properties = new Properties();
            properties.load(stream);
            return properties.getProperty(key);
        } catch (IOException e) {
            String errorMessage = "Unable to open property file " + path;
            BotLogger.log(BotLogger.FATAL, errorMessage);
            throw new NoSuchElementException(e);
        }
    }
}
