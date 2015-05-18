package team.monroe.org.pocketfit.uc;

import org.monroe.team.android.box.services.SettingManager;
import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.ArrayList;

import team.monroe.org.pocketfit.Settings;
import team.monroe.org.pocketfit.manage.Persist;
import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;

public class GetRoutineById extends UserCaseSupport<String, Routine>{

    public GetRoutineById(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Routine executeImpl(String id) {
        Persist.Routine routinePersist = using(PersistManager.class).get(id);
        if (routinePersist == null) return null;

        Routine answer = new Routine(routinePersist.id);
        answer.title = routinePersist.title;
        answer.imageId = routinePersist.imageId;
        answer.description = routinePersist.description;
        answer.active = routinePersist.id.equals(using(SettingManager.class).get(Settings.ACTIVE_ROUTINE_ID));

        answer.trainingDays = new ArrayList<>();
        for (String dayId : routinePersist.routineDaysIdList) {
            RoutineDay routineDay = using(Model.class).execute(GetRoutineDayById.class,dayId);
            if (routineDay == null) throw new NullPointerException("Day couldn`t be null");
            answer.trainingDays.add(routineDay);
        }
        return answer;
    }
}
