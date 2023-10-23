package bot.game.mechanism;

import bot.game.roles.EnhanceRoleType;
import bot.game.roles.Role;
import bot.game.roles.RoleManagement;
import bot.io.ProcessingException;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.List;

public class WitchAction extends Action {
    private static final int DEFAULT_DURATION = 15;
    ArrayList<Role> deadPerson = new ArrayList<>();

    public WitchAction(EnhanceRoleType author, List<Role> roles, Role eliminated) {
        super(author, roles, DEFAULT_DURATION);
        authorizedActions.add("kill");
        authorizedActions.add("save");
        deadPerson.add(eliminated);
    }

    @Override
    public void handleAction(Member author, Member target, String action) throws ProcessingException {
        super.handleAction(author, target, action);
        if (action.equals("save")) deadPerson.remove(0);
        if (action.equals("kill")) deadPerson.add(RoleManagement.getRoleOf(roles, target.getId()));
    }

    @Override
    public ArrayList<Role> getResult() throws ProcessingException {
        this.isActive = false;
        return deadPerson;

    }
}
