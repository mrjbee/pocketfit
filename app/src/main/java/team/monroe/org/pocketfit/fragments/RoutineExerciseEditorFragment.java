package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import org.monroe.team.android.box.app.ui.GenericListViewAdapter;
import org.monroe.team.android.box.app.ui.GetViewImplementation;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.RoutinesActivity;
import team.monroe.org.pocketfit.presentations.Exercise;
import team.monroe.org.pocketfit.presentations.RoutineDay;
import team.monroe.org.pocketfit.presentations.RoutineExercise;
import team.monroe.org.pocketfit.uc.UpdateRoutineDay;
import team.monroe.org.pocketfit.view.presenter.TimePickPresenter;
import team.monroe.org.pocketfit.view.presenter.ViewPresenter;

public class RoutineExerciseEditorFragment extends BodyFragment<RoutinesActivity> {

    private final static PositionDescription POSITION_AFTER_ALL = new PositionDescription("Last. After All", UpdateRoutineDay.RoutineDayUpdate.INDEX_ADD_LAST);

    private String mDayId;
    private String mRoutineExerciseId;
    private String mExerciseId;
    private RoutineExercise mRoutineExercise;

    private ExerciseDetailsViewPresenter<RoutineExercise.PowerExerciseDetails> powerExerciseDetailsViewPresenter;
    private ExerciseDetailsViewPresenter<RoutineExercise.DistanceExerciseDetails> distanceExerciseDetailsViewPresenter;
    private ExerciseDetailsViewPresenter<RoutineExercise.TimeExerciseDetails> timeExerciseDetailsViewPresenter;

    private ExerciseDetailsViewPresenter mDetailsViewPresenter;

    private GenericListViewAdapter<PositionDescription, GetViewImplementation.ViewHolder<PositionDescription>> mPositionAdapter;
    private Spinner mPositionSpinner;
    private TimePickPresenter mTimePickPresenter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTimePickPresenter = new TimePickPresenter(view(R.id.panel_time_2));

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
                details.distance  = readPositiveFloat(R.id.edit_distance);
            }

            @Override
            protected void fillUI(RoutineExercise.DistanceExerciseDetails details) {
                updateTextView(R.id.edit_distance, details.distance);
            }
        };
        timeExerciseDetailsViewPresenter = new ExerciseDetailsViewPresenter<RoutineExercise.TimeExerciseDetails>(view(R.id.panel_time_2)) {
            @Override
            public void fillDetails(RoutineExercise.TimeExerciseDetails details) {
                details.time  = mTimePickPresenter.getMinutes();
            }

            @Override
            protected void fillUI(RoutineExercise.TimeExerciseDetails details) {
                Float existingTime =  details.time;
                mTimePickPresenter.setMinutes(existingTime);
            }
        };


        mPositionSpinner = view(R.id.spinner_position, Spinner.class);
        mPositionAdapter = new GenericListViewAdapter<PositionDescription,GetViewImplementation.ViewHolder<PositionDescription>>(activity(),new GetViewImplementation.ViewHolderFactory<GetViewImplementation.ViewHolder<PositionDescription>>() {
            @Override
            public GetViewImplementation.ViewHolder<PositionDescription> create(final View convertView) {
                return new GetViewImplementation.GenericViewHolder<PositionDescription>() {
                    TextView caption = (TextView) convertView.findViewById(R.id.item_caption);
                    @Override
                    public void update(PositionDescription description, int position) {
                        caption.setText(description.description);
                    }
                };
            }
        }, R.layout.item_simple);
        mPositionAdapter.add(POSITION_AFTER_ALL);
        mPositionSpinner.setAdapter(mPositionAdapter);
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
                ownerContract(RoutinesActivity.class).open_exercisesAsChooser(mDayId, mRoutineExerciseId, false);
            }
        });

        application().function_getRoutineDay(mDayId, observe_function(State.STOP, new PocketFitApp.DataAction<RoutineDay>() {
            @Override
            public void data(RoutineDay routineDay) {
                int position_index = 0;
                mPositionAdapter.clear();
                for (int i = 0; i < routineDay.exerciseList.size(); i++) {
                    RoutineExercise routineExercise = routineDay.exerciseList.get(i);
                    if (mRoutineExerciseId.equals(routineExercise.id)) {
                        position_index = i;
                        String positionDescription = (i + 1) + ". Do not change";
                        mPositionAdapter.add(new PositionDescription(positionDescription, i));
                    } else {
                        String positionDescription = (i + 1) + ". Before " + routineExercise.exercise.title + ":" + routineExercise.exerciseDetails.detailsString();
                        mPositionAdapter.add(new PositionDescription(positionDescription, i));
                    }
                }
                mPositionAdapter.add(POSITION_AFTER_ALL);
                mPositionAdapter.notifyDataSetChanged();
                mPositionSpinner.setSelection(position_index);
            }
        }));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDetailsViewPresenter != null) {
            mDetailsViewPresenter.fillDetails(mRoutineExercise.exerciseDetails);
            PositionDescription positionDescription = (PositionDescription) mPositionSpinner.getSelectedItem();
            application().function_updateRoutineExercise(mRoutineExercise, mDayId, positionDescription.index, observe_function(State.STOP, new PocketFitApp.DataAction<Void>() {
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

    private static class PositionDescription{
        private final String description;
        private final int index;

        private PositionDescription(String description, int index) {
            this.description = description;
            this.index = index;
        }
    }
}
