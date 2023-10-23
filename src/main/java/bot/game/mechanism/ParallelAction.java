package bot.game.mechanism;

import bot.game.roles.Role;
import bot.io.ProcessingException;
import net.dv8tion.jda.api.entities.Member;

import java.util.List;

public interface ParallelAction {
    /**
     * Handle an action while the game is waiting for actions
     *
     * @param author the member who perform the action
     * @param target the target designed
     * @param action the command which trigger the event (use to check if authorized now)
     * @throws ProcessingException in different case
     */
    void handleAction(Member author, Member target, String action) throws ProcessingException;

    default Integer getDuration() {
        return 60;
    }

    List<Role> getResult() throws ProcessingException;

    void terminate();

    boolean isActive();
}
