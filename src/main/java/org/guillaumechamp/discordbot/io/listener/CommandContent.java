package org.guillaumechamp.discordbot.io.listener;

import lombok.Builder;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.StringJoiner;


@Builder
@Getter
public class CommandContent {
    private final String commandType;
    private final Member author;
    private final JDA api;
    private final String stringArgument;
    private final Integer intArgument;
    private final Guild guild;
    private final Integer gameIndex;
    private final Member target;

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(" ")
                .add(author.getEffectiveName())
                .add('[' + guild.getName()+']')
                .add("used " + commandType);
        if (stringArgument!=null){
            joiner.add(stringArgument);
        }
        if (intArgument!=null){
            joiner.add(intArgument.toString());
        }
        if (gameIndex!=null){
            joiner.add("[game "+ gameIndex +']');
        }
        if (target!=null){
            joiner.add("against").add(target.getEffectiveName());
        }
        return joiner.toString();
    }
}
