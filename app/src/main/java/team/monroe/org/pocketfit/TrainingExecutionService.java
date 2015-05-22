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

import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;
import team.monroe.org.pocketfit.presentations.RoutineExercise;

public class TrainingExecutionService extends Service {

    public TrainingExecutionService() {}

    @Override
    public IBinder onBind(Intent intent) {
        return new TrainingExecutionMangerBinder();
    }

    public interface TrainingExecutionManager{
        public void startExecution(Routine routine, RoutineDay dayIndex);
        String getRoutineId();
        String getRoutineDayId();
        Routine getRoutine();
        TrainingExecutionMangerBinder.ExerciseExecution getCurrentExecution();
    }


    public class TrainingExecutionMangerBinder extends Binder implements TrainingExecutionManager{

        Routine mRoutine;
        RoutineDay mRoutineDay;
        private ExerciseExecution currentExecution;

        @Override
        public void startExecution(Routine routine, RoutineDay routineDay) {
            if (mRoutine != null) throw new IllegalStateException("Routine is running");
            mRoutine = routine;
            mRoutineDay = routineDay;

            if (!mRoutineDay.exerciseList.isEmpty()){
                currentExecution = new ExerciseExecution(mRoutineDay.exerciseList.get(0));
            }

            Notification.Builder builder = new Notification.Builder(service());
            builder.setSmallIcon(R.drawable.runner);
            builder.setContentTitle(mRoutine.title);
            builder.setContentText(mRoutineDay.description);
            builder.setOngoing(true);
            builder.setTicker("Training ...");
            builder.setContentIntent(PendingIntent.getActivity(service(),334,new Intent(service(),TrainingActivity.class),PendingIntent.FLAG_UPDATE_CURRENT));
            service().startInForeground(builder.getNotification());
        }

        public ExerciseExecution getCurrentExecution() {
            return currentExecution;
        }


        @Override
        public String getRoutineId() {
            return mRoutine.id;
        }

        @Override
        public String getRoutineDayId() {
            return mRoutineDay.id;
        }

        @Override
        public Routine getRoutine() {
            return mRoutine;
        }

        private TrainingExecutionService service() {
            return TrainingExecutionService.this;
        }


        public class ExerciseExecution {

            public final RoutineExercise routineExercise;
            private final List<Set> setList = new ArrayList<>();

            public ExerciseExecution(RoutineExercise routineExercise) {
                this.routineExercise = routineExercise;
            }

            public void addSet() {
                setList.add(new Set());
            }

            public boolean isStarted() {
                return !setList.isEmpty();
            }

            public boolean isSetFinished() {
                return isStarted() && Lists.getLast(setList).isDone();
            }


            public void stopSet() {
               Set set = Lists.getLast(setList);
               set.endDate = DateUtils.now();
            }

            public void startSet() {
                Set set = new Set();
                set.startDate = DateUtils.now();
                setList.add(set);
            }


            public int getActiveSetIndex() {
                return setList.size() - 1;
            }


            public class Set {

                private Map<String, Object> results = new HashMap<>();
                private Date startDate;
                private Date endDate;

                public Date getStartDate() {
                    return startDate;
                }

                public Date start(){
                    startDate = DateUtils.now();
                    return getStartDate();
                }

                public boolean isDone() {
                    return startDate != null && endDate != null;
                }
            }
        }
    }

    private void startInForeground(Notification notification) {
        startForeground(101, notification);
    }


}
