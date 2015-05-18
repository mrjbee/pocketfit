package team.monroe.org.pocketfit.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.ArrayList;

import team.monroe.org.pocketfit.manage.Persist;
import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.Exercise;
import team.monroe.org.pocketfit.presentations.RoutineDay;

public class GetExerciseById extends UserCaseSupport<String, Exercise>{

    public GetExerciseById(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Exercise executeImpl(String id) {
        return using(PersistManager.class).getExercise(id);
    }
}
