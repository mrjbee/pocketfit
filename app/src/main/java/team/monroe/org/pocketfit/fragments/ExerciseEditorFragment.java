package team.monroe.org.pocketfit.fragments;


import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import org.monroe.team.android.box.app.ui.GenericListViewAdapter;
import org.monroe.team.android.box.app.ui.GetViewImplementation;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.RoutineSetupActivity;
import team.monroe.org.pocketfit.presentations.Exercise;

public class ExerciseEditorFragment extends BodyFragment<RoutineSetupActivity>{

    private Spinner mTypeSpinner;
    private GenericListViewAdapter<String, GetViewImplementation.ViewHolder<String>> mTypeAdapter;
    private String mExerciseId;
    private Exercise mExercise;

    @Override
    protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    protected String getHeaderName() {
        return "Edit Exercise";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_exercise;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTypeSpinner = view(R.id.spinner, Spinner.class);
        mTypeAdapter = new GenericListViewAdapter<String,GetViewImplementation.ViewHolder<String>>(activity(),new GetViewImplementation.ViewHolderFactory<GetViewImplementation.ViewHolder<String>>() {
            @Override
            public GetViewImplementation.ViewHolder<String> create(final View convertView) {
                return new GetViewImplementation.GenericViewHolder<String>() {
                    TextView caption = (TextView) convertView.findViewById(R.id.item_caption);
                    @Override
                    public void update(String typeString, int position) {
                        caption.setText(typeString);
                    }
                };
            }
        }, R.layout.item_simple);

        for (Exercise.Type type : Exercise.Type.values()) {
            mTypeAdapter.add(type.human);
        }
        mTypeSpinner.setAdapter(mTypeAdapter);
        mTypeSpinner.setEnabled(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        mExerciseId = getStringArgument("exercise_id");
        if (mExerciseId == null) throw new IllegalStateException();
        application().function_getExercise(mExerciseId, observe_function(State.STOP, new PocketFitApp.DataAction<Exercise>() {
            @Override
            public void data(Exercise exercise) {
                mExercise = exercise;
                if (mExercise == null) mExercise = new Exercise(mExerciseId);
                view_text(R.id.edit_title).setText(mExercise.title);
                view_text(R.id.edit_description).setText(mExercise.description);
                if (mExercise.type != null){
                    mTypeSpinner.setSelection(mExercise.type.ordinal());
                    application().function_getExerciseTypeEditable(mExercise.id, observe_function(State.STOP, new PocketFitApp.DataAction<Boolean>() {
                        @Override
                        public void data(Boolean data) {
                            mTypeSpinner.setEnabled(data);
                        }
                    }));
                }else {
                    mTypeSpinner.setEnabled(true);
                }
            }
        }));
    }

    @Override
    public void onStop() {
        super.onStop();
        mExercise.title = view_text(R.id.edit_title).getText().toString();
        mExercise.description = view_text(R.id.edit_description).getText().toString();
        mExercise.type = Exercise.Type.values()[mTypeSpinner.getSelectedItemPosition()];
        application().function_updateExercise(mExercise, observe_function(State.STOP, new PocketFitApp.DataAction<Void>() {
            @Override
            public void data(Void data) {

            }
        }));
    }
}
