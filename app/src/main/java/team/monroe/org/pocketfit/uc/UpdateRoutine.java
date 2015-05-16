package team.monroe.org.pocketfit.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import team.monroe.org.pocketfit.manage.RoutineManager;
import team.monroe.org.pocketfit.presentations.Routine;

public class UpdateRoutine extends UserCaseSupport<Routine, Routine>{

    public UpdateRoutine(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Routine executeImpl(Routine update) {

        Routine origin = using(RoutineManager.class).get(update.id);

        if (origin == null){
            using(RoutineManager.class).updateOrCreate(update);
            checkForRemove(update);
            return update;
        }

        origin.title = diff(origin.title, update.title);
        origin.description = diff(origin.description, update.description);
        using(RoutineManager.class).updateOrCreate(origin);

        checkForRemove(origin);

        return origin;
    }

    private void checkForRemove(Routine origin) {
        if (!isDefined(origin)){
            using(RoutineManager.class).remove(origin.id);
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
