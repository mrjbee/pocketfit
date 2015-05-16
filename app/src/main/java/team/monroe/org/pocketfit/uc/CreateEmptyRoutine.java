package team.monroe.org.pocketfit.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.UUID;

import team.monroe.org.pocketfit.manage.RoutineManager;
import team.monroe.org.pocketfit.presentations.Routine;

public class CreateEmptyRoutine extends UserCaseSupport<Void, Routine>{

    public CreateEmptyRoutine(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Routine executeImpl(Void request) {
        String id = UUID.randomUUID().toString();
        Routine routine = new Routine(id);
        using(RoutineManager.class).updateOrCreate(routine);
        return routine;
    }
}
