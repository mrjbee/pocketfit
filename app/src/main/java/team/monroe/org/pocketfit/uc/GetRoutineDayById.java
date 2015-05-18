package team.monroe.org.pocketfit.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.ArrayList;

import team.monroe.org.pocketfit.manage.Persist;
import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.RoutineDay;

public class GetRoutineDayById extends UserCaseSupport<String, RoutineDay>{

    public GetRoutineDayById(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected RoutineDay executeImpl(String id) {
        Persist.RoutineDay routineDayPersist = using(PersistManager.class).getDay(id);
        if (routineDayPersist == null) return null;
        RoutineDay answer = new RoutineDay(routineDayPersist.id);
        answer.restDays = routineDayPersist.restDays;
        answer.description = routineDayPersist.description;
        answer.exerciseList = new ArrayList<>(0);
        return answer;
    }
}
