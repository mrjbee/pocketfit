package team.monroe.org.pocketfit;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import org.monroe.team.android.box.data.Data;
import org.monroe.team.corebox.utils.Closure;
import org.monroe.team.corebox.utils.DateUtils;
import org.monroe.team.corebox.utils.Lists;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
        void stopExecution();
    }


    public class TrainingExecutionMangerBinder extends Binder implements TrainingExecutionManager{

        private TrainingPlan trainingPlan;

        @Override
        public void startExecution(Routine routine, RoutineDay routineDay) {
            if (trainingPlan != null) throw new IllegalStateException("Routine is running");
            PocketFitApp pocketFitApp = (PocketFitApp) getApplication();
            trainingPlan = new TrainingPlan(routine, routineDay, pocketFitApp.model());

            Notification.Builder builder = new Notification.Builder(service());
            builder.setSmallIcon(R.drawable.runner);
            builder.setContentTitle(routine.title);
            builder.setContentText(routineDay.description);
            builder.setOngoing(true);
            builder.setTicker("Training ...");
            builder.setContentIntent(PendingIntent.getActivity(service(),334,new Intent(service(),TrainingActivity.class),PendingIntent.FLAG_CANCEL_CURRENT));

            service().startInForeground(builder.getNotification());

        }

        public TrainingPlan getTrainingPlan() {
            return trainingPlan;
        }

        @Override
        public void stopExecution() {
            service().stopForeground(true);
            service().stopSelf();
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
        private final Data<Agenda> data_Agenda;
        private Date startDate;
        private Date pauseStartDate;
        private ExerciseExecution currentExecution;
        private TrainingPlanListener trainingPlanListener = new NoOpTrainingPlanListener();
        private List<ResultRecord> resultRecordList = new ArrayList<>();

        public TrainingPlan(final Routine routine, final RoutineDay routineDay, PocketFitModel model) {
            this.routine = routine;
            this.routineDay = routineDay;
            if (!routineDay.exerciseList.isEmpty()) {
                currentExecution = new ExerciseExecution(routineDay.exerciseList.get(0));
            }

            data_Agenda = new Data<Agenda>(Agenda.class, model) {
                @Override
                protected Agenda provideData() {
                    Agenda agenda = new Agenda();
                    for (RoutineExercise routineExercise : routineDay.exerciseList) {
                        agenda.exerciseList.add(new AgendaExercise(
                                routineExercise.exercise,
                                isExecuted(routineExercise),
                                isRunning(routineExercise)));
                    }
                    return agenda;
                }

                private boolean isExecuted(RoutineExercise routineExercise) {
                    for (ResultRecord resultRecord : resultRecordList) {
                        if (routineExercise.equals(resultRecord.exercise)){
                            return true;
                        }
                    }
                    return false;
                }

                private boolean isRunning(RoutineExercise routineExercise) {
                    if (currentExecution ==null)return false;
                    return routineExercise.equals(currentExecution.routineExercise);
                }
            };

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
            pauseStartDate = null;
            trainingPlanListener.onStartPauseDateChanged(pauseStartDate);
            data_Agenda.invalidate();
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


        public long getSetDuration() {
            Date date = Lists.getLast(currentExecution.setList).startDate;
            Date endDate = Lists.getLast(currentExecution.setList).endDate;
            if (date == null || endDate == null){
                return 0;
            }
            return endDate.getTime() - date.getTime();
        }


        public void stopSet() {
            Date date = DateUtils.now();
            Lists.getLast(currentExecution.setList).endDate = date;
            if (startDate != null){
                pauseStartDate = date;
                trainingPlanListener.onStartPauseDateChanged(pauseStartDate);
            }
            data_Agenda.invalidate();
        }

        public boolean isPaused() {
            return pauseStartDate != null;
        }

        public Date getPauseStartDate() {
            return pauseStartDate;
        }

        public void commitPowerSet(float weight, int times) {
            Lists.getLast(currentExecution.setList).results.put("weight",weight);
            Lists.getLast(currentExecution.setList).results.put("times",times);
            Lists.getLast(currentExecution.setList).results.put("description",times+" x "+weight);
        }

        public void commitTimeSet(Float duration) {
            Lists.getLast(currentExecution.setList).results.put("duration", duration);
        }


        public void commitDistanceSet(Float distance) {
            Lists.getLast(currentExecution.setList).results.put("distance", distance);
        }

        public boolean hasMoreSetsScheduled() {
            int requiredSets = 1;
            Exercise.Type type = currentExecution.routineExercise.exercise.type;
            switch (type){
                case weight_times:
                    requiredSets = ((RoutineExercise.PowerExerciseDetails) currentExecution.routineExercise.exerciseDetails).sets;
                    break;
            }

            return (requiredSets - currentExecution.setList.size()) >0;
        }

        public List<String> getSetsDescriptionList() {
            return Lists.collect(currentExecution.setList,new Closure<Set, String>() {
                @Override
                public String execute(Set arg) {
                    return (String) arg.results.get("description");
                }
            });
        }

        public void nextExercise() {
            int index = routineDay.exerciseList.indexOf(currentExecution.routineExercise);
            for (int i = 0; i < currentExecution.setList.size(); i++) {
                Set set = currentExecution.setList.get(i);
                ResultRecord resultRecord = new ResultRecord(
                        set.startDate,
                        set.endDate,
                        currentExecution.routineExercise,
                        i,
                        set.results);
                resultRecordList.add(resultRecord);
            }

            index++;
            if (index < routineDay.exerciseList.size()){
                currentExecution = new ExerciseExecution(routineDay.exerciseList.get(index));
            }else {
                currentExecution = null;
            }
            data_Agenda.invalidate();
        }

        public boolean isAllSetsCommitted() {
            return (currentExecution.setList.size() - currentExecution.commitedSetsCount()) <= 0;
        }

        public boolean hasMoreExercises() {
            return currentExecution != null;
        }

        public long getDurationMs() {
            if (startDate == null){
                return 0;
            }
            if (resultRecordList.isEmpty()){
                return 0;
            }
            Date endDate = Lists.getLast(resultRecordList).endDate;
            return endDate.getTime() - startDate.getTime();
        }

        public float getLiftedUpWeight() {
            float answer = 0;
            for (ResultRecord resultRecord : resultRecordList) {
                if (resultRecord.exercise.exercise.type == Exercise.Type.weight_times){
                    answer += ((Integer)resultRecord.results.get("times")) *
                            ((Float)resultRecord.results.get("weight"));
                }
            }
            return answer;
        }

        public float getDistance() {
            float answer = 0;
            for (ResultRecord resultRecord : resultRecordList) {
                if (resultRecord.exercise.exercise.type == Exercise.Type.distance){
                    answer += ((Float)resultRecord.results.get("distance"));
                }
            }
            return answer;
        }

        public int getExerciseCount() {
            int answer = 0;
            java.util.Set<Exercise> exerciseSet = new HashSet<>();
            for (ResultRecord resultRecord : resultRecordList) {
                if (!exerciseSet.contains(resultRecord.exercise.exercise)){
                    answer++;
                }
                exerciseSet.add(resultRecord.exercise.exercise);
            }
            return answer;
        }

        public List<ResultRecord> getResultRecords() {
            return resultRecordList;
        }

        public Routine getRoutine() {
            return routine;
        }

        public RoutineDay getRoutineDay() {
            return routineDay;
        }

        public Data<Agenda> getAgenda() {
            return data_Agenda;
        }

        public class Agenda{
            public final List<AgendaExercise> exerciseList = new ArrayList<>();
        }

        public class AgendaExercise {

            public final Exercise exercise;
            public final boolean executed;
            public final boolean running;

            public AgendaExercise(Exercise exercise, boolean executed, boolean running) {
                this.exercise = exercise;
                this.executed = executed;
                this.running = running;
            }
        }

        public interface TrainingPlanListener{
            void onStartDateChanged(Date startDate);
            void onStartPauseDateChanged(Date pauseStartDate);
        }

        public static class NoOpTrainingPlanListener implements TrainingPlanListener{
            @Override
            public void onStartDateChanged(Date startDate) {}
            @Override
            public void onStartPauseDateChanged(Date pauseStartDate) {}
        }

        public class ExerciseExecution {

            public final RoutineExercise routineExercise;
            private final List<Set> setList = new ArrayList<>();

            public ExerciseExecution(RoutineExercise routineExercise) {
                this.routineExercise = routineExercise;
            }

            public int commitedSetsCount() {
                int size = 0;
                for (Set set : setList) {
                    size += set.results.isEmpty()?0:1;
                }
                return size;
            }
        }

        public class Set {
            private Map<String, Object> results = new HashMap<>();
            private Date startDate;
            private Date endDate;
        }

        public static class ResultRecord {

            public final Date startDate;
            public final Date endDate;
            public final RoutineExercise exercise;
            public final int setNumber;
            public final Map<String, Object> results;

            public ResultRecord(Date startDate, Date endDate, RoutineExercise exercise, int setNumber, Map<String, Object> results) {
                this.startDate = startDate;
                this.endDate = endDate;
                this.exercise = exercise;
                this.setNumber = setNumber;
                this.results = results;
            }
        }

    }

}
