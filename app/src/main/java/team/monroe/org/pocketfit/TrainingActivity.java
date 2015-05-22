package team.monroe.org.pocketfit;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import team.monroe.org.pocketfit.fragments.TrainingTileExerciseFragment;
import team.monroe.org.pocketfit.fragments.TrainingTileFragment;
import team.monroe.org.pocketfit.fragments.TrainingTileLoadingRoutineExerciseFragment;
import team.monroe.org.pocketfit.fragments.TrainingTilePowerSetFragment;
import team.monroe.org.pocketfit.presentations.Exercise;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.view.presenter.ClockViewPresenter;

public class TrainingActivity extends FragmentActivity {

    private ClockViewPresenter mTrainingDurationClockPresenter;

    @Override
    protected FragmentItem customize_startupFragment() {
        return new FragmentItem(calculateCurrentFragment());
    }


    @Override
    protected int customize_rootLayout() {
        return R.layout.activity_fragment_training_execution;
    }

    @Override
    public void header(String headerText, boolean secondary) {}

    @Override
    public void animateHeader(String headerText, boolean secondary) {}

    @Override
    public View buildHeaderActionsView(ViewGroup actionPanel) {
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Routine mRoutine = application().getTrainingRoutine();
        view_text(R.id.text_routine_name).setText(mRoutine.title);
        mTrainingDurationClockPresenter = new ClockViewPresenter(view_text(R.id.text_clock));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (application().getExerciseExecution().isStarted()){
            mTrainingDurationClockPresenter.startClock(application().getExerciseExecutionManger().getStartDate());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mTrainingDurationClockPresenter.resetClock();
    }

    private Class<? extends TrainingTileFragment> calculateCurrentFragment() {
        TrainingExecutionService.TrainingExecutionMangerBinder.ExerciseExecution exerciseExecution =
                application().getExerciseExecutionManger().getCurrentExecution();

        Exercise.Type exerciseType = exerciseExecution.routineExercise.exercise.type;
        if (exerciseType != Exercise.Type.weight_times){
            finish();
        }

        boolean started = exerciseExecution.isStarted();
        if (!started){
            return TrainingTileExerciseFragment.class;
        }else{
            if (!exerciseExecution.isSetFinished()){
                switch (exerciseType){
                    case weight_times:
                        return TrainingTilePowerSetFragment.class;
                }
            }else {

            }
        }

        return TrainingTileLoadingRoutineExerciseFragment.class;
    }

    public void updateTile() {
        Class<? extends TrainingTileFragment> nextTileFragment = calculateCurrentFragment();
        replaceBodyFragment(new FragmentItem(nextTileFragment), animation_slide_from_right());
    }
}
