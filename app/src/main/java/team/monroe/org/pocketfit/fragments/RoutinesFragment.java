package team.monroe.org.pocketfit.fragments;


import android.os.Bundle;
import android.view.View;

import team.monroe.org.pocketfit.R;

public class RoutinesFragment extends BodyFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_routines;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view(R.id.panel_new_routine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                owner().open_Routine(null);
            }
        });
    }

    @Override
    protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    protected String getHeaderName() {
        return "Workout Routines";
    }

}
