package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;
import org.monroe.team.android.box.app.ui.animation.apperrance.SceneDirector;
import org.monroe.team.android.box.utils.DisplayUtils;
import org.monroe.team.corebox.log.L;

import java.util.List;

import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.*;

import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.TrainingActivity;
import team.monroe.org.pocketfit.TrainingExecutionService;
import team.monroe.org.pocketfit.presentations.Exercise;
import team.monroe.org.pocketfit.presentations.RoutineExercise;
import team.monroe.org.pocketfit.view.presenter.ClockViewPresenter;
import team.monroe.org.pocketfit.view.presenter.DowntimeClockViewPresenter;
import team.monroe.org.pocketfit.view.presenter.ExerciseResultEditPresenter;

public class TrainingExerciseFragment extends BodyFragment<TrainingActivity> {

    private TrainingExecutionService.TrainingPlan mTrainingPlan;
    private RoutineExercise mRoutineExercise;
    private Exercise.Type mExerciseType;
    private State mState;
    private AppearanceController ac_stopButton;
    private AppearanceController ac_executionPanelHeight;
    private AppearanceController ac_executionPanelAlpha;
    private AppearanceController ac_actionPanel;
    private AppearanceController ac_executionPanelMove;
    private AppearanceController ac_editPanelMove;

    private ClockViewPresenter mClockViewPresenter;
    private DowntimeClockViewPresenter mDowntimeClockViewPresenter;
    private ExerciseResultEditPresenter mResultEditPresenter;


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
        mResultEditPresenter = new ExerciseResultEditPresenter((ViewGroup) view(R.id.panel_exercise_edit_result));
        mTrainingPlan = application().getTrainingPlan();
        mRoutineExercise = mTrainingPlan.getCurrentExercise();
        view(R.id.scroll, ScrollView.class).getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                int scrollY =   view(R.id.scroll, ScrollView.class).getScrollY();
                if (owner() !=null) {
                    owner().onBodyScroll(scrollY);
                }
            }
        });
        updateHeaderSpace(owner().getHeaderHeight());
        owner().mHeaderChangeListener = new TrainingActivity.HeaderChangeListener() {
            @Override
            public void onHeightChange(int newHeight) {
                updateHeaderSpace(newHeight);
            }
        };
        //

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

        ac_executionPanelMove =  animateAppearance(view(R.id.panel_exercise_stop), xSlide(0f, -DisplayUtils.screenWidth(getResources())))
                    .showAnimation(duration_constant(300), interpreter_decelerate(0.8f))
                    .hideAnimation(duration_constant(300), interpreter_accelerate(0.6f))
                    .hideAndInvisible().build();


        ac_editPanelMove =  animateAppearance(view(R.id.panel_exercise_edit_result), xSlide(0f, DisplayUtils.screenWidth(getResources())))
                        .showAnimation(duration_constant(300), interpreter_decelerate(0.8f))
                        .hideAnimation(duration_constant(300), interpreter_accelerate(0.6f))
                        .hideAndGone().build();


        ac_actionPanel = animateAppearance(view(R.id.panel_action_button), heightSlide((int) DisplayUtils.dpToPx(50, getResources()), 0))
                .showAnimation(duration_constant(200), interpreter_decelerate(0.8f))
                .hideAnimation(duration_constant(200), interpreter_accelerate(0.6f))
                .hideAndGone()
                .build();

        fillUI_ExerciseDetails();
        fillUI_SetsDetails();
        calculateState();
        updateUI(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        owner().mHeaderChangeListener = null;
    }

    private void updateHeaderSpace(int newHeight) {
        int height =  (int) (newHeight + DisplayUtils.dpToPx(20, getResources()));
        height = Math.max(height, (int) DisplayUtils.dpToPx(80, getResources()));
        view(R.id.panel_top_wrap).getLayoutParams().height = height;
        view(R.id.panel_top_wrap).requestLayout();
    }

    private void fillUI_SetsDetails() {
        view(R.id.panel_sets_details, ViewGroup.class).removeAllViews();
        List<TrainingExecutionService.TrainingPlan.ExerciseResult> resultList =  mTrainingPlan.getExerciseResultList();
        for (TrainingExecutionService.TrainingPlan.ExerciseResult exerciseResult : resultList) {
            addSetDetails(exerciseResult.asExerciseDetails());
        }

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
            case RESULT_AWAITING:
                fillUI_awaitingResult();
                updateUI_awaitingResult(animate);
                break;
            case FINISH_AWAITING:
                fillUI_awaitingFinish();
                updateUI_awaitingFinish(animate);
                break;
            default:
                throw new IllegalStateException();
        }
    }

    private void updateUI_awaitingFinish(boolean animate) {
        if (animate){
            SceneDirector.scenario()
                    .hide(ac_stopButton)
                    .then()
                        .hide(ac_editPanelMove)
                        .show(ac_executionPanelMove)
                        .hide(ac_executionPanelAlpha)
                        .then()
                            .hide(ac_executionPanelHeight)
                        .then()
                            .show(ac_actionPanel)
                    .play();
        }else{
            ac_editPanelMove.hideWithoutAnimation();
            ac_executionPanelMove.showWithoutAnimation();
            ac_executionPanelAlpha.hideWithoutAnimation();
            ac_executionPanelHeight.hideWithoutAnimation();
            ac_stopButton.hideWithoutAnimation();
            ac_actionPanel.showWithoutAnimation();
        }
    }

    private void updateUI_awaitingResult(boolean animate) {
        if (animate){
            SceneDirector.scenario()
                            .hide(ac_stopButton)
                            .then()
                                .hide(ac_executionPanelMove)
                                .show(ac_editPanelMove)
                                .show(ac_executionPanelAlpha, ac_executionPanelHeight)
                            .then()
                                .show(ac_actionPanel)
                    .play();
        }else{
            ac_editPanelMove.showWithoutAnimation();
            ac_executionPanelMove.hideWithoutAnimation();
            ac_executionPanelAlpha.showWithoutAnimation();
            ac_executionPanelHeight.showWithoutAnimation();
            ac_stopButton.hideWithoutAnimation();
            ac_actionPanel.showWithoutAnimation();
        }
    }

    private void updateUI_awaitingStop(boolean animate) {
        if (animate){
            SceneDirector.scenario()
                    .hide(ac_actionPanel)
                    .show(ac_executionPanelHeight)
                        .then()
                            .show(ac_executionPanelAlpha)
                            .show(ac_executionPanelMove)
                            .hide(ac_editPanelMove)
                        .then()
                            .show(ac_stopButton)
                    .play();
        }else{
            ac_editPanelMove.hideWithoutAnimation();
            ac_executionPanelMove.showWithoutAnimation();
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
                        .hide(ac_editPanelMove)
                        .show(ac_executionPanelMove)
                        .hide(ac_executionPanelAlpha)
                        .then()
                            .hide(ac_executionPanelHeight)
                        .then()
                            .show(ac_actionPanel)
            .play();
        }else{
            ac_editPanelMove.hideWithoutAnimation();
            ac_executionPanelMove.showWithoutAnimation();
            ac_executionPanelAlpha.hideWithoutAnimation();
            ac_executionPanelHeight.hideWithoutAnimation();
            ac_stopButton.hideWithoutAnimation();
            ac_actionPanel.showWithoutAnimation();
        }
    }

    private void fillUI_awaitingFinish() {

        if (mExerciseType == Exercise.Type.weight_times) {
            view_button(R.id.action_secondary).setText("Extra Set");
            view_button(R.id.action_secondary).setVisibility(View.VISIBLE);
        }else {
            view_button(R.id.action_secondary).setVisibility(View.GONE);
        }

        view_button(R.id.action_secondary).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrainingPlan.addSet();
                mTrainingPlan.startSet();
                mState = State.STOP_AWAITING;
                updateUI(true);
            }
        });

        view_button(R.id.action).setText("Go Next");
        view_button(R.id.action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrainingPlan.nextExercise();
                owner().updateTile();
            }
        });
    }

    private void fillUI_awaitingResult() {
        RoutineExercise.ExerciseDetails details;
        switch (mExerciseType){
            case weight_times:
                details = new RoutineExercise.PowerExerciseDetails();
                ((RoutineExercise.PowerExerciseDetails)details).sets = -1;
                ((RoutineExercise.PowerExerciseDetails)details).times = ((RoutineExercise.PowerExerciseDetails)mRoutineExercise.exerciseDetails).times;
                ((RoutineExercise.PowerExerciseDetails)details).weight =  ((RoutineExercise.PowerExerciseDetails)mRoutineExercise.exerciseDetails).weight;
                break;
            case distance:
                details = new RoutineExercise.DistanceExerciseDetails();
                ((RoutineExercise.DistanceExerciseDetails)details).distance = ((RoutineExercise.DistanceExerciseDetails)mRoutineExercise.exerciseDetails).distance;
                break;
            case time:
                details = new RoutineExercise.TimeExerciseDetails();
                ((RoutineExercise.TimeExerciseDetails)details).time = (float)((double)mTrainingPlan.getSetDuration() / (60d*1000d));
                break;
            default:
                throw new IllegalStateException();
        }

        mResultEditPresenter.setup(details);

        view_button(R.id.action_secondary).setVisibility(View.GONE);
        view_button(R.id.action).setText("Save Result");
        view_button(R.id.action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoutineExercise.ExerciseDetails exerciseDetails = mResultEditPresenter.result();
                if (exerciseDetails instanceof RoutineExercise.PowerExerciseDetails){
                    mTrainingPlan.commitPowerSet(((RoutineExercise.PowerExerciseDetails) exerciseDetails).weight, ((RoutineExercise.PowerExerciseDetails) exerciseDetails).times);
                }else if (exerciseDetails instanceof RoutineExercise.DistanceExerciseDetails){
                    mTrainingPlan.commitDistanceSet(((RoutineExercise.DistanceExerciseDetails) exerciseDetails).distance);
                }else if (exerciseDetails instanceof RoutineExercise.TimeExerciseDetails){
                    mTrainingPlan.commitTimeSet(((RoutineExercise.TimeExerciseDetails) exerciseDetails).time);
                }else{
                    throw new IllegalStateException();
                }
                fillUI_SetsDetails();
                if (mTrainingPlan.hasMoreSetsScheduled()){
                    mState = State.START_AWAITING;
                    updateUI(true);
                }else{
                    mState = State.FINISH_AWAITING;
                    updateUI(true);
                }
            }
        });
    }

    private void fillUI_awaitingStop() {
        view(R.id.stop_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClockViewPresenter != null){
                    mClockViewPresenter.resetClock();
                    mClockViewPresenter = null;
                }
                if (mDowntimeClockViewPresenter != null){
                    mDowntimeClockViewPresenter.resetClock();
                    mDowntimeClockViewPresenter = null;
                }
                mTrainingPlan.stopSet();
                mState = State.RESULT_AWAITING;
                updateUI(true);
            }
        });
        String description = RoutineExercise.shortDescription(mRoutineExercise.exerciseDetails, getResources());
        if (mExerciseType == Exercise.Type.weight_times) {
            int sets = ((RoutineExercise.PowerExerciseDetails) mRoutineExercise.exerciseDetails).sets;
            ((RoutineExercise.PowerExerciseDetails) mRoutineExercise.exerciseDetails).sets = -1;
            description =
                    RoutineExercise.detailsCharacteristic(mRoutineExercise.exerciseDetails, getResources())
                            + " " + (mTrainingPlan.getSetIndex() + 1) + " " +
                            RoutineExercise.shortDescription(mRoutineExercise.exerciseDetails, getResources());
            ((RoutineExercise.PowerExerciseDetails) mRoutineExercise.exerciseDetails).sets = sets;
        }

        view_text(R.id.text_exercise_details_short).setText(description);
        if (mExerciseType == Exercise.Type.time) {
            mDowntimeClockViewPresenter = new DowntimeClockViewPresenter(view_text(R.id.text_exercise_execution_timer));
            mDowntimeClockViewPresenter.startClock(mTrainingPlan.getSetStartDate(),
                    (long) (((RoutineExercise.TimeExerciseDetails)mRoutineExercise.exerciseDetails).time * 1000 * 60));
        } else {
            mClockViewPresenter = new ClockViewPresenter(view_text(R.id.text_exercise_execution_timer));
            mClockViewPresenter.startClock(mTrainingPlan.getSetStartDate());
        }
    }

    private void fillUI_awaitingStart() {
        view_button(R.id.action_secondary).setVisibility(View.GONE);
        int nextSetIndex = mTrainingPlan.getSetIndex() + 1;
        if (nextSetIndex == 0){
            view_button(R.id.action).setText("Start Exercise");
        }else {
            view_button(R.id.action).setText("Start Set "+(nextSetIndex+1));
        }
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
        view(R.id.panel_exercise_details, ViewGroup.class).removeAllViews();

        switch (mExerciseType){
            case weight_times:
                RoutineExercise.PowerExerciseDetails exerciseDetails = (RoutineExercise.PowerExerciseDetails) mRoutineExercise.exerciseDetails;
                addDetails("Weight",exerciseDetails.weight, "kg");
                addDetails("Reps",exerciseDetails.times, "reps");
                addDetails("Sets",exerciseDetails.sets, "sets");
                break;
            case distance:
            case time:
                RoutineExercise.ExerciseDetails details = mRoutineExercise.exerciseDetails;
                addDetails(
                        RoutineExercise.detailsCharacteristic(details, getResources()),
                        RoutineExercise.detailsValue(details, getResources()),
                        RoutineExercise.detailsMeasure(details, getResources())
                        );
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

    private void addSetDetails(RoutineExercise.ExerciseDetails details) {
        ViewGroup viewPanel =view(R.id.panel_sets_details, ViewGroup.class);
        View view = activity().getLayoutInflater().inflate(R.layout.panel_3_column_details,viewPanel,false);
        ((TextView)view.findViewById(R.id.item_caption)).setText(RoutineExercise.detailsCharacteristic(details, getResources()));
        ((TextView)view.findViewById(R.id.item_value)).setText(RoutineExercise.detailsValue(details, getResources()));
        ((TextView)view.findViewById(R.id.item_measure)).setText(RoutineExercise.detailsMeasure(details, getResources()));
        viewPanel.addView(view);
    }

    private enum State {
        START_AWAITING, STOP_AWAITING, RESULT_AWAITING, FINISH_AWAITING
    }
}
