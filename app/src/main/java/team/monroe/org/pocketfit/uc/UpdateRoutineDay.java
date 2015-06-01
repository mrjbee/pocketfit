package team.monroe.org.pocketfit.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import team.monroe.org.pocketfit.manage.Persist;
import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;

public class UpdateRoutineDay extends UserCaseSupport<UpdateRoutineDay.RoutineDayUpdate, Void>{

    public UpdateRoutineDay(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Void executeImpl(RoutineDayUpdate update) {

        Persist.Routine routinePersists = using(PersistManager.class).get(update.routineId);
        Persist.RoutineDay updateDayPersists = new Persist.RoutineDay(update.day.id, update.routineId);
        updateDayPersists.description = update.day.description;
        updateDayPersists.restDays = update.day.restDays;

        Persist.RoutineDay dayPersists = using(PersistManager.class).getDay(update.day.id);
        if (dayPersists == null){
            dayPersists = updateDayPersists;
        } else {
            dayPersists.description = diff(dayPersists.description, updateDayPersists.description);
            dayPersists.restDays = diff(dayPersists.restDays, updateDayPersists.restDays);
        }

        boolean remove = !isDefined(dayPersists);

        if (remove || (update.index == RoutineDayUpdate.INDEX_DELETE)){
            routinePersists.routineDaysIdList.remove(update.day.id);
            using(PersistManager.class).updateOrCreate(routinePersists);
            using(PersistManager.class).removeDay(update.day.id);
            return null;
        }


        int indexExists = routinePersists.routineDaysIdList.indexOf(update.day.id);
        int indexInsert = update.index;
        if (indexInsert > routinePersists.routineDaysIdList.size()){
            indexInsert = routinePersists.routineDaysIdList.size();
        }

        routinePersists.routineDaysIdList.add(indexInsert, dayPersists.id);

        if (indexExists != -1){
            if (indexExists > indexInsert){
                indexExists ++;
            }
            routinePersists.routineDaysIdList.remove(indexExists);
        }

        using(PersistManager.class).updateOrCreateDay(dayPersists);
        using(PersistManager.class).updateOrCreate(routinePersists);
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


    public static class RoutineDayUpdate{

        public final static int INDEX_DELETE = Integer.MIN_VALUE;
        public final static int INDEX_ADD_LAST = Integer.MAX_VALUE;

        public final RoutineDay day;
        public final String routineId;
        public final int index;

        public RoutineDayUpdate(RoutineDay day, String routine, int index) {
            this.day = day;
            this.routineId = routine;
            this.index = index;
        }
    }
}
