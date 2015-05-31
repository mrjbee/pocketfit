package team.monroe.org.pocketfit;

import android.os.Bundle;

import team.monroe.org.pocketfit.fragments.DashboardNoBottomNoTopTileFragment;

public class PageHistoryFragment extends DashboardNoBottomNoTopTileFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        configureHeader("History", null, false, false);
    }

    @Override
    protected int getTileLayoutId() {
        return R.layout.page_content_history;
    }

    @Override
    public void onMainButton() {

    }


}
