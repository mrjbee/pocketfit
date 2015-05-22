package team.monroe.org.pocketfit;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import org.monroe.team.corebox.utils.DateUtils;
import org.monroe.team.corebox.utils.Lists;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import team.monroe.org.pocketfit.presentations.Exercise;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;
import team.monroe.org.pocketfit.presentations.RoutineExercise;

public class TrainingExecutionService extends Service {

    public TrainingExecutionService() {}

    @Override
    public IBinder onBind(Intent intent) {
        return new TrainingExecutionMangerBinder();
    }

    public interface TrainingExecutionManager {
        public void startExecution(Routine routine, RoutineDay dayIndex);

        String getRoutineId();

        String getRoutineDayId();

        Routine getRoutine();

        public TrainingPlan getTrainingPlan();
    }


    public class TrainingExecutionMangerBinder extends Binder implements TrainingExecutionManager{

        private TrainingPlan trainingPlan;

        @Override
        public void startExecution(Routine routine, RoutineDay routineDay) {
            if (trainingPlan != null) throw new IllegalStateException("Routine is running");
            trainingPlan = new TrainingPlan(routine, routineDay);
            Notification.Builder builder = new Notification.Builder(service());
            builder.setSmallIcon(R.drawable.runner);
            builder.setContentTitle(routine.title);
            builder.setContentText(routineDay.description);
            builder.setOngoing(true);
            builder.setTicker("Training ...");
            builder.setContentIntent(PendingIntent.getActivity(service(),334,new Intent(service(),TrainingActivity.class),PendingIntent.FLAG_UPDATE_CURRENT));
            service().startInForeground(builder.getNotification());
        }

        public TrainingPlan getTrainingPlan() {
            return trainingPlan;
        }

        @Deprecated
        @Override
        public String getRoutineId() {
            return trainingPlan.routine.id;
        }

        @Deprecated
        @Override
        public String getRoutineDayId() {
            return trainingPlan.routineDay.id;
        }

        @Deprecated
        @Override
        public Routine getRoutine() {
            return trainingPlan.routine;
        }


        private TrainingExecutionService service() {
            return TrainingExecutionService.this;
        }
    }

    private void startInForeground(Notification notification) {
        startForeground(101, notification);
    }


    public static class TrainingPlan {

        private final Routine routine;
        private final RoutineDay routineDay;
        private Date startDate;
        private ExerciseExecution currentExecution;
        private TrainingPlanListener trainingPlanListener = new NoOpTrainingPlanListener();

        public TrainingPlan(Routine routine, RoutineDay routineDay) {
            this.routine = routine;
            this.routineDay = routineDay;
            currentExecution = new ExerciseExecution(routineDay.exerciseList.get(0));
        }

        public TrainingPlanListener getTrainingPlanListener() {
            return trainingPlanListener;
        }

        public void setTrainingPlanListener(TrainingPlanListener trainingPlanListener) {
            if (trainingPlanListener == null)trainingPlanListener = new NoOpTrainingPlanListener();
            this.trainingPlanListener = trainingPlanListener;
        }

        public boolean isStarted() {
            return startDate != null;
        }

        public RoutineExercise getCurrentExercise(){
            return currentExecution.routineExercise;
        }

        public boolean isSetStarted(){
            if (currentExecution.setList.isEmpty()) return false;
            return Lists.getLast(currentExecution.setList).startDate != null;
        }

        public boolean isSetDone(){
            if (currentExecution.setList.isEmpty()) return false;
            return Lists.getLast(currentExecution.setList).endDate != null;
        }

        public Date getStartDate() {
            return startDate;
        }

        public boolean isExerciseStarted() {
            return !currentExecution.setList.isEmpty();
        }

        public void addSet() {
            currentExecution.setList.add(new Set());
        }

        public void startSet() {
            Date date = DateUtils.now();
            Lists.getLast(currentExecution.setList).startDate = date;
            if (startDate == null){
                startDate = date;
                trainingPlanListener.onStartDateChanged(startDate);
            }
        }

        private boolean isMultiSetExercise() {
            return currentExecution.routineExercise.exercise.type == Exercise.Type.weight_times;
        }

        public int getSetIndex() {
            return currentExecution.setList.size()-1;
        }

        public Date getSetStartDate() {
            return Lists.getLast(currentExecution.setList).startDate;
        }

        public void stopSet() {
            Lists.getLast(currentExecution.setList).endDate = DateUtils.now();
        }

        public interface TrainingPlanListener{

            void onStartDateChanged(Date startDate);
        }

        public static class NoOpTrainingPlanListener implements TrainingPlanListener{

            @Override
            public void onStartDateChanged(Date startDate) {

            }
        }

        public class ExerciseExecution {

            public final RoutineExercise routineExercise;
            private final List<Set> setList = new ArrayList<>();

            public ExerciseExecution(RoutineExercise routineExercise) {
                this.routineExercise = routineExercise;
            }
        }


        public class Set {
            private Map<String, Object> results = new HashMap<>();
            private Date startDate;
            private Date endDate;
        }

    }

}
