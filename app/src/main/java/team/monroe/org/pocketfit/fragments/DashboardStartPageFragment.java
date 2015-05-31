package team.monroe.org.pocketfit.fragments;

import team.monroe.org.pocketfit.R;

@Deprecated
public abstract class DashboardStartPageFragment extends DashboardPageFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_single_tile_no_bottom;
    }

}
