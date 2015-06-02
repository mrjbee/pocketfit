package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.RoutineExercise;

public class TrainingTileDistanceResultFragment extends TrainingTileResultFragment<RoutineExercise.DistanceExerciseDetails> {

    @Override
    protected int setNumber() {
        return -1;
    }

    @Override
    protected RoutineExercise.DistanceExerciseDetails createExerciseDetails() {
        RoutineExercise.DistanceExerciseDetails distanceExerciseDetails = new RoutineExercise.DistanceExerciseDetails();
        distanceExerciseDetails.distance = ((RoutineExercise.DistanceExerciseDetails)routineExercise().exerciseDetails).distance;
        return distanceExerciseDetails;
    }

    @Override
    protected void onSaveResult(RoutineExercise.DistanceExerciseDetails exerciseDetails) {
        application().getTrainingPlan().commitDistanceSet(exerciseDetails.distance);
        application().getTrainingPlan().nextExercise();
        owner().updateTile();
    }

}
