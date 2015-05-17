package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import org.monroe.team.android.box.app.ui.GenericListViewAdapter;
import org.monroe.team.android.box.app.ui.GetViewImplementation;

import team.monroe.org.pocketfit.R;

public class RoutineTrainingFragment extends BodyFragment{

    private Spinner mRestLongSpinner;
    private GenericListViewAdapter<Integer, GetViewImplementation.ViewHolder<Integer>> mRestLongAdapter;

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
    protected String getHeaderName() {
        return "Manage Training";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_training_day;
    }
}
