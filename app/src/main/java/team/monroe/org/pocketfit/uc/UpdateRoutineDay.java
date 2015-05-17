package team.monroe.org.pocketfit.uc;

import org.monroe.team.android.box.services.SettingManager;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import team.monroe.org.pocketfit.Settings;
import team.monroe.org.pocketfit.manage.RoutineManager;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;

public class UpdateRoutineDay extends UserCaseSupport<RoutineDay, Void>{

    public UpdateRoutineDay(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Void executeImpl(RoutineDay update) {

        RoutineDay origin = using(RoutineManager.class).getDay(update.id);

        if (origin == null){
            using(RoutineManager.class).updateOrCreateDay(update);
            checkForRemove(update);
            return null;
        }

        origin.restDays = diff(origin.restDays, update.restDays);
        origin.description = diff(origin.description, update.description);
        using(RoutineManager.class).updateOrCreateDay(origin);
        checkForRemove(origin);
        return null;
    }

    private void checkForRemove(RoutineDay origin) {
        if (!isDefined(origin)){
            using(RoutineManager.class).remove(origin.id);
            if (origin.id.equals(using(SettingManager.class).get(Settings.ACTIVE_ROUTINE_ID))){
                using(SettingManager.class).set(Settings.ACTIVE_ROUTINE_ID, null);
            }
        }
    }

    private boolean isDefined(RoutineDay routine) {
        return routine.restDays != null || isDefined(routine.description);
    }

    private boolean isDefined(String string) {
        return string != null && !string.trim().isEmpty();
    }

    private <Type> Type diff(Type origin, Type update) {
        return update == null? update:origin;
    }
}
