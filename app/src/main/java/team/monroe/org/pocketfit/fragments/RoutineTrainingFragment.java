package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import org.monroe.team.android.box.app.ui.GenericListViewAdapter;
import org.monroe.team.android.box.app.ui.GetViewImplementation;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.RoutineDay;

public class RoutineTrainingFragment extends BodyFragment{

    private Spinner mRestLongSpinner;
    private GenericListViewAdapter<Integer, GetViewImplementation.ViewHolder<Integer>> mRestLongAdapter;
    private String mRoutineDayId;
    private RoutineDay mRoutineDay;
    private String mRoutineId;

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
                    mRoutineDay = new RoutineDay(mRoutineDayId, mRoutineId);
                }
                view_text(R.id.edit_description).setText(mRoutineDay.description);
                mRestLongSpinner.setSelection(mRoutineDay.restDays == null ? 0 : mRoutineDay.restDays);
            }
        }));
    }

    @Override
    public void onStop() {
        super.onStop();
        mRoutineDay.description = view_text(R.id.edit_description).getText().toString();
        mRoutineDay.restDays = mRestLongSpinner.getSelectedItemPosition();
        application().function_updateRoutineDay(mRoutineDay);
    }

    @Override
    protected String getHeaderName() {
        return "Manage Training";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_training_day;
    }
}
