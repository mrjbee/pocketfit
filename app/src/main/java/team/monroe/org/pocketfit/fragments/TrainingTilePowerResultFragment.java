package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.RoutineExercise;
import team.monroe.org.pocketfit.view.presenter.ClockViewPresenter;

public class TrainingTilePowerResultFragment extends TrainingTileFragment {

    private RoutineExercise mRoutineExercise;
    private ClockViewPresenter mSetClockPresenter;

    @Override
    protected int getTileLayoutId() {
        return R.layout.tile_training_power_result;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        mRoutineExercise= application().getTrainingPlan().getCurrentExercise();
        mSetClockPresenter = new ClockViewPresenter(view_text(R.id.exercise_timer));
        view_text(R.id.exercise_name).setText(mRoutineExercise.exercise.title);
        RoutineExercise.PowerExerciseDetails details = (RoutineExercise.PowerExerciseDetails) mRoutineExercise.exerciseDetails;
        view_text(R.id.exercise_set).setText("Set " + (application().getTrainingPlan().getSetIndex() + 1));
        view(R.id.edit_power_weight, EditText.class).setText(details.weight+"");
        view(R.id.edit_power_times, EditText.class).setText(details.times+"");


        view(R.id.action_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Float weight = readPositiveFloat(R.id.edit_power_weight);
                Integer times = readPositiveInteger(R.id.edit_power_times);
                application().getTrainingPlan().commitPowerSet(weight, times);
                if (application().getTrainingPlan().hasMoreSetsScheduled()){
                    application().getTrainingPlan().addSet();
                }
                owner().updateTile();
            }
        });
    }

    protected Integer readPositiveInteger(int r_text) {
        Integer value;
        String text = view(r_text, EditText.class).getText().toString();
        try {
            value = Math.abs(Integer.parseInt(text));
        }catch (Exception e){
            value = null;
        }
        return value;
    }

    protected Float readPositiveFloat(int r_text) {
        Float value;
        String text = view(r_text, EditText.class).getText().toString();
        try {
            value = Math.abs(Float.parseFloat(text));
        }catch (Exception e){
            value = null;
        }
        return value;
    }

}
