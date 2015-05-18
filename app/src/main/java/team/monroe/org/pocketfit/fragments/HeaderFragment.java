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

import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.*;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.RootActivity;

public class HeaderFragment extends FragmentSupport<PocketFitApp>{

    private AppearanceController mainHeaderContainerAC;
    private AppearanceController secondaryHeaderContainerAC;
    private boolean secondaryHeaderActivated = false;
    private String headerCaption = "";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_header;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            secondaryHeaderActivated = savedInstanceState.getBoolean("header_secondary",false);
            headerCaption = savedInstanceState.getString("header_caption","Uppss Not Set");
        }

        mainHeaderContainerAC = combine(
                animateAppearance(view(R.id.header_main_container),xSlide(0, -DisplayUtils.screenWidth(getResources())/2))
                .showAnimation(duration_constant(200),interpreter_overshot())
                .hideAnimation(duration_constant(200), interpreter_accelerate(0.5f))
                .hideAndGone(),

                animateAppearance(view(R.id.panel_actions),xSlide(0, DisplayUtils.screenWidth(getResources())/2))
                .showAnimation(duration_constant(200),interpreter_overshot())
                .hideAnimation(duration_constant(200), interpreter_accelerate(0.5f))
        );

        secondaryHeaderContainerAC = combine(
                animateAppearance(view(R.id.header_secondary_container), xSlide(0, -DisplayUtils.screenWidth(getResources()) / 2))
                .showAnimation(duration_constant(200), interpreter_overshot())
                .hideAnimation(duration_constant(200), interpreter_accelerate(0.5f))
                .hideAndGone(),

                animateAppearance(view(R.id.panel_actions),xSlide(0, DisplayUtils.screenWidth(getResources())/2))
                .showAnimation(duration_constant(200),interpreter_overshot())
                .hideAnimation(duration_constant(200), interpreter_accelerate(0.5f)),

                animateAppearance(view(R.id.secondary_caption_arrow),rotate(0, -180))
                .showAnimation(duration_constant(500), interpreter_overshot())
                .hideAnimation(duration_constant(200), interpreter_accelerate(0.5f))
                .hideAndGone());

        if (secondaryHeaderActivated){
            mainHeaderContainerAC.hideWithoutAnimation();
            secondaryHeaderContainerAC.showWithoutAnimation();
        }else {
            secondaryHeaderContainerAC.hideWithoutAnimation();
            mainHeaderContainerAC.showWithoutAnimation();
        }

        build_header();

        view(R.id.header_secondary_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackHeaderArrow();
            }
        });
    }

    private void build_header() {
        getHeaderCaptionView().setText(headerCaption);
        ViewGroup group = view(R.id.panel_actions, ViewGroup.class);
        group.removeAllViews();
        View answer = ((RootActivity)activity()).build_actions(group);
        if (answer != null) {
            group.addView(answer);
        }
    }

    private void onBackHeaderArrow() {
        activity().onBackPressed();
    }

    private TextView getHeaderCaptionView() {
        return view_text(secondaryHeaderActivated? R.id.secondary_caption:R.id.caption);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("header_secondary", secondaryHeaderActivated);
        outState.putString("header_caption",headerCaption);
    }

    public void changeCaption(String newCaption, boolean secondaryHeaderRequested, boolean animate) {
        AppearanceController hideController = secondaryHeaderActivated?secondaryHeaderContainerAC:mainHeaderContainerAC;
        secondaryHeaderActivated = secondaryHeaderRequested;
        final AppearanceController showController = secondaryHeaderActivated?secondaryHeaderContainerAC:mainHeaderContainerAC;
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
        return secondaryHeaderActivated;
    }
}
