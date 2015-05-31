package team.monroe.org.pocketfit.fragments;

import team.monroe.org.pocketfit.R;

public abstract class DefaultPageFragment extends DashboardPageFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_single_tile_no_top_no_bottom;
    }

    @Override
    protected boolean headerFullVersion() {
        return false;
    }

    @Override
    protected boolean headerLightVersion() {
        return false;
    }
}
