package bot.game.game.mechanism;

import bot.game.roles.EnhanceRoleType;
import bot.game.roles.Role;
import bot.game.roles.RoleManagement;
import bot.io.ProcessingException;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.List;

public class SeerAction extends Action{
    private final List<Role> result = new ArrayList<>();

    public SeerAction(List<Role> roles) {
        super(EnhanceRoleType.seer, roles);
        authorizedActions.add("see");
    }

    @Override
    public void handleAction(Member author, Member target, String action) throws ProcessingException {
        super.handleAction(author, target, action);
        result.add(RoleManagement.getRoleOf(roles,target.getId()));
        isActive=false;
    }

    @Override
    public List<Role> getResult() throws ProcessingException {
        return result;
    }
}
