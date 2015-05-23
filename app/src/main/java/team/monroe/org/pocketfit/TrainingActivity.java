package team.monroe.org.pocketfit;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;

import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.*;

import java.util.Date;

import team.monroe.org.pocketfit.fragments.BodyFragment;
import team.monroe.org.pocketfit.fragments.TrainingEndFragment;
import team.monroe.org.pocketfit.fragments.TrainingTileExerciseFragment;
import team.monroe.org.pocketfit.fragments.TrainingTileLoadingRoutineExerciseFragment;
import team.monroe.org.pocketfit.fragments.TrainingTilePowerAllResultFragment;
import team.monroe.org.pocketfit.fragments.TrainingTilePowerExecuteFragment;
import team.monroe.org.pocketfit.fragments.TrainingTilePowerResultFragment;
import team.monroe.org.pocketfit.presentations.Exercise;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.view.presenter.ClockViewPresenter;

import static team.monroe.org.pocketfit.TrainingExecutionService.TrainingPlan.NoOpTrainingPlanListener;

public class TrainingActivity extends FragmentActivity{

    private ClockViewPresenter mTrainingDurationClockPresenter;
    private ClockViewPresenter mTrainingPauseClockPresenter;
    private AppearanceController mPauseClockAnimator;
    private AppearanceController mTrainingClockAnimator;

    @Override
    protected FragmentItem customize_startupFragment() {
        currentFragment =calculateCurrentFragment();
        return new FragmentItem(currentFragment);
    }


    @Override
    protected int customize_rootLayout() {
        return R.layout.activity_fragment_training_execution;
    }

    @Override
    public void header(String headerText, boolean secondary) {}

    @Override
    public void animateHeader(String headerText, boolean secondary) {}

    @Override
    public View buildHeaderActionsView(ViewGroup actionPanel) {
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Routine mRoutine = application().getTrainingRoutine();
        view_text(R.id.text_routine_name).setText(mRoutine.title);
        mTrainingDurationClockPresenter = new ClockViewPresenter(view_text(R.id.text_clock));
        mTrainingPauseClockPresenter = new ClockViewPresenter(view_text(R.id.text_pause_clock));
        mPauseClockAnimator = animateAppearance(view(R.id.text_pause_clock), scale(1f,0f))
                .showAnimation(duration_constant(300),interpreter_overshot())
                .hideAndGone()
                .hideAnimation(duration_constant(200), interpreter_decelerate(0.5f))
                .build();

        mTrainingClockAnimator = animateAppearance(view(R.id.text_clock), scale(1f,0f))
                .showAnimation(duration_constant(300),interpreter_overshot())
                .hideAndGone()
                .hideAnimation(duration_constant(200), interpreter_decelerate(0.5f))
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateClock(false);
        application().getTrainingPlan().setTrainingPlanListener(new NoOpTrainingPlanListener(){
            @Override
            public void onStartDateChanged(Date startDate) {
                updateClock(true);
            }

            @Override
            public void onStartPauseDateChanged(Date pauseStartDate) {
                updateClock(true);
            }
        });
    }


    private void updateClock(boolean animate) {
        if (application().getTrainingPlan().isStarted()){
            mTrainingDurationClockPresenter.startClock(application().getTrainingPlan().getStartDate());
        }else {
            mTrainingDurationClockPresenter.resetClock();
        }

        if (application().getTrainingPlan().isPaused()){
            if (animate) {
                mPauseClockAnimator.show();
            }else {
                mPauseClockAnimator.showWithoutAnimation();
            }
            mTrainingPauseClockPresenter.startClock(application().getTrainingPlan().getPauseStartDate());
        }else {
            if (animate) {
                mPauseClockAnimator.hide();
            }else {
                mPauseClockAnimator.hideWithoutAnimation();
            }
            mTrainingPauseClockPresenter.resetClock();
        }

        if (application().getTrainingPlan().hasMoreExercises()){
            if (animate) {
                mTrainingClockAnimator.show();
                mPauseClockAnimator.show();
            }else {
                mTrainingClockAnimator.showWithoutAnimation();
                mPauseClockAnimator.showWithoutAnimation();
            }
        } else {
            if (animate) {
                mTrainingClockAnimator.hide();
                mPauseClockAnimator.hide();
            }else {
                mTrainingClockAnimator.hideWithoutAnimation();
                mPauseClockAnimator.hideWithoutAnimation();
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mTrainingDurationClockPresenter.resetClock();
        if (application().getTrainingPlan() != null) {
            application().getTrainingPlan().setTrainingPlanListener(null);
        }
    }

    private Class<? extends BodyFragment> calculateCurrentFragment() {
        TrainingExecutionService.TrainingPlan trainingPlan = application().getTrainingPlan();

        if (!application().getTrainingPlan().hasMoreExercises()){
            return TrainingEndFragment.class;
        }

        Exercise.Type exerciseType = trainingPlan.getCurrentExercise().exercise.type;

        if (!trainingPlan.isExerciseStarted()){
            //show exercise fragment
            return TrainingTileExerciseFragment.class;
        }else{
            if (trainingPlan.isSetStarted() && trainingPlan.isSetDone()){
                //set done, show set results
                switch (exerciseType){
                    case weight_times:
                        if (trainingPlan.isAllSetsCommitted()){
                            return TrainingTilePowerAllResultFragment.class;
                        }else {
                            return TrainingTilePowerResultFragment.class;
                        }
                }
            }else {
                //show set execution
                switch (exerciseType){
                    case weight_times:
                        return TrainingTilePowerExecuteFragment.class;
                }
            }
        }
        return TrainingTileLoadingRoutineExerciseFragment.class;
    }

    //TODO: add back stack top
    private Class currentFragment;

    //TrainingTileExerciseFragment
    //TrainingTilePowerAllResultFragment
    //TrainingTilePowerExecuteFragment
    //TrainingTilePowerResultFragment
    //TrainingEndFragment
    public void updateTile() {
        Class<? extends BodyFragment> nextTileFragment = calculateCurrentFragment();
        BodyFragmentAnimationRequest animationRequest = animation_slide_out_from_right();
        if (    nextTileFragment != TrainingTileExerciseFragment.class
                && currentFragment != TrainingTileExerciseFragment.class
                && nextTileFragment != TrainingTilePowerAllResultFragment.class
                && nextTileFragment != TrainingEndFragment.class){

            if (currentFragment == TrainingTilePowerAllResultFragment.class){
                animationRequest = animation_slide_from_left();
            } else if (nextTileFragment == TrainingTilePowerExecuteFragment.class){
                animationRequest = animation_flip_out();
            } else if (nextTileFragment == TrainingTilePowerResultFragment.class){
                animationRequest = animation_flip_in();
            }
        }
        currentFragment = nextTileFragment;
        replaceBodyFragment(new FragmentItem(nextTileFragment), animationRequest);
        updateClock(true);
    }

    public void stopTraining() {
        application().getTrainingPlan().setTrainingPlanListener(null);
        application().stopTraining();
        finish();
    }
}
