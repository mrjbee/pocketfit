package team.monroe.org.pocketfit.fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.monroe.team.android.box.app.FragmentSupport;
import org.monroe.team.android.box.app.ui.animation.AnimatorListenerSupport;
import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;
import org.monroe.team.android.box.utils.DisplayUtils;

import team.monroe.org.pocketfit.DashboardActivity;
import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.fragments.contract.HeaderContract;
import team.monroe.org.pocketfit.fragments.contract.HeaderOwnerContract;

import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.animateAppearance;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.combine;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.duration_constant;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.interpreter_accelerate;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.interpreter_overshot;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.rotate;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.xSlide;

public class HeaderDashboardFragment extends AppFragment<DashboardActivity>{

    private boolean mShown = true;
    private float mCaptionHeight;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dash_header;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCaptionHeight = DisplayUtils.dpToPx(100, getResources());
    }

    public void changeHeader(String title, View actions, boolean topHeaderRequired, boolean light){

        int textColor = getResources().getColor(light? R.color.text_header_dash:R.color.text_header_dash_dark);

        view_text(R.id.secondary_caption).setTextColor(textColor);
        view_text(R.id.secondary_caption).setText(title);

        ViewGroup container = view(R.id.panel_actions, ViewGroup.class);
        container.removeAllViews();
        if (actions != null) {
            container.addView(actions);
        }

        if (!mShown){
            //appear animation required
            mShown = true;
        }

        if (DisplayUtils.isLandscape(getResources(), R.bool.class)){
            view_text(R.id.caption).setVisibility(View.GONE);
        }else {
            scaleCaption(1f);
            view_text(R.id.caption).setVisibility(topHeaderRequired?View.VISIBLE:View.GONE);
        }
    }

    public void updateAlpha(float alpha) {
        getFragmentView().setAlpha(alpha);
    }

    public void scaleCaption(float fraction) {
        getFragmentView().setTranslationY((1- fraction) * - mCaptionHeight);
    }
}
