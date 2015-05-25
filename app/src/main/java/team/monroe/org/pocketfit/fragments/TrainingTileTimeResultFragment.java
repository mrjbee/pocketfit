package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.RoutineExercise;
import team.monroe.org.pocketfit.view.presenter.TimePickPresenter;

public class TrainingTileTimeResultFragment extends TrainingTileFragment {

    private RoutineExercise mRoutineExercise;
    private TimePickPresenter mTimePickPresenter;

    @Override
    protected int getTileLayoutId() {
        return R.layout.tile_training_time_result;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        mTimePickPresenter = new TimePickPresenter(view(R.id.panel_time_edit));

        mRoutineExercise= application().getTrainingPlan().getCurrentExercise();
        view_text(R.id.exercise_name).setText(mRoutineExercise.exercise.title);
        long durationMs = application().getTrainingPlan().getSetDuration();
        double duration =  (double)durationMs / (60d*1000d);

        mTimePickPresenter.setMinutes((float) duration);

        view(R.id.action_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Float duration = mTimePickPresenter.getMinutes();
                application().getTrainingPlan().commitTimeSet(duration);
                application().getTrainingPlan().nextExercise();
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
