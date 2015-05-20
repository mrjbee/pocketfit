package team.monroe.org.pocketfit;

import team.monroe.org.pocketfit.fragments.NoActiveRoutineFragment;

public class DashboardActivity extends FragmentActivity {
    @Override
    protected FragmentItem customize_startupFragment() {
        return new FragmentItem(NoActiveRoutineFragment.class);
    }

    @Override
    protected int customize_rootLayout() {
        return R.layout.activity_dashboard;
    }
}
