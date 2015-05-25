package team.monroe.org.pocketfit.fragments;

import android.widget.TextView;

import team.monroe.org.pocketfit.presentations.RoutineExercise;
import team.monroe.org.pocketfit.view.presenter.ClockViewPresenter;
import team.monroe.org.pocketfit.view.presenter.DowntimeClockViewPresenter;

public class TrainingTileTimeExecuteFragment extends TrainingTileExecuteFragment {

    private DowntimeClockViewPresenter mSetClockPresenter;

    @Override
    protected void initializeSetProgressView(TextView textView) {
        RoutineExercise.TimeExerciseDetails details = (RoutineExercise.TimeExerciseDetails) mRoutineExercise.exerciseDetails;
        long requestedMs = (long) (details.time * 60 * 1000);
        mSetClockPresenter = new DowntimeClockViewPresenter(textView);
        mSetClockPresenter.show();
        mSetClockPresenter.startClock(application().getTrainingPlan().getSetStartDate(), requestedMs);
    }

    @Override
    protected String createExerciseDescription(RoutineExercise exercise) {
        RoutineExercise.TimeExerciseDetails details = (RoutineExercise.TimeExerciseDetails) exercise.exerciseDetails;
        return details.time + " min";
    }

    @Override
    protected String getSetDescription() {
        return "";
    }

}
