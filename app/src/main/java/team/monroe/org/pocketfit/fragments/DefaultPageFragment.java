package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;

import team.monroe.org.pocketfit.R;

public abstract class DefaultPageFragment extends DashboardPageFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_single_tile_no_top_no_bottom;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getHeaderContainer().setVisibility(View.VISIBLE);
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
