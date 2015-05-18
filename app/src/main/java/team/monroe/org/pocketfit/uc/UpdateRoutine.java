package team.monroe.org.pocketfit.uc;

import org.monroe.team.android.box.services.SettingManager;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import team.monroe.org.pocketfit.Settings;
import team.monroe.org.pocketfit.manage.Persist;
import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;

public class UpdateRoutine extends UserCaseSupport<Routine, Void>{

    public UpdateRoutine(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Void executeImpl(Routine update) {

        Persist.Routine updatePersist = new Persist.Routine(update.id);
        updatePersist.imageId = update.imageId;
        updatePersist.title = update.title;
        updatePersist.description = update.description;
        if (update.trainingDays != null){
            for (RoutineDay trainingDay : update.trainingDays) {
                updatePersist.routineDaysIdList.add(trainingDay.id);
            }

        }
        Persist.Routine originPersist = using(PersistManager.class).get(updatePersist.id);

        if (update.active != null) {
            if (update.active) {
                using(SettingManager.class).set(Settings.ACTIVE_ROUTINE_ID, updatePersist.id);
            } else if (updatePersist.id.equals(using(SettingManager.class).get(Settings.ACTIVE_ROUTINE_ID))) {
                using(SettingManager.class).set(Settings.ACTIVE_ROUTINE_ID, null);
            }
        }

        if (originPersist == null){
            using(PersistManager.class).updateOrCreate(updatePersist);
            checkForRemove(updatePersist);
            return null;
        }

        originPersist.title = diff(originPersist.title, updatePersist.title);
        originPersist.description = diff(originPersist.description, updatePersist.description);
        originPersist.imageId = diff(originPersist.imageId, updatePersist.imageId);
        originPersist.routineDaysIdList = diff(originPersist.routineDaysIdList, updatePersist.routineDaysIdList);

        using(PersistManager.class).updateOrCreate(originPersist);
        checkForRemove(originPersist);
        return null;
    }

    private void checkForRemove(Persist.Routine origin) {
        if (!isDefined(origin)){
            using(PersistManager.class).remove(origin.id);
            if (origin.id.equals(using(SettingManager.class).get(Settings.ACTIVE_ROUTINE_ID))){
                using(SettingManager.class).set(Settings.ACTIVE_ROUTINE_ID, null);
            }
        }
    }

    private boolean isDefined(Persist.Routine routine) {
        return isDefined(routine.title) ||
               isDefined(routine.description) ||
               !routine.routineDaysIdList.isEmpty();
    }

    private boolean isDefined(String string) {
        return string != null && !string.trim().isEmpty();
    }

    private <Type> Type diff(Type origin, Type update) {
        return update != null? update:origin;
    }

}
