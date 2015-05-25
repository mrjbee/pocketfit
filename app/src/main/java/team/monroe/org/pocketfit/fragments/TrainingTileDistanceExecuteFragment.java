package team.monroe.org.pocketfit.fragments;

import android.widget.TextView;

import team.monroe.org.pocketfit.presentations.RoutineExercise;
import team.monroe.org.pocketfit.view.presenter.ClockViewPresenter;

public class TrainingTileDistanceExecuteFragment extends TrainingTileExecuteFragment {

    private ClockViewPresenter mSetClockPresenter;

    @Override
    protected void initializeSetProgressView(TextView textView) {
        mSetClockPresenter = new ClockViewPresenter(textView);
        mSetClockPresenter.show();
        mSetClockPresenter.startClock(application().getTrainingPlan().getSetStartDate());
    }

    @Override
    protected String createExerciseDescription(RoutineExercise exercise) {
        RoutineExercise.DistanceExerciseDetails details = (RoutineExercise.DistanceExerciseDetails) exercise.exerciseDetails;
        return details.distance + " m";
    }

    @Override
    protected String getSetDescription() {
        return "";
    }
}
