package team.monroe.org.pocketfit.fragments;

import team.monroe.org.pocketfit.R;

public class RoutineEditorFragment extends BodyFragment {
    @Override
    protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    protected String getHeaderName() {
        return "Edit Routine";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_routine;
    }
}
