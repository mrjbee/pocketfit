package team.monroe.org.pocketfit.presentations;

import org.monroe.team.corebox.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RoutineSchedule {

    public final Routine mRoutine;
    public final Date lastTariningTime;
    private int mLastTrainingIndex = -1;
    private List<RoutineDay> mRoutineByDays = new ArrayList<>();

    public RoutineSchedule(Routine routine, int lastTrainingDayIndex, Date lastTrainingDate) {

        this.mRoutine = routine;
        this.lastTariningTime = DateUtils.dateOnly(lastTrainingDate);

        if (lastTrainingDate ==null) throw new IllegalStateException("No start date");
        if (routine != null && routine.trainingDays != null) {
            for (int dayIndex =0 ;dayIndex < routine.trainingDays.size(); dayIndex++) {
                RoutineDay trainingDay = routine.trainingDays.get(dayIndex);
                mRoutineByDays.add(trainingDay);
                if (dayIndex == lastTrainingDayIndex){
                    mLastTrainingIndex = mRoutineByDays.size()-1;
                }
                for (int i=0; i < trainingDay.restDays; i++){
                    mRoutineByDays.add(null);
                }
            }
        }else {
            mRoutineByDays = Collections.emptyList();
        }
    }

    public boolean isNull() {
        return mRoutine == null;
    }

    public boolean isDefined() {
        return !mRoutineByDays.isEmpty();
    }

    public int getDaysBeforeNextTrainingDay() {
        Date today = DateUtils.today();
        int daysPastLastTrainingStart = (int) DateUtils.asDays(today.getTime() - lastTariningTime.getTime(), false);
        if (daysPastLastTrainingStart < 0 ) daysPastLastTrainingStart =0;
        int lastTrainingIndex = mLastTrainingIndex;
        int addToAnswer = 0;
        if (mLastTrainingIndex != -1 && wasLastTrainingOn(today)){
            lastTrainingIndex++;
            addToAnswer = 1;
        }
        if (mLastTrainingIndex == -1){
            lastTrainingIndex = 0;
        }
        int trainingDayInRoutine = (daysPastLastTrainingStart + lastTrainingIndex) % mRoutineByDays.size();
        int daysBeforeTraining = 0;
        for (int i = trainingDayInRoutine; i < mRoutineByDays.size(); i++){
            if (mRoutineByDays.get(i) != null) break;
            daysBeforeTraining ++;
        }
        return daysBeforeTraining + addToAnswer;
    }

    public RoutineDay getTrainingDay(Date date) {
        int daysPastTraining = (int) DateUtils.asDays(date.getTime() - lastTariningTime.getTime(), true);
        if (daysPastTraining < 0 ) daysPastTraining =0;
        int lastTrainingIndex = mLastTrainingIndex;
        if (mLastTrainingIndex == -1){
            lastTrainingIndex = 0;
        }
        return mRoutineByDays.get((daysPastTraining + lastTrainingIndex) % mRoutineByDays.size());
    }

    public boolean wasLastTrainingOn(Date date) {
        return  date.getTime() == lastTariningTime.getTime();
    }
}
