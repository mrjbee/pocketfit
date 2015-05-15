package team.monroe.org.pocketfit.fragments;


import android.os.Bundle;

import org.monroe.team.android.box.app.FragmentSupport;

import team.monroe.org.pocketfit.Dashboard;
import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.view.SlidingRelativeLayout;

public class RoutineEditFragment extends BodyFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_routine_editor;
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
