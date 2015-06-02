package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.RoutineExercise;
import team.monroe.org.pocketfit.view.presenter.TimePickPresenter;

public class TrainingTileTimeResultFragment extends TrainingTileResultFragment<RoutineExercise.TimeExerciseDetails> {

    @Override
    protected int setNumber() {
        return -1;
    }

    @Override
    protected RoutineExercise.TimeExerciseDetails createExerciseDetails() {
        RoutineExercise.TimeExerciseDetails timeExerciseDetails = new RoutineExercise.TimeExerciseDetails();
        long durationMs = application().getTrainingPlan().getSetDuration();
        double duration =  (double)durationMs / (60d*1000d);
        timeExerciseDetails.time = (float)duration;
        return timeExerciseDetails;
    }

    @Override
    protected void onSaveResult(RoutineExercise.TimeExerciseDetails exerciseDetails) {
        application().getTrainingPlan().commitTimeSet(exerciseDetails.time);
        application().getTrainingPlan().nextExercise();
        owner().updateTile();
    }

}
