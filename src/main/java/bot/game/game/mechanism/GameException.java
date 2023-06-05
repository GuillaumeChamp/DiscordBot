package bot.game.game.mechanism;

import bot.game.roles.Role;
import bot.io.ProcessingException;

import java.util.List;

public class GameException extends ProcessingException {
    final List<Role> winningTeam;
    public GameException(String message, List<Role> composition) {
        super(message);
        this.winningTeam = composition;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "\nRemaining roles : "+ winningTeam.toString();
    }
}
