package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;

import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.RoutineExercise;
import team.monroe.org.pocketfit.view.presenter.ClockViewPresenter;

public class TrainingTilePowerExecuteFragment extends TrainingTileFragment {

    private RoutineExercise mRoutineExercise;
    private ClockViewPresenter mSetClockPresenter;

    @Override
    protected int getTileLayoutId() {
        return R.layout.tile_training_power_execute;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        mRoutineExercise= application().getTrainingPlan().getCurrentExercise();
        mSetClockPresenter = new ClockViewPresenter(view_text(R.id.exercise_timer));
        view_text(R.id.exercise_name).setText(mRoutineExercise.exercise.title);
        RoutineExercise.PowerExerciseDetails details = (RoutineExercise.PowerExerciseDetails) mRoutineExercise.exerciseDetails;
        view_text(R.id.exercise_description).setText(details.times + " x " + details.weight + " times/kg");
        view_text(R.id.exercise_set).setText("Set " + (application().getTrainingPlan().getSetIndex() + 1));
        updateActionText();
        view(R.id.action_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSetStarted()){
                    application().getTrainingPlan().stopSet();
                    owner().updateTile();
                }else {
                    application().getTrainingPlan().startSet();
                    startTimer();
                    updateActionText();
                }
            }
        });
    }

    private void updateActionText() {
        if (isSetStarted()){
            startTimer();
            view_text(R.id.action_main).setText("Done");
        }else {
            mSetClockPresenter.resetClock();
            view_text(R.id.action_main).setText("Start");
        }
    }

    private void startTimer() {
        mSetClockPresenter.startClock(application().getTrainingPlan().getSetStartDate());
    }

    private boolean isSetStarted() {
        return application().getTrainingPlan().isSetStarted();
    }

}
