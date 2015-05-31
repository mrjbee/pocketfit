package team.monroe.org.pocketfit.fragments;

import team.monroe.org.pocketfit.R;

public abstract class DashboardNoBottomNoTopTileFragment extends AbstractDashboardTileFragment{
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_single_tile_no_top_no_bottom;
    }
}
