package team.monroe.org.pocketfit.fragments;


import team.monroe.org.pocketfit.R;

public class ExerciseEditorFragment extends BodyFragment{
    @Override
    protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    protected String getHeaderName() {
        return "Edit Exercise";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_exercise;
    }
}
