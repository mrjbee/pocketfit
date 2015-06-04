package team.monroe.org.pocketfit.fragments;
import android.widget.TextView;
import team.monroe.org.pocketfit.presentations.RoutineExercise;
import team.monroe.org.pocketfit.view.presenter.ClockViewPresenter;

public class TrainingTilePowerExecuteFragment extends TrainingTileExecuteFragment {

    private ClockViewPresenter mSetClockPresenter;

    @Override
    protected void initializeSetProgressView(TextView textView) {
        if (mSetClockPresenter != null){
            mSetClockPresenter.hide();
            mSetClockPresenter.resetClock();
        }

        mSetClockPresenter = new ClockViewPresenter(textView);
        mSetClockPresenter.show();
        mSetClockPresenter.startClock(application().getTrainingPlan().getSetStartDate());
    }

    @Override
    protected String createExerciseDescription(RoutineExercise exercise) {
        RoutineExercise.PowerExerciseDetails details = (RoutineExercise.PowerExerciseDetails) exercise.exerciseDetails;
        return details.times + " x " + details.weight + " times/kg";
    }
}
