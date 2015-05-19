package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.Exercise;
import team.monroe.org.pocketfit.presentations.RoutineExercise;
import team.monroe.org.pocketfit.view.presenter.ViewPresenter;

public class RoutineExerciseEditorFragment extends BodyFragment {

    private String mDayId;
    private String mRoutineExerciseId;
    private String mExerciseId;
    private RoutineExercise mRoutineExercise;

    private ExerciseDetailsViewPresenter<RoutineExercise.PowerExerciseDetails> powerExerciseDetailsViewPresenter;
    private ExerciseDetailsViewPresenter<RoutineExercise.DistanceExerciseDetails> distanceExerciseDetailsViewPresenter;
    private ExerciseDetailsViewPresenter<RoutineExercise.TimeExerciseDetails> timeExerciseDetailsViewPresenter;
    private ExerciseDetailsViewPresenter<RoutineExercise.TimesExerciseDetails> timesExerciseDetailsViewPresenter;

    private ExerciseDetailsViewPresenter mDetailsViewPresenter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        powerExerciseDetailsViewPresenter = new ExerciseDetailsViewPresenter<RoutineExercise.PowerExerciseDetails>(view(R.id.panel_power)) {
            @Override
            public void fillDetails(RoutineExercise.PowerExerciseDetails details) {
                details.times  = readPositiveInteger(R.id.edit_power_times);
                details.sets  = readPositiveInteger(R.id.edit_power_sets);
                details.weight  = readPositiveFloat(R.id.edit_power_weight);
            }

            @Override
            protected void fillUI(RoutineExercise.PowerExerciseDetails details) {
                updateTextView(R.id.edit_power_weight, details.weight);
                updateTextView(R.id.edit_power_sets, details.sets);
                updateTextView(R.id.edit_power_times, details.times);
            }

        };
        distanceExerciseDetailsViewPresenter = new ExerciseDetailsViewPresenter<RoutineExercise.DistanceExerciseDetails>(view(R.id.panel_disatnce)) {
            @Override
            public void fillDetails(RoutineExercise.DistanceExerciseDetails details) {

            }

            @Override
            protected void fillUI(RoutineExercise.DistanceExerciseDetails details) {

            }
        };
        timeExerciseDetailsViewPresenter = new ExerciseDetailsViewPresenter<RoutineExercise.TimeExerciseDetails>(view(R.id.panel_time)) {
            @Override
            public void fillDetails(RoutineExercise.TimeExerciseDetails details) {

            }

            @Override
            protected void fillUI(RoutineExercise.TimeExerciseDetails details) {

            }
        };
        timesExerciseDetailsViewPresenter = new ExerciseDetailsViewPresenter<RoutineExercise.TimesExerciseDetails>(view(R.id.panel_times)) {
            @Override
            public void fillDetails(RoutineExercise.TimesExerciseDetails details) {

            }

            @Override
            protected void fillUI(RoutineExercise.TimesExerciseDetails details) {

            }
        };
    }

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

    @Override
    public void onStop() {
        super.onStop();
        if (mDetailsViewPresenter != null) {
            mDetailsViewPresenter.fillDetails(mRoutineExercise.exerciseDetails);
            application().function_updateRoutineExercise(mRoutineExercise, mDayId, Integer.MAX_VALUE, observe_function(State.STOP, new PocketFitApp.DataAction<Void>() {
                @Override
                public void data(Void data) {

                }
            }));
        }
    }

    private void updateExerciseDetailsUI() {
        view_text(R.id.text_title).setText(mRoutineExercise.exercise.title);
        view_text(R.id.text_description).setText(mRoutineExercise.exercise.description);
        switch (mRoutineExercise.exercise.type){
            case distance:
                mDetailsViewPresenter = distanceExerciseDetailsViewPresenter;
                break;
            case time:
                mDetailsViewPresenter = timeExerciseDetailsViewPresenter;
                break;
            case times:
                mDetailsViewPresenter = timesExerciseDetailsViewPresenter;
                break;
            case weight_times:
                mDetailsViewPresenter = powerExerciseDetailsViewPresenter;
                break;
            default:
                throw new IllegalStateException();
        }
        mDetailsViewPresenter.show(mRoutineExercise.exerciseDetails);
    }

    public static abstract class ExerciseDetailsViewPresenter<DetailsType extends RoutineExercise.ExerciseDetails> extends ViewPresenter<View>{

        public ExerciseDetailsViewPresenter(View rootView) {
            super(rootView);
        }

        protected Integer readPositiveInteger(int r_text) {
            Integer value;
            String text = ((TextView)getRootView().findViewById(r_text)).getText().toString();
            try {
                value = Math.abs(Integer.parseInt(text));
            }catch (Exception e){
                value = null;
            }
            return value;
        }

        protected Float readPositiveFloat(int r_text) {
            Float value;
            String text = ((TextView)getRootView().findViewById(r_text)).getText().toString();
            try {
                value = Math.abs(Float.parseFloat(text));
            }catch (Exception e){
                value = null;
            }
            return value;
        }

        protected void updateTextView(int r, Object value) {
            ((TextView)getRootView().findViewById(r)).setText(value == null?"":value.toString());
        }


        public void show(DetailsType details) {
            fillUI(details);
            show();
        }

        protected abstract void fillUI(DetailsType details);
        public abstract void fillDetails(DetailsType details);

    }
}