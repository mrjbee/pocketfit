package team.monroe.org.pocketfit.uc;

import org.monroe.team.android.box.services.SettingManager;
import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;
import org.monroe.team.corebox.utils.DateUtils;

import java.util.Date;

import team.monroe.org.pocketfit.Settings;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;
import team.monroe.org.pocketfit.presentations.RoutineSchedule;

public class GetActiveRoutineSchedule extends UserCaseSupport<Void, RoutineSchedule> {

    public GetActiveRoutineSchedule(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected RoutineSchedule executeImpl(Void request) {

        String activeRoutineId = using(SettingManager.class).get(Settings.ID_ACtIVE_ROUTINE);
        if (activeRoutineId == null) return null;

        Routine routine = using(Model.class).execute(GetRoutineById.class, activeRoutineId);
        if (routine == null) throw new IllegalStateException("Routine Schedule: Routine not available. Wrong id");

        int lastTrainingDayIndex = -1;
        long lastTrainingDate = -1;
        String lastActiveRoutineDayId = using(SettingManager.class).get(Settings.ID_ACTIVE_ROUTINE_LAST_DAY);
        if (lastActiveRoutineDayId != null){
            for (int i = 0; i < routine.trainingDays.size(); i++) {
                if (lastActiveRoutineDayId.equals(routine.trainingDays.get(i).id)){
                    lastTrainingDayIndex = i;
                    lastTrainingDate = using(SettingManager.class).get(Settings.DATE_ACTIVE_ROUTINE_LAST_TRAINING);
                    break;
                }
            }
        }

        if (lastTrainingDate == -1){
            lastTrainingDate = DateUtils.today().getTime();
        }

        return new RoutineSchedule(routine, lastTrainingDayIndex,  new Date(lastTrainingDate));
    }
}
