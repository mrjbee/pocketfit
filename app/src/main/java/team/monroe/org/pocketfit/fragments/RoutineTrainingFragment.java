package team.monroe.org.pocketfit.fragments;

public class RoutineTrainingFragment extends BodyFragment{
    @Override
    protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    protected String getHeaderName() {
        return "Training Day";
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }
}
