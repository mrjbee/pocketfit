package team.monroe.org.pocketfit;

import android.animation.Animator;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import org.monroe.team.android.box.app.ActivitySupport;
import org.monroe.team.android.box.app.ui.animation.AnimatorListenerSupport;
import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;
import org.monroe.team.android.box.utils.DisplayUtils;
import org.monroe.team.corebox.log.L;

import java.util.Date;

import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.*;

import team.monroe.org.pocketfit.fragments.DashboardPageFragment;
import team.monroe.org.pocketfit.fragments.PageWorkoutFragment;
import team.monroe.org.pocketfit.fragments.PopupMealListFragment;
import team.monroe.org.pocketfit.fragments.contract.BackButtonContract;
import team.monroe.org.pocketfit.fragments.contract.MainButtonUserContract;
import team.monroe.org.pocketfit.view.VerticalViewPager;

public class DashboardActivity extends ActivitySupport<PocketFitApp>{

    private MainButtonController mainButtonController;
    private PopupController popupController;

    private VerticalViewPager mViewPager;
    private team.monroe.org.pocketfit.view.FragmentPagerAdapter mPageAdapter;
    private Class<? extends  DashboardPageFragment> mCurrentPageClass = PageWorkoutFragment.class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_dashboard);

        mainButtonController = new MainButtonController(view(R.id.panel_main_button),view(R.id.image_main_button,ImageView.class));
        popupController = new PopupController(view(R.id.layer_shadow), view(R.id.frag_popup), this);


        view(R.id.main_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPage(MainButtonUserContract.class).onMainButton();
            }
        });

        popupController.restoreState(savedInstanceState);
        if (isFirstRun()){
        }else {
            mainButtonController.restoreState(savedInstanceState);
            mCurrentPageClass = (Class<? extends DashboardPageFragment>) savedInstanceState.getSerializable("current_page");
        }

        mViewPager = view(R.id.view_pager, VerticalViewPager.class);
        mPageAdapter = new team.monroe.org.pocketfit.view.FragmentPagerAdapter(getFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return createWorkoutPage();
                    case 1:
                        return new PageFoodFragment();
                    case 2:
                        return new PageHistoryFragment();
                    default:
                        throw new IllegalStateException();
                }
            }

            private PageWorkoutFragment createWorkoutPage() {
                PageWorkoutFragment.TransformationState state;
                if (application().isTrainingRunning()) {
                    state = PageWorkoutFragment.TransformationState.PROGRESS;
                } else if (application().hasActiveRoutine()) {
                    state = PageWorkoutFragment.TransformationState.ABOUT;
                } else {
                    state = PageWorkoutFragment.TransformationState.NOT_SET;
                }
                PageWorkoutFragment workoutFragment = new PageWorkoutFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("state", state.ordinal());
                workoutFragment.setArguments(bundle);
                return workoutFragment;
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private boolean mScrollHandlingEnabled = false;
            public boolean mDragging = false;
            public boolean mPageMotionDirectionUp = false;
            public int mCurrentPage;
            public int mTopPage;
            public int mBottomPage;

            @Override
            public void onPageScrolled(int pos, float positionOffset, int positionOffsetPixels) {

                if (!mScrollHandlingEnabled) return;

                if (mDragging) {
                    mTopPage = pos;
                    mBottomPage = pos + 1;
                    mCurrentPage = mViewPager.getCurrentItem();
                    mPageMotionDirectionUp = mCurrentPage == mTopPage;
                }

                int bottomPagePosition = (mViewPager.getHeight() - positionOffsetPixels);
                int topPagePosition = - positionOffsetPixels;
                if (!mDragging & mPageMotionDirectionUp & pos != mTopPage){
                    int cup = topPagePosition;
                    topPagePosition = -bottomPagePosition;
                    bottomPagePosition = -cup;
                }

                L.DEBUG.d("DASH SCROLL [up = "+mPageMotionDirectionUp+"]"+" top :" + mTopPage + "; bottom :" + mBottomPage + "; current :" + mCurrentPage);
                L.DEBUG.d("DASH SCROLL position [top = "+topPagePosition+" x bottom = "+bottomPagePosition+"]");

                if (mPageMotionDirectionUp){
                    if (mTopPage > -1) {
                        getPage(DashboardPageFragment.class, mTopPage).onPageMoveToHide(topPagePosition, mPageMotionDirectionUp);
                    }
                    if (mBottomPage != mPageAdapter.getCount()){
                        if (getPage(DashboardPageFragment.class, mBottomPage) != null) {
                            getPage(DashboardPageFragment.class, mBottomPage).onPageMoveToShow(bottomPagePosition, mPageMotionDirectionUp);
                        }
                    }
                } else {
                    if (mTopPage > -1) {
                        getPage(DashboardPageFragment.class, mTopPage).onPageMoveToShow(topPagePosition, mPageMotionDirectionUp);
                    }
                   if (mBottomPage != mPageAdapter.getCount()) {
                        if ( getPage(DashboardPageFragment.class, mBottomPage) != null) {
                            getPage(DashboardPageFragment.class, mBottomPage).onPageMoveToHide(bottomPagePosition, mPageMotionDirectionUp);
                        }
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                L.DEBUG.d("Dashboard Page SELECTED");
                DashboardPageFragment pageFragment = getPage(DashboardPageFragment.class);
                mCurrentPageClass = pageFragment.getClass();
                pageFragment.updateMainButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state){
                    case VerticalViewPager.SCROLL_STATE_DRAGGING:
                        L.DEBUG.d("Dashboard Page DRAGGING");
                        mScrollHandlingEnabled = true;
                        mDragging = true;
                        return;
                    case VerticalViewPager.SCROLL_STATE_SETTLING:
                        mDragging = false;
                        L.DEBUG.d("Dashboard Page SETTLING");
                        return;
                    case VerticalViewPager.SCROLL_STATE_IDLE:
                        L.DEBUG.d("Dashboard Page IDLE");
                        return;
                }
            }
        });
        mViewPager.setAdapter(mPageAdapter);
    }


    private <ContractType>  ContractType getPage(Class<ContractType> contract) {
        int pageIndex = mViewPager.getCurrentItem();
        return getPage(contract, pageIndex);
    }


    private <ContractType>  ContractType getPage(Class<ContractType> contract, int pageIndex) {
        String pageTag = "android:switcher:" + mViewPager.getId() + ":" + pageIndex;
        return (ContractType)getFragmentManager().findFragmentByTag(pageTag);
    }

    public void openExerciseEditor() {
        startActivity(new Intent(this, ExercisesActivity.class));
    }


    public void openProductList() {
        startActivity(new Intent(DashboardActivity.this, ProductsFoodActivity.class));
    }

    public void openRoutinesEditor() {
        startActivityForResult(new Intent(DashboardActivity.this, RoutinesActivity.class), 40);
    }

    public void openMealsSelect() {
        startActivity(new Intent(DashboardActivity.this, FoodActivity.class));
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

    public void showMainButton(Class<? extends DashboardPageFragment> pageClass, int resource, Runnable action) {
        if (pageClass != mCurrentPageClass)return;
        mainButtonController.show(resource, action);
    }

    public void hideMainButton(Class<? extends DashboardPageFragment> pageClass, Runnable actionOnHide) {
        if (pageClass != mCurrentPageClass)return;
        mainButtonController.hide(actionOnHide);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        popupController.saveState(outState);
        mainButtonController.saveState(outState);
        outState.putSerializable("current_page",mCurrentPageClass);
    }

    @Override
    public void onBackPressed() {
        if (popupController.onBackPressed(getFragmentManager())){
            return;
        }
        if (!getPage(BackButtonContract.class).onBackButton()){
            super.onBackPressed();
        }
    }

    public void openPopupMealList(Date date) {
        Fragment fragment = new PopupMealListFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("date_time", date.getTime());
        fragment.setArguments(bundle);
        popupController.showPopup(getFragmentManager(), fragment);
    }

    public void closePopup() {
        popupController.hidePopup(getFragmentManager());
    }



    private static class PopupController {

        private final View shadowLayer;
        private final View fragLayer;
        private final AppearanceController mShadowAc;
        private final AppearanceController mFragAc;
        private boolean mPopupVisible = false;


        private PopupController(View shadowLayer, View fragLayer, Context context) {

            this.shadowLayer = shadowLayer;
            this.fragLayer = fragLayer;

            fragLayer.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

            mShadowAc = animateAppearance(shadowLayer, alpha(0.7f,0f))
                    .showAnimation(duration_constant(300), interpreter_overshot())
                    .hideAnimation(duration_constant(300), interpreter_accelerate(0.8f))
                    .hideAndGone()
                    .build();

            mFragAc = animateAppearance(fragLayer, ySlide(0f, DisplayUtils.screenHeight(context.getResources())))
                    .showAnimation(duration_constant(500), interpreter_decelerate(0.8f))
                    .hideAnimation(duration_constant(300), interpreter_accelerate(0.8f))
                    .hideAndGone()
                    .build();
        }

        public void restoreState(Bundle savedInstanceState) {
            if (savedInstanceState != null){
                mPopupVisible = savedInstanceState.getBoolean("popup_visibility",false);
            }else {
                mPopupVisible = false;
            }
            if(mPopupVisible){
                mFragAc.showWithoutAnimation();
                mShadowAc.showWithoutAnimation();
            }else{
                mFragAc.hideWithoutAnimation();
                mShadowAc.hideWithoutAnimation();
            }
        }

        public void saveState(Bundle outState) {
            outState.putBoolean("popup_visibility", mPopupVisible);
        }

        public void showPopup(FragmentManager fragmentManager, Fragment fragment) {
            fragmentManager.beginTransaction().add(R.id.frag_popup,fragment).commit();
            mPopupVisible = true;
            mShadowAc.show();
            mFragAc.show();
        }

        private void hidePopup(final FragmentManager manager) {
            if (!mPopupVisible) return;
            mShadowAc.hide();
            mFragAc.hideAndCustomize(new AppearanceController.AnimatorCustomization() {
                @Override
                public void customize(Animator animator) {
                    animator.addListener(new AnimatorListenerSupport(){
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            manager.beginTransaction().remove(manager.findFragmentById(R.id.frag_popup)).commit();
                            mPopupVisible = false;
                        }
                    });
                }
            });
        }

        public boolean onBackPressed(FragmentManager manager) {
            if (mPopupVisible){
                hidePopup(manager);
                return true;
            }
            return false;
        }


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
