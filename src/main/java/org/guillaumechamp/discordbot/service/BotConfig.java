package org.guillaumechamp.discordbot.service;

public class BotConfig {
    private static Boolean silenceMod = java.lang.Boolean.FALSE;

    private BotConfig() {
    }

    public static Boolean isSilence() {
        return silenceMod;
    }

    public static void changeBotMessagePolicy(boolean mute) {
        BotConfig.silenceMod = mute;
    }

}
