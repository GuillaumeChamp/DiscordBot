package bot.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertyReader {
    private static final String BOT_DATA_PATH = "src/main/resources/botConfiguration.properties";
    private static final String TEXT_FR_PATH = "src/main/resources/textFR.properties";
    private static final String TEXT_EN_PATH = "src/main/resources/textEN.properties";

    private PropertyReader(){}

    /**
     * Allow to retrieve value of a key in botConfiguration.properties
     * @param key key int the file
     */
    public static String getBotProperty(String key) throws IOException {
        Properties properties = new Properties();
        try (InputStream stream = Files.newInputStream(Paths.get(BOT_DATA_PATH))) {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return properties.getProperty(key);
    }

    public static String getGameText(String key, boolean isFR) throws IOException {
        Properties properties = new Properties();
        String path = TEXT_EN_PATH;
        if (isFR){
            path = TEXT_FR_PATH;
        }
        try (InputStream stream = Files.newInputStream(Paths.get(path))) {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return properties.getProperty(key);
    }
}
