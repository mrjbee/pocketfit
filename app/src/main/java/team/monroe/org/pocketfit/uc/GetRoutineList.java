package team.monroe.org.pocketfit.uc;

import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.Routine;

public class GetRoutineList extends UserCaseSupport<Void, List<Routine>>{

    public GetRoutineList(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected  List<Routine> executeImpl(Void request) {
        Set<String> idSet = using(PersistManager.class).listRoutineIds();
        List<Routine> answer = new ArrayList<>(idSet.size());
        for (String routineId : idSet) {
            Routine routine = using(Model.class).execute(GetRoutineById.class, routineId);
            answer.add(routine);
        }
        return answer;
    }
}
