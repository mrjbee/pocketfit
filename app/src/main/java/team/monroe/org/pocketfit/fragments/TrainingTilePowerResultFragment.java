package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.RoutineExercise;
import team.monroe.org.pocketfit.view.presenter.ClockViewPresenter;

public class TrainingTilePowerResultFragment extends TrainingTileResultFragment<RoutineExercise.PowerExerciseDetails> {

    @Override
    protected int setNumber() {
        return (application().getTrainingPlan().getSetIndex() + 1);
    }

    @Override
    protected RoutineExercise.PowerExerciseDetails createExerciseDetails() {
        RoutineExercise.PowerExerciseDetails answer = new RoutineExercise.PowerExerciseDetails();
        answer.sets = -1;
        answer.weight = ((RoutineExercise.PowerExerciseDetails)routineExercise().exerciseDetails).weight;
        answer.times = ((RoutineExercise.PowerExerciseDetails)routineExercise().exerciseDetails).times;
        return answer;
    }

    @Override
    protected void onSaveResult(RoutineExercise.PowerExerciseDetails exerciseDetails) {
        application().getTrainingPlan().commitPowerSet(exerciseDetails.weight, exerciseDetails.times);
        if (application().getTrainingPlan().hasMoreSetsScheduled()){
            application().getTrainingPlan().addSet();
        }
        owner().updateTile();
    }
}
