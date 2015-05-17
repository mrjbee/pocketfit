package team.monroe.org.pocketfit.uc;

import org.monroe.team.android.box.services.SettingManager;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import team.monroe.org.pocketfit.Settings;
import team.monroe.org.pocketfit.manage.RoutineManager;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;

public class GetRoutineDayById extends UserCaseSupport<String, RoutineDay>{

    public GetRoutineDayById(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected RoutineDay executeImpl(String id) {
        RoutineDay routineDay = using(RoutineManager.class).getDay(id);
        if (routineDay == null) return null;
        return routineDay;
    }
}
