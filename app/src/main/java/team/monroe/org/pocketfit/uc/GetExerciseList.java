package team.monroe.org.pocketfit.uc;

import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.Exercise;
import team.monroe.org.pocketfit.presentations.Routine;

public class GetExerciseList extends UserCaseSupport<Void, List<Exercise>>{

    public GetExerciseList(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected  List<Exercise> executeImpl(Void request) {
        Set<String> idSet = using(PersistManager.class).listExerciseIds();
        List<Exercise> answer = new ArrayList<>(idSet.size());
        for (String exId : idSet) {
            Exercise exercise = using(Model.class).execute(GetExerciseById.class, exId);
            answer.add(exercise);
        }
        return answer;
    }
}
