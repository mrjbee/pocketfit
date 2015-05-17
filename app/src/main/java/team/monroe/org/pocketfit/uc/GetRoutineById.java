package team.monroe.org.pocketfit.uc;

import org.monroe.team.android.box.services.SettingManager;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.UUID;

import team.monroe.org.pocketfit.Settings;
import team.monroe.org.pocketfit.manage.RoutineManager;
import team.monroe.org.pocketfit.presentations.Routine;

public class GetRoutineById extends UserCaseSupport<String, Routine>{

    public GetRoutineById(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Routine executeImpl(String id) {
        Routine routine = using(RoutineManager.class).get(id);
        if (routine == null) return null;

        routine.active = routine.id.equals(using(SettingManager.class).get(Settings.ACTIVE_ROUTINE_ID));
        return routine;
    }
}
