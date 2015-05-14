package team.monroe.org.pocketfit.view.presenter;

import android.animation.Animator;
import android.view.View;

import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;

import team.monroe.org.pocketfit.R;

import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.*;

public class TileNoRoutineViewPresenter extends TileViewPresenter {

    public TileNoRoutineViewPresenter(View rootView) {
        super(rootView);
    }

    @Override
    public void showWithAnimation() {
        super.showWithAnimation();
        AppearanceController expandBtnController = animateAppearance(find(R.id.expand_arrow_btn), scale(1,0))
                .showAnimation(duration_constant(200), interpreter_overshot())
                .hideAndGone()
                .build();
        expandBtnController.hideWithoutAnimation();
        expandBtnController.showAndCustomize(new AppearanceController.AnimatorCustomization() {
            @Override
            public void customize(Animator animator) {
                animator.setStartDelay(1000);
            }
        });
    }


    public void onExpandListener(View.OnClickListener listener){
        find(R.id.expand_arrow_btn).setOnClickListener(listener);
    }
}
