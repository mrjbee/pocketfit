package team.monroe.org.pocketfit.fragments;

import team.monroe.org.pocketfit.R;

public class TrainingDayFragment extends BodyFragment {

    @Override
    protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    protected String getHeaderName() {
        return "Manage Trainings";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_days;
    }

}
