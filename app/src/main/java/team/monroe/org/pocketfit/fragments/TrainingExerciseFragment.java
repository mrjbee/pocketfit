package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;
import org.monroe.team.android.box.app.ui.animation.apperrance.SceneDirector;
import org.monroe.team.android.box.utils.DisplayUtils;

import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.*;

import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.TrainingActivity;
import team.monroe.org.pocketfit.TrainingExecutionService;
import team.monroe.org.pocketfit.presentations.Exercise;
import team.monroe.org.pocketfit.presentations.RoutineExercise;
import team.monroe.org.pocketfit.view.presenter.ClockViewPresenter;

public class TrainingExerciseFragment extends BodyFragment<TrainingActivity> {

    private TrainingExecutionService.TrainingPlan mTrainingPlan;
    private RoutineExercise mRoutineExercise;
    private Exercise.Type mExerciseType;
    private State mState;
    private AppearanceController ac_stopButton;
    private AppearanceController ac_executionPanelHeight;
    private AppearanceController ac_executionPanelAlpha;
    private AppearanceController ac_actionPanel;
    private ClockViewPresenter mClockViewPresenter;

    @Override protected boolean isHeaderSecondary() {
        return false;
    }
    @Override protected String getHeaderName() {
        return null;
    }
    @Override protected int getLayoutId() {
        return R.layout.fragment_training_exercise;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTrainingPlan = application().getTrainingPlan();
        mRoutineExercise = mTrainingPlan.getCurrentExercise();

        ac_stopButton = animateAppearance(view(R.id.panel_main_button), scale(1,0))
                .showAnimation(duration_constant(400), interpreter_overshot())
                .hideAnimation(duration_constant(300), interpreter_accelerate(0.6f))
                .build();

        ac_executionPanelHeight = animateAppearance(view(R.id.panel_exercise_running), heightSlide((int) DisplayUtils.dpToPx(200, getResources()), 0))
                .showAnimation(duration_constant(500), interpreter_overshot())
                .hideAnimation(duration_constant(300), interpreter_accelerate(0.6f))
                .hideAndGone()
                .build();

        ac_executionPanelAlpha = animateAppearance(view(R.id.panel_exercise_running), alpha(1f, 0f))
                .showAnimation(duration_constant(300), interpreter_decelerate(0.8f))
                .hideAnimation(duration_constant(300), interpreter_accelerate(0.6f))
                .build();

        ac_actionPanel = animateAppearance(view(R.id.panel_action_button), heightSlide((int) DisplayUtils.dpToPx(50, getResources()), 0))
                .showAnimation(duration_constant(200), interpreter_decelerate(0.8f))
                .hideAnimation(duration_constant(200), interpreter_accelerate(0.6f))
                .hideAndGone()
                .build();

        fillUI_ExerciseDetails();
        calculateState();
        updateUI(false);
    }

    private void updateUI(boolean animate) {
        switch (mState){
            case START_AWAITING:
                fillUI_awaitingStart();
                updateUI_awaitingStart(animate);
                break;
            case STOP_AWAITING:
                fillUI_awaitingStop();
                updateUI_awaitingStop(animate);
                break;
            case FINISH_AWAITING:
            case RESULT_AWAITING:
            default:
                throw new IllegalStateException();
        }
    }

    private void updateUI_awaitingStop(boolean animate) {
        if (animate){
            SceneDirector.scenario()
                    .hide(ac_actionPanel)
                    .show(ac_executionPanelHeight)
                        .then()
                            .show(ac_executionPanelAlpha)
                        .then()
                            .show(ac_stopButton)
                    .play();
        }else{
            ac_executionPanelAlpha.showWithoutAnimation();
            ac_executionPanelHeight.showWithoutAnimation();
            ac_stopButton.showWithoutAnimation();
            ac_actionPanel.hideWithoutAnimation();
        }
    }


    private void updateUI_awaitingStart(boolean animate) {
        if (animate){
            SceneDirector.scenario()
                    .hide(ac_stopButton)
                    .then()
                        .hide(ac_executionPanelAlpha)
                        .then()
                            .hide(ac_executionPanelHeight)
                        .then()
                            .show(ac_actionPanel)
            .play();
        }else{
            ac_executionPanelAlpha.hideWithoutAnimation();
            ac_executionPanelHeight.hideWithoutAnimation();
            ac_stopButton.hideWithoutAnimation();
            ac_actionPanel.showWithoutAnimation();
        }
    }

    private void fillUI_awaitingStop() {
        view(R.id.stop_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: add stop action
            }
        });
        mClockViewPresenter = new ClockViewPresenter(view_text(R.id.text_exercise_execution_timer));
        mClockViewPresenter.startClock(mTrainingPlan.getSetStartDate());
    }

    private void fillUI_awaitingStart() {
        view_button(R.id.action_secondary).setVisibility(View.GONE);
        view_button(R.id.action).setText("Start Exercise");
        view_button(R.id.action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrainingPlan.addSet();
                mTrainingPlan.startSet();
                mState = State.STOP_AWAITING;
                updateUI(true);
            }
        });
    }

    private void calculateState() {
        if (!mTrainingPlan.isSetStarted()){
            mState = State.START_AWAITING;
        }else if (!mTrainingPlan.isSetDone()){
            mState = State.STOP_AWAITING;
        }else if (!mTrainingPlan.isAllSetsCommitted()){
            mState = State.RESULT_AWAITING;
        }else {
            mState = State.FINISH_AWAITING;
        }

        if (mState == null) {
            throw new IllegalStateException();
        }
    }

    private void fillUI_ExerciseDetails() {
        view_text(R.id.exercise_name).setText(mRoutineExercise.exercise.title);
        view_text(R.id.exercise_description).setText(mRoutineExercise.exercise.description);
        mExerciseType = mRoutineExercise.exercise.type;

        switch (mExerciseType){
            case weight_times:
                RoutineExercise.PowerExerciseDetails exerciseDetails = (RoutineExercise.PowerExerciseDetails) mRoutineExercise.exerciseDetails;
                addDetails("Weight",exerciseDetails.weight, "kg");
                addDetails("Times",exerciseDetails.times, "times");
                addDetails("Sets",exerciseDetails.sets, "sets");
                break;
            case time:
                RoutineExercise.TimeExerciseDetails timeDetails = (RoutineExercise.TimeExerciseDetails) mRoutineExercise.exerciseDetails;
                addDetails("Time",timeDetails.detailsString(), "");
                break;
            case distance:
                RoutineExercise.DistanceExerciseDetails distanceDetails = (RoutineExercise.DistanceExerciseDetails) mRoutineExercise.exerciseDetails;
                addDetails("Distance", distanceDetails.detailsString(), "");
                break;
            default:
                throw new IllegalStateException();
        }
    }

    private void addDetails(String caption, Object value, String measure) {
        ViewGroup viewPanel =view(R.id.panel_exercise_details, ViewGroup.class);
        View view = activity().getLayoutInflater().inflate(R.layout.panel_3_column_details,viewPanel,false);
        ((TextView)view.findViewById(R.id.item_caption)).setText(caption);
        ((TextView)view.findViewById(R.id.item_value)).setText(value.toString());
        ((TextView)view.findViewById(R.id.item_measure)).setText(measure);
        viewPanel.addView(view);
    }

    private enum State {
        START_AWAITING, STOP_AWAITING, RESULT_AWAITING, FINISH_AWAITING
    }
}
