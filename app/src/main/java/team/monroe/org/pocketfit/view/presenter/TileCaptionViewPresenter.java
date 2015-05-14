package team.monroe.org.pocketfit.view.presenter;

import android.view.View;
import android.widget.TextView;

import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;
import org.monroe.team.android.box.utils.DisplayUtils;

import team.monroe.org.pocketfit.R;

import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.alpha;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.animateAppearance;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.combine;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.duration_auto_fint;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.duration_constant;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.interpreter_decelerate;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.ySlide;

public class TileCaptionViewPresenter extends TileViewPresenter{

    private final TextView captionView;

    public TileCaptionViewPresenter(View rootView) {
        super(rootView);
        captionView = find(R.id.text);
    }

    @Override
    public void showWithAnimation() {
        AppearanceController controller =  combine(
                animateAppearance(getRootView(), alpha(1, 0))
                        .showAnimation(duration_constant(1000))
                        .hideAndInvisible());
        controller.hideWithoutAnimation();
        controller.show();
    }

    public void setCaption(CharSequence value){
        captionView.setText(value);
    }
}
