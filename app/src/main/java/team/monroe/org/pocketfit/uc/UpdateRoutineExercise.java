package team.monroe.org.pocketfit.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import team.monroe.org.pocketfit.manage.Persist;
import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.RoutineExercise;

public class UpdateRoutineExercise extends UserCaseSupport<UpdateRoutineExercise.RoutineExerciseUpdate, Void>{

    public UpdateRoutineExercise(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Void executeImpl(RoutineExerciseUpdate request) {

        Persist.RoutineDay routineDayPersists = using(PersistManager.class).getDay(request.dayId);
        Persist.RoutineExercise updateExercisePersists = new Persist.RoutineExercise(request.update.id, request.dayId, request.update.exercise.id);
        updateExercisePersists.details = request.update.exerciseDetails;


        boolean not_filled = updateExercisePersists.details == null || !updateExercisePersists.details.isDefined();

        if (not_filled){
            return null;
        }

        if (request.index == RoutineExerciseUpdate.INDEX_DELETE){
            routineDayPersists.routineExerciseIdList.remove(updateExercisePersists.id);
            using(PersistManager.class).updateOrCreateDay(routineDayPersists);
            using(PersistManager.class).removeRoutineExercise(updateExercisePersists.id);
            return null;
        }


        int indexExists = routineDayPersists.routineExerciseIdList.indexOf(request.update.id);
        int indexInsert = request.index;
        if (indexInsert > routineDayPersists.routineExerciseIdList.size()){
            indexInsert = routineDayPersists.routineExerciseIdList.size();
        }

        routineDayPersists.routineExerciseIdList.add(indexInsert, updateExercisePersists.id);

        if (indexExists != -1){

            if (indexExists > indexInsert){
                indexExists ++;
            }
            routineDayPersists.routineExerciseIdList.remove(indexExists);
        }

        using(PersistManager.class).updateOrCreateRoutineExercise(updateExercisePersists);
        using(PersistManager.class).updateOrCreateDay(routineDayPersists);
        return null;
    }


    private boolean isDefined(Persist.RoutineDay routineDay) {
        return isDefined(routineDay.description);
    }

    private boolean isDefined(String string) {
        return string != null && !string.trim().isEmpty();
    }

    private <Type> Type diff(Type origin, Type update) {
        return update != null? update:origin;
    }


    public static class RoutineExerciseUpdate {

        public final static int INDEX_DELETE = Integer.MIN_VALUE;
        public final static int INDEX_ADD_LAST = Integer.MAX_VALUE;

        public final RoutineExercise update;
        public final String dayId;
        public final int index;


        public RoutineExerciseUpdate(RoutineExercise exercise, String dayId, int index) {
            this.update = exercise;
            this.dayId = dayId;
            this.index = index;
        }
    }
}
