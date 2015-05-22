package team.monroe.org.pocketfit;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;

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
    }


    public class TrainingExecutionMangerBinder extends Binder implements TrainingExecutionManager{

        Routine mRoutine;
        RoutineDay mRoutineDay;

        @Override
        public void startExecution(Routine routine, RoutineDay routineDay) {
            if (mRoutine != null) throw new IllegalStateException("Routine is running");
            mRoutine = routine;
            mRoutineDay = routineDay;

            Notification.Builder builder = new Notification.Builder(service());
            builder.setSmallIcon(R.drawable.runner);
            builder.setContentTitle(mRoutine.title);
            builder.setContentText(mRoutineDay.description);
            builder.setOngoing(true);
            builder.setTicker("Training ...");

            service().startInForeground(builder.getNotification());
        }

        @Override
        public String getRoutineId() {
            return mRoutine.id;
        }

        @Override
        public String getRoutineDayId() {
            return mRoutineDay.id;
        }

        private TrainingExecutionService service() {
            return TrainingExecutionService.this;
        }
    }

    private void startInForeground(Notification notification) {
        startForeground(101, notification);
    }

}
