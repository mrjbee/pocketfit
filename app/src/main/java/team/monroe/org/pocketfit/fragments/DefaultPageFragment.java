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
    public void onPageMoveToShow(float top, boolean pageMotionDirectionUp) {
        if (pageMotionDirectionUp){
            if (top != 0){
                getHeaderContainer().setAlpha(0);
            }else{
                //animation here
                getHeaderContainer().setAlpha(1);
            }
        }else{
            getHeaderContainer().setAlpha(1);
        }
    }

    @Override
    public void onPageMoveToHide(float top, boolean pageMotionDirectionUp) {
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
