package team.monroe.org.pocketfit.uc;

import org.monroe.team.android.box.services.SettingManager;
import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;
import org.monroe.team.corebox.utils.DateUtils;

import java.util.Date;

import team.monroe.org.pocketfit.Settings;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineSchedule;

public class GetActiveRoutineSchedule extends UserCaseSupport<Void, RoutineSchedule> {

    public GetActiveRoutineSchedule(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected RoutineSchedule executeImpl(Void request) {

        String activeRoutineId = using(SettingManager.class).get(Settings.ROUTINE_ACTIVE_ID);
        if (activeRoutineId == null) return null;

        String startedRoutineId = using(SettingManager.class).get(Settings.ROUTINE_STARTED_ID);
        Long routineStartDate = using(SettingManager.class).get(Settings.ROUTINE_START_DATE);

        if (routineStartDate == null || activeRoutineId.equals(startedRoutineId)){
            routineStartDate = DateUtils.today().getTime();
        }

        Routine routine = using(Model.class).execute(GetRoutineById.class, activeRoutineId);
        if (routine == null) throw new IllegalStateException();

        return new RoutineSchedule(routine, new Date(routineStartDate));
    }
}
