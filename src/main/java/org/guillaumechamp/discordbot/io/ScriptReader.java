package org.guillaumechamp.discordbot.io;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.event.Level;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.MissingResourceException;

public class ScriptReader {
    /***
     * enum to hold all possible entries in the script files (.properties)
     */
    public enum KeyEntry {
        VOTE("vote"),
        START_GAME("start"),
        ROLE_GIVEN("roleGiven",Tag.ROLE),
        START_SEER("seerStart"),
        START_SEER_PRIVATE("seerStartPrivate"),
        SEER_SPEC("seerSpec", Tag.ROLE),
        WOLF_START("wolfStart"),
        WITCH_PUBLIC("witchAction"),
        WITCH_SAVE("witchSave"),
        WITCH_KILL("witchKill", Tag.NAME),
        WITCH_NOTHING("witchNothing"),
        ELIMINATED("eliminated", Tag.NAME, Tag.ROLE),
        NO_DEATH("noDeath");
        public final String keyName;
        private final Tag[] tags;

        KeyEntry(String keyName, Tag... tags) {
            this.keyName = keyName;
            this.tags = tags;
        }

        public void assertTagsAreFilled(List<Tag> extractedTag) throws InvalidParameterException {
            if (tags.length > extractedTag.size()) {
                throw new InvalidParameterException("missing tag, expected : " + Arrays.toString(tags));
            }
            if (tags.length < extractedTag.size()) {
                throw new InvalidParameterException("too many tags, expected : " + Arrays.toString(tags));
            }
            for (Tag tag : tags) {
                if (!extractedTag.contains(tag)) {
                    throw new InvalidParameterException("tag " + tag + " is missing");
                }
            }
        }
    }

    public enum Tag {
        NAME("%name%"),
        ROLE("%role%");
        public final String tagName;

        Tag(String tagName) {
            this.tagName = tagName;
        }
    }

    public enum SupportedLanguage {EN, FR}

    private static final String PATH_FR = "src/main/resources/textFR.properties";
    private static final String PATH_EN = "src/main/resources/textEN.properties";

    private ScriptReader() {
    }

    /**
     * Allow to retrieve a particular game text using key and language
     *
     * @param keyEntry identifier of the text to prompt
     * @param language language to use, english by default
     * @return the targeted text
     * @throws InvalidParameterException if key is not found in file
     */
    public static String readLine(KeyEntry keyEntry, SupportedLanguage language) {
        if (!ArrayUtils.isEmpty(keyEntry.tags)) {
            BotLogger.log(Level.WARN,"No tag provided, expected :" + Arrays.toString(keyEntry.tags));
            throw new InvalidParameterException("No tag provided, expected :" + Arrays.toString(keyEntry.tags));
        }
        return unsafeReadLine(keyEntry, language);
    }

    @SafeVarargs
    public static String readLineAndParse(KeyEntry keyEntry, SupportedLanguage language, Pair<Tag, String>... wards) {
        keyEntry.assertTagsAreFilled(Arrays.stream(wards).map(Pair::getLeft).toList());
        String rawText = unsafeReadLine(keyEntry, language);
        for (Pair<Tag, String> ward : wards) {
            rawText = parse(rawText, ward.getLeft().tagName, ward.getRight());
        }
        return rawText;

    }

    private static String unsafeReadLine(KeyEntry keyEntry, SupportedLanguage language) {
        String path = language == SupportedLanguage.FR ? PATH_FR : PATH_EN;
        String text = PropertyReader.getPropertyFromFile(path, keyEntry.keyName);

        if (text == null) {
            String errorMessage = keyEntry.keyName + " not found in " + path;
            BotLogger.log(BotLogger.WARN, errorMessage);
            throw new MissingResourceException(errorMessage, path, keyEntry.keyName);
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
    private static String parse(String payload, String tag, String content) {
        if (!StringUtils.contains(payload, tag)) {
            BotLogger.log(BotLogger.WARN, tag + " not found in " + payload);
            return payload;
        }
        return StringUtils.replace(payload, tag, content);
    }
}
