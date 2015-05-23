package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.monroe.team.android.box.app.ui.GenericListViewAdapter;
import org.monroe.team.android.box.app.ui.GetViewImplementation;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.RoutinesActivity;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;
import team.monroe.org.pocketfit.presentations.RoutineExercise;
import team.monroe.org.pocketfit.uc.UpdateRoutineDay;
import team.monroe.org.pocketfit.uc.UpdateRoutineExercise;
import team.monroe.org.pocketfit.view.presenter.ListViewPresenter;

public class RoutineDayEditorFragment extends BodyFragment<RoutinesActivity>{

    private final static PositionDescription POSITION_AFTER_ALL = new PositionDescription("Last. After All", UpdateRoutineDay.RoutineDayUpdate.INDEX_ADD_LAST);

    private Spinner mRestLongSpinner;
    private GenericListViewAdapter<Integer, GetViewImplementation.ViewHolder<Integer>> mRestLongAdapter;
    private String mRoutineDayId;
    private RoutineDay mRoutineDay;
    private String mRoutineId;
    private Spinner mPositionSpinner;
    private GenericListViewAdapter<PositionDescription, GetViewImplementation.ViewHolder<PositionDescription>> mPositionAdapter;
    private ListViewPresenter<RoutineExercise> listViewPresenter;

    @Override
    protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRestLongSpinner = view(R.id.spinner, Spinner.class);
        mRestLongAdapter = new GenericListViewAdapter<Integer,GetViewImplementation.ViewHolder<Integer>>(activity(),new GetViewImplementation.ViewHolderFactory<GetViewImplementation.ViewHolder<Integer>>() {
            @Override
            public GetViewImplementation.ViewHolder<Integer> create(final View convertView) {
                return new GetViewImplementation.GenericViewHolder<Integer>() {
                    TextView caption = (TextView) convertView.findViewById(R.id.item_caption);
                    @Override
                    public void update(Integer integer, int position) {
                         caption.setText(integer+" days");
                    }
                };
            }
        }, R.layout.item_simple);
        for (int i = 0; i < 15; i++){
            mRestLongAdapter.add(i);
        }
        mRestLongSpinner.setAdapter(mRestLongAdapter);

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

        view(R.id.button_add_exercise).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                application().function_createId("dex",observe_function(State.STOP, new PocketFitApp.DataAction<String>() {
                    @Override
                    public void data(String id) {
                        String title = view_text(R.id.edit_description).getText().toString();
                        if (!title.trim().isEmpty()){
                            owner(RoutinesActivity.class).open_exercisesAsChooser(mRoutineDay.id, id, true);
                        }else{
                            Toast.makeText(getActivity(), "Please add description first", Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
            }
        });

        listViewPresenter = new ListViewPresenter<RoutineExercise>(view(R.id.panel_exercises, ViewGroup.class)) {
            @Override
            protected View data_to_view(int index, final RoutineExercise routineExercise, final ViewGroup owner, LayoutInflater inflater) {
                View view = inflater.inflate(R.layout.item_day,owner, false);
                ((TextView)view.findViewById(R.id.item_caption)).setText(routineExercise.exercise.title);
                ((TextView)view.findViewById(R.id.item_sub_caption)).setText(routineExercise.exerciseDetails.detailsString());
                ((TextView)view.findViewById(R.id.item_text)).setText(routineExercise.exercise.description);
                ((TextView)view.findViewById(R.id.item_index)).setText(""+(index + 1));
                if (index == 0){
                    view.findViewById(R.id.item_image).setBackgroundResource(R.drawable.step_top);
                } else if (index == mRoutineDay.exerciseList.size()-1){
                    view.findViewById(R.id.item_image).setBackgroundResource(R.drawable.step_bottom);
                }else {
                    view.findViewById(R.id.item_image).setBackgroundResource(R.drawable.step);
                }

                view.findViewById(R.id.item_action).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        owner(RoutinesActivity.class).open_RoutineExercise(mRoutineDay.id, routineExercise.id);
                    }
                });
                view.findViewById(R.id.item_trash).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        application().function_updateRoutineExercise(routineExercise, mRoutineDay.id, UpdateRoutineExercise.RoutineExerciseUpdate.INDEX_DELETE, observe_function(State.STOP,new PocketFitApp.DataAction<Void>() {
                            @Override
                            public void data(Void data) {
                               updateRoutineDay();
                            }
                        }));
                    }
                });
                return view;
            }

            @Override
            protected String data_to_id(RoutineExercise routineExercise) {
                return routineExercise.id;
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mRoutineId = getStringArgument("routine_id");
        mRoutineDayId = getStringArgument("day_id");
        if (mRoutineDayId == null){
            throw new IllegalStateException();
        }
        updateRoutineDay();
        application().function_getRoutine(mRoutineId, observe_function(State.STOP, new PocketFitApp.DataAction<Routine>() {
            @Override
            public void data(Routine routine) {
                int position_index = 0;
                mPositionAdapter.clear();
                for (int i= 0; i < routine.trainingDays.size(); i++){
                    RoutineDay routineDay = routine.trainingDays.get(i);
                    if (mRoutineDayId.equals(routineDay.id)){
                        position_index = i;
                        String positionDescription = (i+1)+". Do not change";
                        mPositionAdapter.add(new PositionDescription(positionDescription,i));
                    }else {
                        String positionDescription = (i+1)+"."+(routineDay.hasDescription() ? " Before step: "+ routineDay.description:" Routine Step");
                        mPositionAdapter.add(new PositionDescription(positionDescription,i));
                    }
                }
                mPositionAdapter.add(POSITION_AFTER_ALL);
                mPositionAdapter.notifyDataSetChanged();
                mPositionSpinner.setSelection(position_index);
            }
        }));
    }

    private void updateRoutineDay() {
        application().function_getRoutineDay(mRoutineDayId, observe_function(State.STOP, new PocketFitApp.DataAction<RoutineDay>() {
            @Override
            public void data(RoutineDay day) {
                mRoutineDay = day;
                if (mRoutineDay == null){
                    mRoutineDay = new RoutineDay(mRoutineDayId);
                }
                view_text(R.id.edit_description).setText(mRoutineDay.description);
                view_text(R.id.text_exercises).setText(mRoutineDay.exerciseList.size()+"");
                mRestLongSpinner.setSelection(mRoutineDay.restDays == null ? 0 : mRoutineDay.restDays);
                listViewPresenter.synchronizeItems(mRoutineDay.exerciseList);
            }
        }));
    }

    //button_add_exercise
    @Override
    public void onStop() {
        super.onStop();
        mRoutineDay.description = view_text(R.id.edit_description).getText().toString();
        mRoutineDay.restDays = mRestLongSpinner.getSelectedItemPosition();
        PositionDescription positionDescription = (PositionDescription) mPositionSpinner.getSelectedItem();
        application().function_updateRoutineDay(mRoutineDay, mRoutineId, positionDescription.index, null);
    }

    @Override
    protected String getHeaderName() {
        return "Manage Training";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_day;
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
