package team.monroe.org.pocketfit.presentations;

import org.monroe.team.corebox.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RoutineSchedule {

    public final Routine routine;
    private List<RoutineDay> routineByDays = new ArrayList<>();
    public final Date startDate;

    public RoutineSchedule(Routine routine, Date startDate) {
        this.routine = routine;
        this.startDate = startDate;
        if (startDate ==null) throw new IllegalStateException("No start date");
        if (routine != null && routine.trainingDays != null) {
            for (RoutineDay trainingDay : routine.trainingDays) {
                routineByDays.add(trainingDay);
                for (int i=0; i < trainingDay.restDays; i++){
                    routineByDays.add(null);
                }
            }
        }else {
            routineByDays = Collections.emptyList();
        }
    }

    public boolean isNull() {
        return routine == null;
    }

    public boolean isDefined() {
        return !routineByDays.isEmpty();
    }

    public int getDaysBeforeNextTrainingDay() {
        Date today = DateUtils.today();
        int daysPastStart = (int) DateUtils.asDays(today.getTime() - startDate.getTime(), true);
        int trainingDayInRoutine = daysPastStart % routineByDays.size();
        int daysBeforeTraining = 0;
        for (int i = trainingDayInRoutine; i < routineByDays.size(); i++){
            if (routineByDays.get(i) != null) break;
            daysBeforeTraining ++;
        }
        return daysBeforeTraining;
    }

    public RoutineDay getTrainingDay(Date date) {
        int daysPastStart = (int) DateUtils.asDays(date.getTime() - startDate.getTime(), true);
        if (daysPastStart < 0) return null;
        return routineByDays.get(daysPastStart%routineByDays.size());
    }
}
