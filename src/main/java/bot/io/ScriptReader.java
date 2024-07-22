package bot.io;

import org.apache.commons.lang3.StringUtils;

import java.security.InvalidParameterException;

public class ScriptReader {
    public enum TextLanguage {EN, FR}

    private static final String PATH_FR = "src/main/resources/textFR.properties";
    private static final String PATH_EN = "src/main/resources/textEN.properties";

    private ScriptReader() {
    }

    /**
     * Allow to retrieve a particular game text using key and language
     *
     * @param key      identifier of the text to prompt
     * @param language language to use, english by default
     * @return the targeted text
     * @throws InvalidParameterException if key is not found in file
     */
    public static String readLine(String key, TextLanguage language) throws InvalidParameterException {
        String path = language == TextLanguage.FR ? PATH_FR : PATH_EN;
        String text = PropertyReader.getPropertyFromFile(path, key);
        if (text == null) {
            String errorMessage = key + " not found in " + path;
            BotLogger.log(BotLogger.WARN, errorMessage);
            throw new InvalidParameterException(errorMessage);
        }
        return text;
    }

    /**
     * Parse a line and replace all tag by the value
     *
     * @param payload raw string from file
     * @param tag     identifier
     * @param content value
     * @return the new string if ward exist, the input string if ward not found and log it to bot log
     */
    public static String parse(String payload, String tag, String content) {
        if (!StringUtils.contains(payload, tag)) {
            BotLogger.log(BotLogger.WARN, tag + " not found in " + payload);
            return payload;
        }
        return StringUtils.replace(payload, tag, content);
    }
}
