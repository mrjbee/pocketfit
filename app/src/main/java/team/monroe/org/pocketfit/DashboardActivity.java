package team.monroe.org.pocketfit;

import android.animation.Animator;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.widget.ImageView;

import org.monroe.team.android.box.app.ui.animation.AnimatorListenerSupport;
import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;
import org.monroe.team.android.box.utils.DisplayUtils;

import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.*;
import team.monroe.org.pocketfit.fragments.TileWorkoutFragment;
import team.monroe.org.pocketfit.fragments.contract.BackButtonContract;
import team.monroe.org.pocketfit.fragments.contract.MainButtonOwnerContract;
import team.monroe.org.pocketfit.fragments.contract.MainButtonUserContract;
import team.monroe.org.pocketfit.view.VerticalViewPager;

public class DashboardActivity extends FragmentActivity implements MainButtonOwnerContract {

    private AppearanceController startupTileAC;
    private AppearanceController backgroundStripeAC;
    private MainButtonController mainButtonController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainButtonController = new MainButtonController(view(R.id.panel_main_button),view(R.id.image_main_button,ImageView.class));

        startupTileAC = animateAppearance(view(R.id.fragment_container_body), ySlide(0, DisplayUtils.screenHeight(getResources())))
                .showAnimation(duration_constant(300), interpreter_decelerate(0.8f))
                .build();

        backgroundStripeAC = animateAppearance(view(R.id.background_stripe), ySlide(0, DisplayUtils.screenHeight(getResources())/2))
                .showAnimation(duration_constant(400), interpreter_accelerate(0.5f))
                .build();


        view(R.id.main_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBodyFragment(MainButtonUserContract.class).onMainButton();
            }
        });

        if (isFirstRun()){
            mainButtonController.blockAppearance();
            backgroundStripeAC.hideWithoutAnimation();
            startupTileAC.hideWithoutAnimation();
            backgroundStripeAC.showAndCustomize(new AppearanceController.AnimatorCustomization() {
                @Override
                public void customize(Animator animator) {
                    animator.setStartDelay(200);
                    animator.addListener(new AnimatorListenerSupport(){
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            startupTileAC.showAndCustomize(new AppearanceController.AnimatorCustomization() {
                                @Override
                                public void customize(Animator animator) {
                                    animator.addListener(new AnimatorListenerSupport(){
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            runLastOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mainButtonController.applyAppearance();
                                                }
                                            }, 500);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }else {
            startupTileAC.showWithoutAnimation();
            backgroundStripeAC.showWithoutAnimation();
            mainButtonController.restoreState(savedInstanceState);
        }

        VerticalViewPager viewPager = view(R.id.view_pager, VerticalViewPager.class);
        viewPager.setAdapter(new team.monroe.org.pocketfit.view.FragmentPagerAdapter(getFragmentManager()) {


            @Override
            public Fragment getItem(int position) {
                return null;
            }

            @Override
            public int getCount() {
                return 0;
            }
        });
    }

    @Override
    protected FragmentItem customize_startupFragment() {
        FragmentItem fragmentItem;
        if (application().isTrainingRunning()){
            fragmentItem = new FragmentItem(TileWorkoutFragment.class)
                    .addArgument("state", TileWorkoutFragment.TransformationState.PROGRESS.ordinal());
        }else {

            fragmentItem =
                    new FragmentItem(TileWorkoutFragment.class)
                            .addArgument("state",
                                    application().hasActiveRoutine() ?
                                            TileWorkoutFragment.TransformationState.ABOUT.ordinal():
                                            TileWorkoutFragment.TransformationState.NOT_SET.ordinal());
        }
        return fragmentItem;
    }

    @Override
    protected int customize_rootLayout() {
        return R.layout.activity_dashboard;
    }

    public void openExerciseEditor() {
        startActivity(new Intent(this, ExercisesActivity.class));
    }

    public void openRoutinesEditor() {
        startActivityForResult(new Intent(DashboardActivity.this, RoutinesActivity.class), 40);
    }


    public void openDayEditor(String routineId, String routineDayId) {
        Intent intent = new Intent(DashboardActivity.this, RoutineDayEditActivity.class);
        intent.putExtra("routine_id",routineId);
        intent.putExtra("day_id",routineDayId);
        startActivity(intent);
    }

    public void openRoutineEditor(final String routineId) {
        Intent intent = new Intent(DashboardActivity.this, RoutineEditActivity.class);
        intent.putExtra("routine_id",routineId);
        startActivityForResult(intent, 40);
    }


    public void openTrainingRunner() {
        Intent intent = new Intent(DashboardActivity.this, TrainingActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 40){
            //TODO: Required unless animation ends
            mainButtonController.blockAppearance();
            runLastOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainButtonController.applyAppearance();
                }
            }, 1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void showMainButton(int resource, Runnable action) {
        mainButtonController.show(resource, action);
    }

    @Override
    public void hideMainButton(Runnable actionOnHide) {
        mainButtonController.hide(actionOnHide);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mainButtonController.saveState(outState);
    }

    public void removeTopFromHistory() {
       FragmentItem item = super.dropTopBackStack();
    }

    @Override
    public void onBackPressed() {
        if (!getBodyFragment(BackButtonContract.class).onBackButton()){
            super.onBackPressed();
        }
    }

    public void updateWorkoutTileState(TileWorkoutFragment.TransformationState state) {
        if (fragmentBackStackTop().fragmentClass != TileWorkoutFragment.class) throw new IllegalStateException("Not a workout tile");
        fragmentBackStackTop().addArgument("state", state.ordinal());
    }

    private static class MainButtonController {

        private final View mMainButtonPanel;
        private final ImageView mMainButtonImage;
        private int mImageButtonResource = 0;
        private final AppearanceController mMainButtonAC;
        private boolean mMainButtonVisible = false;
        private boolean mLock = false;
        private Request mPendingRequest = null;

        private MainButtonController(View mMainButtonPanel, ImageView mMainButtonImage) {
            this.mMainButtonPanel = mMainButtonPanel;
            this.mMainButtonImage = mMainButtonImage;
            mMainButtonAC = animateAppearance(mMainButtonPanel, scale(1f,0f))
                    .showAnimation(duration_constant(500), interpreter_overshot())
                    .hideAnimation(duration_constant(300), interpreter_accelerate(null))
                    .hideAndGone()
                    .build();
            mMainButtonAC.hideWithoutAnimation();
        }

        public void restoreState(Bundle savedInstanceState) {
            int resource = savedInstanceState.getInt("main_btn_res", 0);
            if (savedInstanceState.getBoolean("main_btn_visibility", false)){
                mMainButtonImage.setImageResource(resource);
                mMainButtonAC.showWithoutAnimation();
                mMainButtonVisible = true;
            }else{
                mMainButtonVisible = false;
                mMainButtonAC.hideWithoutAnimation();
            }
        }

        public void saveState(Bundle outState) {
            outState.putBoolean("main_btn_visibility", mMainButtonVisible);
            outState.putInt("main_btn_res", mImageButtonResource);
        }

        public void show(int resource, Runnable action) {
            final Request originalRequest = new Request(false, resource, action);
            if (mMainButtonVisible && resource != mImageButtonResource){
                executeRequest(new Request(true, mImageButtonResource, new Runnable() {
                    @Override
                    public void run() {
                        executeRequest(originalRequest);
                    }
                }));
            }else{
                Request request = new Request(false, resource, action);
                executeRequest(originalRequest);
            }
        }

        public void hide(Runnable actionOnHide) {
            Request request = new Request(true, mImageButtonResource, actionOnHide);
            executeRequest(request);
        }

        private synchronized void executeRequest(Request request) {
            //Save int here as rotation may corrupt state
            mImageButtonResource = request.r;
            mMainButtonVisible = !request.closeRequest;

            if (mLock){
                mPendingRequest = request;
            } else {
                applyDirectAppearance(request);
            }
        }

        public synchronized void blockAppearance() {
            mLock = true;
        }

        public synchronized void applyAppearance() {
            mLock = false;
            if (mPendingRequest != null){
                applyDirectAppearance(mPendingRequest);
            }
        }

        private void applyDirectAppearance(final Request request) {
            mPendingRequest = null;
            mImageButtonResource = request.r;
            mMainButtonVisible = !request.closeRequest;
            mMainButtonImage.setImageResource(mImageButtonResource);
            if (!request.closeRequest){
                mMainButtonAC.showAndCustomize(new AppearanceController.AnimatorCustomization() {
                    @Override
                    public void customize(Animator animator) {
                        animator.addListener(new AnimatorListenerSupport(){
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (request.action != null){
                                    request.action.run();
                                }
                            }
                        });
                    }
                });
            }else{
                mMainButtonAC.hideAndCustomize(new AppearanceController.AnimatorCustomization() {
                    @Override
                    public void customize(Animator animator) {
                        animator.addListener(new AnimatorListenerSupport() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (request.action != null) {
                                    request.action.run();
                                }
                            }
                        });
                    }
                });
            }
        }

        private final class Request{
            private final boolean closeRequest;
            private final int r;
            private final Runnable action;

            private Request(boolean closeRequest, int r, Runnable action) {
                this.closeRequest = closeRequest;
                this.r = r;
                this.action = action;
            }
        }

    }

}
