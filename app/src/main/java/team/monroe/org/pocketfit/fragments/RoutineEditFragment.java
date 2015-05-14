package team.monroe.org.pocketfit.fragments;


import android.animation.Animator;
import android.app.Activity;

import org.monroe.team.android.box.app.FragmentSupport;
import org.monroe.team.android.box.app.ui.animation.AnimatorListenerSupport;
import org.monroe.team.corebox.log.L;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.view.SlidingRelativeLayout;

public class RoutineEditFragment extends FragmentSupport<PocketFitApp> {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_routine_editor;
    }

    @Override
    public void onResume() {
        super.onResume();
        SlidingRelativeLayout relativeLayout = (SlidingRelativeLayout) getFragmentView();
        relativeLayout.mSlidingListener = new SlidingRelativeLayout.SlidingListener() {
            @Override
            public void onXFraction(float fraction) {
                if (fraction == 0){
                    onAnimationEnd();
                }
            }
        };
    }

    private void onAnimationEnd() {

    }
}
