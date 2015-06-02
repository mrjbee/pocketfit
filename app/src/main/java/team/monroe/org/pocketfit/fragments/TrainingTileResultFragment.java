package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.RoutineExercise;
import team.monroe.org.pocketfit.view.presenter.ExerciseResultEditPresenter;

public abstract class TrainingTileResultFragment <DetailsType extends RoutineExercise.ExerciseDetails> extends TrainingTileFragment {

    private RoutineExercise mRoutineExercise;
    private DetailsType mExerciseDetails;
    private ExerciseResultEditPresenter mEditPresenter;

    @Override
    protected int getTileLayoutId() {
        return R.layout.tile_training_result;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        mRoutineExercise = application().getTrainingPlan().getCurrentExercise();
        mExerciseDetails = createExerciseDetails();

        view_text(R.id.exercise_name).setText(mRoutineExercise.exercise.title);
        int setIndex = setNumber();
        if (setIndex < 0){
            view_text(R.id.exercise_set).setVisibility(View.GONE);
        }else{
            view_text(R.id.exercise_set).setText("Set " + setNumber());
        }

        mEditPresenter = new ExerciseResultEditPresenter(view(R.id.panel_edit_result, ViewGroup.class));
        mEditPresenter.setup(mExerciseDetails);

        view(R.id.action_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailsType exerciseDetails = (DetailsType) mEditPresenter.result();
                onSaveResult(exerciseDetails);
            }
        });
    }

    protected abstract int setNumber();
    protected abstract DetailsType createExerciseDetails();
    protected abstract void onSaveResult(DetailsType exerciseDetails);

    public RoutineExercise routineExercise() {
        return mRoutineExercise;
    }
}
