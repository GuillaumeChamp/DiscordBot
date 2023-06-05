package bot.game;

import bot.io.ProcessingException;
import net.dv8tion.jda.api.entities.Member;


public interface GameType {
    GameType startGame() throws ProcessingException;
    void addPlayer(Member member) throws ProcessingException ;
    int getGameId();
}
