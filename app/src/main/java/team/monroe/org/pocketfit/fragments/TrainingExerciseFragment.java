package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.TrainingActivity;
import team.monroe.org.pocketfit.TrainingExecutionService;
import team.monroe.org.pocketfit.presentations.Exercise;
import team.monroe.org.pocketfit.presentations.RoutineExercise;

public class TrainingExerciseFragment extends BodyFragment<TrainingActivity> {

    private TrainingExecutionService.TrainingPlan mTrainingPlan;
    private RoutineExercise mRoutineExercise;
    private Exercise.Type mExerciseType;
    private State mState;

    @Override protected boolean isHeaderSecondary() {
        return false;
    }
    @Override protected String getHeaderName() {
        return null;
    }
    @Override protected int getLayoutId() {
        return R.layout.fragment_training_exercise;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTrainingPlan = application().getTrainingPlan();
        mRoutineExercise = mTrainingPlan.getCurrentExercise();

        if (!mTrainingPlan.isSetStarted()){
            mState = State.START_AWAITING;
        }else if (!mTrainingPlan.isSetDone()){
            mState = State.STOP_AWAITING;
        }else if (!mTrainingPlan.isAllSetsCommitted()){
            mState = State.RESULT_AWAITING;
        }else {
            mState = State.FINISH_AWAITING;
        }

        if (mState == null) {
            throw new IllegalStateException();
        }

        fillUI_ExerciseDetails();

    }

    private void fillUI_ExerciseDetails() {
        view_text(R.id.exercise_name).setText(mRoutineExercise.exercise.title);
        view_text(R.id.exercise_description).setText(mRoutineExercise.exercise.description);
        mExerciseType = mRoutineExercise.exercise.type;

        switch (mExerciseType){
            case weight_times:
                RoutineExercise.PowerExerciseDetails exerciseDetails = (RoutineExercise.PowerExerciseDetails) mRoutineExercise.exerciseDetails;
                addDetails("Weight",exerciseDetails.weight, "kg");
                addDetails("Times",exerciseDetails.times, "times");
                addDetails("Sets",exerciseDetails.sets, "sets");
                break;
            case time:
                RoutineExercise.TimeExerciseDetails timeDetails = (RoutineExercise.TimeExerciseDetails) mRoutineExercise.exerciseDetails;
                addDetails("Time",timeDetails.detailsString(), "");
                break;
            case distance:
                RoutineExercise.DistanceExerciseDetails distanceDetails = (RoutineExercise.DistanceExerciseDetails) mRoutineExercise.exerciseDetails;
                addDetails("Distance", distanceDetails.detailsString(), "");
                break;
            default:
                throw new IllegalStateException();
        }
    }

    private void addDetails(String caption, Object value, String measure) {
        ViewGroup viewPanel =view(R.id.panel_exercise_details, ViewGroup.class);
        View view = activity().getLayoutInflater().inflate(R.layout.panel_3_column_details,viewPanel,false);
        ((TextView)view.findViewById(R.id.item_caption)).setText(caption);
        ((TextView)view.findViewById(R.id.item_value)).setText(value.toString());
        ((TextView)view.findViewById(R.id.item_measure)).setText(measure);
        viewPanel.addView(view);
    }

    private enum State {
        START_AWAITING, STOP_AWAITING, RESULT_AWAITING, FINISH_AWAITING
    }
}
