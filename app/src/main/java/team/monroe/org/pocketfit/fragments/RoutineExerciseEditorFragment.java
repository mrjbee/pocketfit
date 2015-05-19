package team.monroe.org.pocketfit.fragments;

import android.view.View;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.Exercise;
import team.monroe.org.pocketfit.presentations.RoutineExercise;

public class RoutineExerciseEditorFragment extends BodyFragment {

    private String mDayId;
    private String mRoutineExerciseId;
    private String mExerciseId;
    private RoutineExercise mRoutineExercise;

    @Override
    protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    protected String getHeaderName() {
        return "Exercise Characteristics";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_day_exercise;
    }

    @Override
    public void onStart() {
        super.onStart();
        mDayId = getStringArgument("day_id");
        mRoutineExerciseId = getStringArgument("routine_exercise_id");
        if (mRoutineExerciseId == null)throw new NullPointerException();
        mExerciseId = getStringArgument("exercise_id");

        application().function_getRoutineExercise(mRoutineExerciseId, observe_function(State.STOP, new PocketFitApp.DataAction<RoutineExercise>() {
            @Override
            public void data(RoutineExercise routineExercise) {
                mRoutineExercise = routineExercise;
                if (mRoutineExercise == null){
                    mRoutineExercise = new RoutineExercise(mRoutineExerciseId);
                }
                if (mExerciseId != null && !mExerciseId.equals(mRoutineExercise.getExerciseId())){
                    //changing exercise
                    application().function_getExercise(mExerciseId, observe_function(State.STOP, new PocketFitApp.DataAction<Exercise>() {
                        @Override
                        public void data(Exercise exercise) {
                            mRoutineExercise.exercise = exercise;
                            RoutineExercise.ExerciseDetails details = getExerciseDetails(exercise);
                            mRoutineExercise.exerciseDetails = details;
                            updateExerciseDetailsUI();
                        }

                        private RoutineExercise.ExerciseDetails getExerciseDetails(Exercise exercise) {
                            RoutineExercise.ExerciseDetails details;
                            switch (exercise.type){
                                case distance:
                                    details = new RoutineExercise.DistanceExerciseDetails();
                                    break;
                                case time:
                                    details = new RoutineExercise.TimeExerciseDetails();
                                    break;
                                case times:
                                    details = new RoutineExercise.TimesExerciseDetails();
                                    break;
                                case weight_times:
                                    details = new RoutineExercise.PowerExerciseDetails();
                                    break;
                                default:
                                    throw new IllegalStateException();
                            }
                            return details;
                        }
                    }));
                } else {
                    if (mRoutineExercise.exercise == null) throw new NullPointerException();
                    updateExerciseDetailsUI();
                }
            }
        }));


        view(R.id.action_change_exercise).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                owner().open_exercisesAsChooser(mDayId, mRoutineExerciseId, false);
            }
        });
    }

    private void updateExerciseDetailsUI() {
        view_text(R.id.text_title).setText(mRoutineExercise.exercise.title);
        view_text(R.id.text_description).setText(mRoutineExercise.exercise.description);
    }
}
