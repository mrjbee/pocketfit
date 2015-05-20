package team.monroe.org.pocketfit.fragments;

import team.monroe.org.pocketfit.R;

public class NoActiveRoutineFragment extends BodyFragment{

    @Override
    protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    protected String getHeaderName() {
        return "Workout";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_no_active_routine;
    }
}
