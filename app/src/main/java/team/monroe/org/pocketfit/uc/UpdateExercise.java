package team.monroe.org.pocketfit.uc;

import org.monroe.team.android.box.services.SettingManager;
import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import team.monroe.org.pocketfit.Settings;
import team.monroe.org.pocketfit.manage.Persist;
import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.Exercise;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;

public class UpdateExercise extends UserCaseSupport<Exercise, Void>{

    public UpdateExercise(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Void executeImpl(Exercise updatePersist) {

        Exercise originPersist = using(PersistManager.class).getExercise(updatePersist.id);

        if (originPersist == null) {
            if (isDefined(updatePersist)){
                using(PersistManager.class).updateOrCreateExercise(updatePersist);
            }
            return null;
        }

        originPersist.title = diff(originPersist.title, updatePersist.title);
        originPersist.description = diff(originPersist.description, updatePersist.description);
        boolean isUnderUse = using(Model.class).execute(IsExerciseSafeToChange.class, originPersist.id);
        if (isUnderUse && originPersist.type != updatePersist.type){
            throw new IllegalStateException("Inconsistent data change: type");
        }

        originPersist.type = diff(originPersist.type, updatePersist.type);
        if (isDefined(originPersist)) {
            using(PersistManager.class).updateOrCreateExercise(originPersist);
        }

        return null;
    }


    private boolean isDefined(Exercise exercise) {
        return isDefined(exercise.title);
    }

    private boolean isDefined(String string) {
        return string != null && !string.trim().isEmpty();
    }

    private <Type> Type diff(Type origin, Type update) {
        return update != null? update:origin;
    }

}
