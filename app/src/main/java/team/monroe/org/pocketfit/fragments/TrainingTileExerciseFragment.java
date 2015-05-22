package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.TrainingExecutionService;
import team.monroe.org.pocketfit.presentations.RoutineExercise;

public class TrainingTileExerciseFragment extends TrainingTileFragment {

    private RoutineExercise mRoutineExercise;

    @Override
    protected int getTileLayoutId() {
        return R.layout.tile_training_exercise;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view(R.id.action_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                application().getTrainingPlan().addSet();
                owner().updateTile();
            }
        });
        mRoutineExercise = application().getTrainingPlan().getCurrentExercise();
        view_text(R.id.exercise_name).setText(mRoutineExercise.exercise.title);
        view_text(R.id.exercise_description).setText(mRoutineExercise.exercise.description);

        switch (mRoutineExercise.exercise.type ){
            case weight_times:
                RoutineExercise.PowerExerciseDetails exerciseDetails = (RoutineExercise.PowerExerciseDetails) mRoutineExercise.exerciseDetails;
                addDetails("Weight",exerciseDetails.weight, "kg");
                addDetails("Times",exerciseDetails.times, "times");
                addDetails("Sets",exerciseDetails.sets, "sets");
                break;
            default:
                throw new IllegalStateException();
        }
    }

    private void addDetails(String caption, Object value, String measure) {
        ViewGroup viewPanel =view(R.id.panel_exercise_details, ViewGroup.class);
        View view = activity().getLayoutInflater().inflate(R.layout.panel_exercise_detail,viewPanel,false);
        ((TextView)view.findViewById(R.id.item_caption)).setText(caption);
        ((TextView)view.findViewById(R.id.item_value)).setText(value.toString());
        ((TextView)view.findViewById(R.id.item_measure)).setText(measure);
        viewPanel.addView(view);
    }

}
