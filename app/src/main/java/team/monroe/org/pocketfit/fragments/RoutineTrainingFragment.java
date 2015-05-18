package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import org.monroe.team.android.box.app.ui.GenericListViewAdapter;
import org.monroe.team.android.box.app.ui.GetViewImplementation;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;
import team.monroe.org.pocketfit.uc.UpdateRoutineDay;

public class RoutineTrainingFragment extends BodyFragment{

    private final static PositionDescription POSITION_AFTER_ALL = new PositionDescription("Last. After All", UpdateRoutineDay.RoutineDayUpdate.INDEX_ADD_LAST);

    private Spinner mRestLongSpinner;
    private GenericListViewAdapter<Integer, GetViewImplementation.ViewHolder<Integer>> mRestLongAdapter;
    private String mRoutineDayId;
    private RoutineDay mRoutineDay;
    private String mRoutineId;
    private Spinner mPositionSpinner;
    private GenericListViewAdapter<PositionDescription, GetViewImplementation.ViewHolder<PositionDescription>> mPositionAdapter;

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
    }

    @Override
    public void onStart() {
        super.onStart();
        mRoutineId = getStringArgument("routine_id");
        mRoutineDayId = getStringArgument("day_id");
        if (mRoutineDayId == null){
            throw new IllegalStateException();
        }
        application().function_getRoutineDay(mRoutineDayId, observe_function(State.STOP, new PocketFitApp.DataAction<RoutineDay>() {
            @Override
            public void data(RoutineDay day) {
                mRoutineDay = day;
                if (mRoutineDay == null){
                    mRoutineDay = new RoutineDay(mRoutineDayId);
                }
                view_text(R.id.edit_description).setText(mRoutineDay.description);
                mRestLongSpinner.setSelection(mRoutineDay.restDays == null ? 0 : mRoutineDay.restDays);
            }
        }));
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

    @Override
    public void onStop() {
        super.onStop();
        mRoutineDay.description = view_text(R.id.edit_description).getText().toString();
        mRoutineDay.restDays = mRestLongSpinner.getSelectedItemPosition();
        PositionDescription positionDescription = (PositionDescription) mPositionSpinner.getSelectedItem();
        application().function_updateRoutineDay(mRoutineDay, mRoutineId, positionDescription.index);
    }

    @Override
    protected String getHeaderName() {
        return "Manage Training";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_training_day;
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
