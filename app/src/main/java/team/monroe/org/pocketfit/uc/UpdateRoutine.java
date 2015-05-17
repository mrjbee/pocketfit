package team.monroe.org.pocketfit.uc;

import org.monroe.team.android.box.services.SettingManager;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import team.monroe.org.pocketfit.Settings;
import team.monroe.org.pocketfit.manage.RoutineManager;
import team.monroe.org.pocketfit.presentations.Routine;

public class UpdateRoutine extends UserCaseSupport<Routine, Void>{

    public UpdateRoutine(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Void executeImpl(Routine update) {

        Routine origin = using(RoutineManager.class).get(update.id);
        if (update.active != null) {
            if (update.active) {
                using(SettingManager.class).set(Settings.ACTIVE_ROUTINE_ID, update.id);
            } else if (update.id.equals(using(SettingManager.class).get(Settings.ACTIVE_ROUTINE_ID))) {
                using(SettingManager.class).set(Settings.ACTIVE_ROUTINE_ID, null);
            }
        }

        if (origin == null){
            using(RoutineManager.class).updateOrCreate(update);
            checkForRemove(update);
            return null;
        }

        origin.title = diff(origin.title, update.title);
        origin.description = diff(origin.description, update.description);
        using(RoutineManager.class).updateOrCreate(origin);
        checkForRemove(origin);
        return null;
    }

    private void checkForRemove(Routine origin) {
        if (!isDefined(origin)){
            using(RoutineManager.class).remove(origin.id);
            if (origin.id.equals(using(SettingManager.class).get(Settings.ACTIVE_ROUTINE_ID))){
                using(SettingManager.class).set(Settings.ACTIVE_ROUTINE_ID, null);
            }
        }
    }

    private boolean isDefined(Routine routine) {
        return isDefined(routine.title) || isDefined(routine.description);
    }

    private boolean isDefined(String string) {
        return string != null && !string.trim().isEmpty();
    }

    private String diff(String origin, String update) {
        return update == null? update:origin;
    }
}
