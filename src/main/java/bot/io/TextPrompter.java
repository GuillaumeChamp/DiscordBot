package bot.io;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.logging.Level;

public class TextPrompter {
    public enum TextLanguage {EN, FR}

    private static final String PATH_FR = "src/main/resources/textFR.properties";
    private static final String PATH_EN = "src/main/resources/textEN.properties";

    // Singleton
    private TextPrompter() {
    }

    /**
     * Allow to retrieve a particular game text using key and language
     *
     * @param key      identifier of the text to prompt
     * @param language language to use, english by default
     * @return the targeted text
     * @throws InvalidParameterException if key is not found in file
     */
    public static String prompt(String key, TextLanguage language) throws InvalidParameterException {
        String path;
        switch (language) {
            case EN:
                path = PATH_EN;
                break;
            case FR:
                path = PATH_FR;
                break;
            default:
                path = PATH_EN;
                break;
        }
        String text;
        try {
            text = PropertyReader.getProperty(path, key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (text == null) {
            String errorMessage = key + " not found in " + path;
            BotLogger.log(Level.WARNING, errorMessage);
            throw new InvalidParameterException(errorMessage);
        }
        return text;
    }

    /**
     * Replace a ward by content
     *
     * @param payload raw string from file
     * @param ward    ward to replace
     * @param content replacement char
     * @return the new string if ward exist, the input string if ward not found and log it to bot log
     */
    public static String parse(String payload, String ward, String content) {
        if (!StringUtils.contains(payload, ward)) {
            BotLogger.log(Level.INFO, ward + " not found in " + payload);
            return payload;
        }
        return StringUtils.replace(payload, ward, content);
    }
}
