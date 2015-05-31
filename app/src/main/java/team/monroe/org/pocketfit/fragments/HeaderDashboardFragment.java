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

public class HeaderDashboardFragment extends AppFragment<DashboardActivity> implements HeaderContract{

    private AppearanceController secondaryHeaderContainerAC;
    private String headerCaption = "";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dash_header;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            headerCaption = savedInstanceState.getString("header_caption","Uppss Not Set");
        }

        secondaryHeaderContainerAC = combine(
                animateAppearance(view(R.id.header_secondary_container), xSlide(0, -DisplayUtils.screenWidth(getResources()) / 2))
                .showAnimation(duration_constant(200), interpreter_overshot())
                .hideAnimation(duration_constant(200), interpreter_accelerate(0.5f))
                .hideAndGone(),

                animateAppearance(view(R.id.panel_actions),xSlide(0, DisplayUtils.screenWidth(getResources())/2))
                .showAnimation(duration_constant(200),interpreter_overshot())
                .hideAnimation(duration_constant(200), interpreter_accelerate(0.5f))
                );

            secondaryHeaderContainerAC.showWithoutAnimation();
            build_header();
    }

    private void build_header() {
        getHeaderCaptionView().setText(headerCaption);
        ViewGroup group = view(R.id.panel_actions, ViewGroup.class);
        group.removeAllViews();
        View answer = owner().buildHeaderActionsView(group);
        if (answer != null) {
            group.addView(answer);
        }
    }

    private TextView getHeaderCaptionView() {
        return view_text(R.id.secondary_caption);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("header_caption",headerCaption);
    }

    public void changeCaption(String newCaption, boolean secondaryHeaderRequested, boolean animate) {
        AppearanceController hideController = secondaryHeaderContainerAC;
        final AppearanceController showController = secondaryHeaderContainerAC;
        headerCaption = newCaption;
        if (animate) {
            hideController.hideAndCustomize(new AppearanceController.AnimatorCustomization() {
                @Override
                public void customize(Animator animator) {
                    animator.addListener(new AnimatorListenerSupport() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            build_header();
                            showController.hideWithoutAnimation();
                            showController.show();
                        }
                    });
                }
            });
        } else {
            hideController.hideWithoutAnimation();
            showController.showWithoutAnimation();
            build_header();
        }
    }
    public String getCaption() {
        return headerCaption;
    }
    public boolean isSecondary() {
        return true;
    }


}
