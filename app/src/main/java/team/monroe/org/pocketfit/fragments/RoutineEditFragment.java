package team.monroe.org.pocketfit.fragments;


import android.os.Bundle;

import team.monroe.org.pocketfit.R;

public class RoutineEditFragment extends BodyFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_routines;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
