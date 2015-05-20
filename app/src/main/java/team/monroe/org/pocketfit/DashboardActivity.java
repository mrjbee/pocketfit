package team.monroe.org.pocketfit;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import org.monroe.team.android.box.app.ui.animation.AnimatorListenerSupport;
import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;
import org.monroe.team.android.box.utils.DisplayUtils;

import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.*;

import team.monroe.org.pocketfit.fragments.TileNoRoutineFragment;
import team.monroe.org.pocketfit.fragments.TileRoutineFragment;
import team.monroe.org.pocketfit.fragments.contract.MainButtonUserContract;

public class DashboardActivity extends FragmentActivity {


    private AppearanceController startupTileAC;
    private AppearanceController mainButtonAC;
    private AppearanceController backgroundStripeAC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startupTileAC = animateAppearance(view(R.id.fragment_container_body), ySlide(0, DisplayUtils.screenHeight(getResources())))
                .showAnimation(duration_constant(300), interpreter_decelerate(0.8f))
                .build();

        backgroundStripeAC = animateAppearance(view(R.id.background_stripe), ySlide(0, DisplayUtils.screenHeight(getResources())/4))
                .showAnimation(duration_constant(300))
                .build();

        mainButtonAC = animateAppearance(view(R.id.panel_main_button), scale(1f,0f))
                .showAnimation(duration_constant(500), interpreter_overshot())
                .hideAnimation(duration_constant(300), interpreter_accelerate(null))
                .hideAndGone()
                .build();

        view(R.id.main_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBodyFragment(MainButtonUserContract.class).onMainButton();
            }
        });

        if (isFirstRun()){
            backgroundStripeAC.hideWithoutAnimation();
            mainButtonAC.hideWithoutAnimation();
            startupTileAC.hideWithoutAnimation();
            startupTileAC.showAndCustomize(new AppearanceController.AnimatorCustomization() {
                @Override
                public void customize(Animator animator) {
                    animator.setStartDelay(200);
                    animator.addListener(new AnimatorListenerSupport(){
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            backgroundStripeAC.show();
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            boolean required = isMainButtonRequested();
                            if (required) {
                                mainButtonAC.show();
                            }
                        }
                    });
                }
            });
        }else {
            startupTileAC.showWithoutAnimation();
            backgroundStripeAC.showWithoutAnimation();
            if (isMainButtonRequested()){
                mainButtonAC.showWithoutAnimation();
            }else {
                mainButtonAC.hideWithoutAnimation();
            }
        }
    }

    private boolean isMainButtonRequested() {
        boolean required = false;
        Integer imageID = getBodyFragment(MainButtonUserContract.class).icon_mainButton();
        if (imageID != null){
            view(R.id.image_main_button, ImageView.class).setImageResource(imageID);
            required = true;
        }
        return required;
    }

    @Override
    protected FragmentItem customize_startupFragment() {
        return new FragmentItem(application().hasActiveRoutine()? TileRoutineFragment.class : TileNoRoutineFragment.class);
    }

    @Override
    protected int customize_rootLayout() {
        return R.layout.activity_dashboard;
    }

    public void openExerciseEditor() {
        startActivity(new Intent(this, ExercisesActivity.class));
    }

    public void openRoutineEditor() {
        mainButtonAC.hideAndCustomize(new AppearanceController.AnimatorCustomization() {
            @Override
            public void customize(Animator animator) {
                animator.addListener(new AnimatorListenerSupport() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startActivityForResult(new Intent(DashboardActivity.this, RoutineSetupActivity.class), 40);
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 40){
            if (isMainButtonRequested()) {
                mainButtonAC.showAndCustomize(new AppearanceController.AnimatorCustomization() {
                    @Override
                    public void customize(Animator animator) {
                        animator.setStartDelay(400);
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void switchNoRoutineTile() {
        replaceBodyFragment(new FragmentItem(TileNoRoutineFragment.class), change_flip_in());
        runLastOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isMainButtonRequested()) {
                    mainButtonAC.show();
                }
            }
        },500);
    }

    public void switchRoutineTile() {
        replaceBodyFragment(new FragmentItem(TileRoutineFragment.class), change_flip_out());
        runLastOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isMainButtonRequested()) {
                    mainButtonAC.show();
                }
            }
        },500);
    }


}
