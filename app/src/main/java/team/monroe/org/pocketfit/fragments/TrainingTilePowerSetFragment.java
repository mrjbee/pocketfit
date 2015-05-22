package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.TrainingExecutionService;
import team.monroe.org.pocketfit.presentations.RoutineExercise;

public class TrainingTilePowerSetFragment extends TrainingTileFragment {

    private RoutineExercise mRoutineExercise;
    private TrainingExecutionService.TrainingExecutionMangerBinder.ExerciseExecution mExerciseExecution;

    @Override
    protected int getTileLayoutId() {
        return R.layout.tile_training_power_set;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mExerciseExecution = application().getExerciseExecution();
        mRoutineExercise = application().getExerciseExecution().routineExercise;

        view_text(R.id.exercise_name).setText(mRoutineExercise.exercise.title);
        RoutineExercise.PowerExerciseDetails details = (RoutineExercise.PowerExerciseDetails) mRoutineExercise.exerciseDetails;
        view_text(R.id.exercise_description).setText(details.weight+" x "+details.times+" kg/times");
        view_text(R.id.exercise_set).setText("Set " + (mExerciseExecution.getActiveSetIndex() + 1));
        view(R.id.action_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExerciseExecution.stopSet();
                owner().updateTile();
            }
        });
    }

}
