package team.monroe.org.pocketfit.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.UUID;

import team.monroe.org.pocketfit.manage.RoutineManager;
import team.monroe.org.pocketfit.presentations.Routine;

public class GetRoutineById extends UserCaseSupport<String, Routine>{

    public GetRoutineById(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Routine executeImpl(String id) {
        return using(RoutineManager.class).get(id);
    }
}
