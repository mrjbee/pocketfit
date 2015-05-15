package team.monroe.org.pocketfit.fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.monroe.team.android.box.app.FragmentSupport;
import org.monroe.team.android.box.app.ui.animation.AnimatorListenerSupport;
import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;
import org.monroe.team.android.box.utils.DisplayUtils;

import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.*;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;

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

        mainHeaderContainerAC = animateAppearance(view(R.id.header_main_container),xSlide(0, -DisplayUtils.screenWidth(getResources())/2))
                .showAnimation(duration_constant(300),interpreter_overshot())
                .hideAnimation(duration_constant(300), interpreter_accelerate(0.5f))
                .hideAndGone()
                .build();

        secondaryHeaderContainerAC = combine(
                animateAppearance(view(R.id.header_secondary_container), xSlide(0, -DisplayUtils.screenWidth(getResources()) / 2))
                .showAnimation(duration_constant(300), interpreter_overshot())
                .hideAnimation(duration_constant(300), interpreter_accelerate(0.5f))
                .hideAndGone(),
                animateAppearance(view(R.id.secondary_caption_arrow),rotate(0, -180))
                .showAnimation(duration_constant(500), interpreter_overshot())
                .hideAnimation(duration_constant(500), interpreter_accelerate(0.5f))
                .hideAndGone());

        if (secondaryHeaderActivated){
            mainHeaderContainerAC.hideWithoutAnimation();
            secondaryHeaderContainerAC.showWithoutAnimation();
        }else {
            mainHeaderContainerAC.showWithoutAnimation();
            secondaryHeaderContainerAC.hideWithoutAnimation();
        }

        TextView captionView = getHeaderCaptionView();
        captionView.setText(headerCaption);
        view(R.id.header_secondary_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackHeaderArrow();
            }
        });
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
                    animator.setStartDelay(300);
                    animator.addListener(new AnimatorListenerSupport() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            getHeaderCaptionView().setText(headerCaption);
                            showController.hideWithoutAnimation();
                            showController.show();
                        }
                    });
                }
            });
        } else {
            hideController.hideWithoutAnimation();
            showController.showWithoutAnimation();
            getHeaderCaptionView().setText(headerCaption);
        }
    }

    public String getCaption() {
        return headerCaption;
    }

    public boolean isSecondary() {
        return secondaryHeaderActivated;
    }
}
