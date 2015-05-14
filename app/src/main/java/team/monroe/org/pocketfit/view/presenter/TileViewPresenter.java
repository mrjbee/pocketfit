package team.monroe.org.pocketfit.view.presenter;

import android.animation.Animator;
import android.view.View;

import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;
import org.monroe.team.android.box.utils.DisplayUtils;

import team.monroe.org.pocketfit.R;

import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.*;

public class TileViewPresenter extends ViewPresenter {


    public TileViewPresenter(View rootView) {
        super(rootView);
    }

    public void showWithAnimation() {

        AppearanceController controller =  combine(

                animateAppearance(getRootView(), ySlide(0, DisplayUtils.screenHeight(getRootView().getResources())))
                        .showAnimation(duration_auto_fint(0.5f), interpreter_decelerate(0.3f))
                        .hideAndInvisible(),

                animateAppearance(getRootView(), alpha(1, 0))
                        .showAnimation(duration_constant(1000))
                        .hideAndInvisible());
        controller.hideWithoutAnimation();
        controller.show();
    }

}
