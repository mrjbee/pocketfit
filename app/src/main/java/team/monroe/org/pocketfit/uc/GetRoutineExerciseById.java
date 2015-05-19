package team.monroe.org.pocketfit.uc;

import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.ArrayList;

import team.monroe.org.pocketfit.manage.Persist;
import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.RoutineDay;
import team.monroe.org.pocketfit.presentations.RoutineExercise;

public class GetRoutineExerciseById extends UserCaseSupport<String, RoutineExercise>{

    public GetRoutineExerciseById(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected RoutineExercise executeImpl(String id) {
        Persist.RoutineExercise routineExercisePersist = using(PersistManager.class).getRoutineExercise(id);
        if (routineExercisePersist == null) return null;
        RoutineExercise answer = new RoutineExercise(routineExercisePersist.id);
        answer.exercise = using(Model.class).execute(GetExerciseById.class, routineExercisePersist.exerciseId);
        answer.exerciseDetails = routineExercisePersist.details;
        return answer;
    }
}
