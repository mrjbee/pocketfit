package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import org.monroe.team.corebox.log.L;

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
            if(top != 0) {
                float shownFraction = 1 - top / (float) getFragmentView().getHeight();
                L.DEBUG.d("TEST fraction = " + shownFraction + " top:" + top);
                float scaleFactor = 0.8f + 0.3f * shownFraction;
                getSecondaryHeaderView().setScaleX(scaleFactor);
                getSecondaryHeaderView().setScaleY(scaleFactor);
            }else {
                getSecondaryHeaderView().animate().setDuration(200).setInterpolator(new OvershootInterpolator()).scaleX(1).scaleY(1).start();
            }
        }else{
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
