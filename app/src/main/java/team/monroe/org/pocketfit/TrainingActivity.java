package team.monroe.org.pocketfit;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Pair;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.monroe.team.android.box.app.ui.GenericListViewAdapter;
import org.monroe.team.android.box.app.ui.GetViewImplementation;
import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;
import org.monroe.team.android.box.data.Data;
import org.monroe.team.android.box.utils.DisplayUtils;
import org.monroe.team.corebox.utils.Closure;

import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.*;

import java.util.Date;

import team.monroe.org.pocketfit.fragments.BodyFragment;
import team.monroe.org.pocketfit.fragments.TrainingEndFragment;
import team.monroe.org.pocketfit.fragments.TrainingExerciseFragment;
import team.monroe.org.pocketfit.fragments.TrainingTileDistanceExecuteFragment;
import team.monroe.org.pocketfit.fragments.TrainingTileDistanceResultFragment;
import team.monroe.org.pocketfit.fragments.TrainingTileExerciseFragment;
import team.monroe.org.pocketfit.fragments.TrainingTilePowerAllResultFragment;
import team.monroe.org.pocketfit.fragments.TrainingTilePowerExecuteFragment;
import team.monroe.org.pocketfit.fragments.TrainingTilePowerResultFragment;
import team.monroe.org.pocketfit.fragments.TrainingTileTimeExecuteFragment;
import team.monroe.org.pocketfit.fragments.TrainingTileTimeResultFragment;
import team.monroe.org.pocketfit.presentations.Exercise;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineExercise;
import team.monroe.org.pocketfit.view.presenter.ClockViewPresenter;
import team.monroe.org.pocketfit.view.presenter.ExerciseResultEditPresenter;

import static team.monroe.org.pocketfit.TrainingExecutionService.TrainingPlan.NoOpTrainingPlanListener;

public class TrainingActivity extends FragmentActivity{

    private ClockViewPresenter mTrainingDurationClockPresenter;
    private ClockViewPresenter mTrainingPauseClockPresenter;
    private AppearanceController mPauseClockAnimator;
    private AppearanceController mTrainingClockAnimator;
    private GenericListViewAdapter<TrainingExecutionService.TrainingPlan.AgendaExercise, GetViewImplementation.ViewHolder<TrainingExecutionService.TrainingPlan.AgendaExercise>> mAgendaAdapter;
    private Data.DataChangeObserver<TrainingExecutionService.TrainingPlan.Agenda> agendaDataChangeObserver;
    private AppearanceController mShadowLayerAnimator;
    private AppearanceController mEditPanelAnimator;
    private ExerciseResultEditPresenter mResultEditPresenter;
    private Closure<RoutineExercise.ExerciseDetails, Void> mAwaitingEditDoneClosure;

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
                .hideAndInvisible()
                .hideAnimation(duration_constant(200), interpreter_decelerate(0.5f))
                .build();

        mTrainingClockAnimator = animateAppearance(view(R.id.text_clock), scale(1f,0f))
                .showAnimation(duration_constant(300),interpreter_overshot())
                .hideAndInvisible()
                .hideAnimation(duration_constant(200), interpreter_decelerate(0.5f))
                .build();

        mShadowLayerAnimator = animateAppearance(view(R.id.layer_shadow), alpha(1f,0))
                .showAnimation(duration_constant(300), interpreter_accelerate(0.3f))
                .hideAndGone()
                .hideAnimation(duration_constant(200), interpreter_decelerate(0.5f))
                .build();
        view(R.id.layer_shadow).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mShadowLayerAnimator.hideWithoutAnimation();

        mEditPanelAnimator = animateAppearance(view(R.id.layer_edit), ySlide(0, 0.5f* DisplayUtils.screenHeight(getResources())))
                .showAnimation(duration_constant(300), interpreter_accelerate_decelerate())
                .hideAnimation(duration_constant(200), interpreter_accelerate(0.8f))
                .hideAndGone()
                .build();
        mEditPanelAnimator.hideWithoutAnimation();
        mResultEditPresenter = new ExerciseResultEditPresenter(view(R.id.panel_edit, ViewGroup.class));
        view(R.id.action_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDetailsEdit();
            }
        });

        view(R.id.action_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unblockDrawer();
                mAwaitingEditDoneClosure.execute(mResultEditPresenter.result());
                mShadowLayerAnimator.hide();
                mEditPanelAnimator.hide();
            }
        });

        view(R.id.action_options).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.panel_popup_training, null);
                view.findViewById(R.id.action_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelTraining();
                    }
                });
                view.findViewById(R.id.action_stop).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopTraining(false);
                    }
                });
                PopupWindow popupWindow = new PopupWindow(view,
                        (int) DisplayUtils.dpToPx(200, getResources()),
                        (int) DisplayUtils.dpToPx(120, getResources()),
                        true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_white));
                popupWindow.showAsDropDown(view(R.id.action_options));
            }
        });


        mAgendaAdapter =
                new GenericListViewAdapter<TrainingExecutionService.TrainingPlan.AgendaExercise, GetViewImplementation.ViewHolder<TrainingExecutionService.TrainingPlan.AgendaExercise>>(this, new GetViewImplementation.ViewHolderFactory<GetViewImplementation.ViewHolder<TrainingExecutionService.TrainingPlan.AgendaExercise>>() {
                    @Override
                    public GetViewImplementation.ViewHolder<TrainingExecutionService.TrainingPlan.AgendaExercise> create(final View convertView) {
                        return new GetViewImplementation.ViewHolder<TrainingExecutionService.TrainingPlan.AgendaExercise>() {

                            TextView caption = (TextView) convertView.findViewById(R.id.item_caption);
                            TextView index = (TextView) convertView.findViewById(R.id.item_index);
                            ImageView circleImage = (ImageView) convertView.findViewById(R.id.image_circle);

                            ImageView line = (ImageView) convertView.findViewById(R.id.image_line);
                            ImageView lineOver = (ImageView) convertView.findViewById(R.id.image_line_over);

                            ViewGroup setsPanel = (ViewGroup) convertView.findViewById(R.id.panel_exercise_sets);

                            @Override
                            public void update(TrainingExecutionService.TrainingPlan.AgendaExercise exercise, int position) {
                                setsPanel.removeAllViews();
                                caption.setText(exercise.exercise.title);
                                index.setText((position+1)+"");
                                circleImage.setImageResource(!exercise.isExecuted()? R.drawable.circle_gray:R.drawable.circle_pink);
                                if (position == 0){
                                    line.setImageResource(R.drawable.gray_line_bottom);
                                    lineOver.setVisibility(View.VISIBLE);
                                }else if (position == mAgendaAdapter.getCount()-1){
                                    line.setImageResource(R.drawable.gray_line_top);
                                    lineOver.setVisibility(View.INVISIBLE);
                                }else {
                                    line.setImageResource(R.drawable.gray_line_both);
                                    lineOver.setVisibility(View.VISIBLE);
                                }

                                for (final TrainingExecutionService.TrainingPlan.ExerciseResult result : exercise.results) {
                                    final RoutineExercise.ExerciseDetails details = result.asExerciseDetails();
                                    View view = getLayoutInflater().inflate(R.layout.panel_3_column_details_edit, setsPanel, false);
                                    ((TextView)view.findViewById(R.id.item_caption)).setVisibility(View.GONE);
                                    ((TextView)view.findViewById(R.id.item_value)).setText(RoutineExercise.detailsValue(details, getResources()));
                                    ((TextView)view.findViewById(R.id.item_measure)).setText(RoutineExercise.detailsMeasure(details, getResources()));
                                    view.findViewById(R.id.action_edit).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            TrainingActivity.this.editDetails(details, new Closure<RoutineExercise.ExerciseDetails, Void>() {

                                                @Override
                                                public Void execute(RoutineExercise.ExerciseDetails arg) {
                                                    if (arg != null){
                                                        result.updateDetails(arg);
                                                    }
                                                    return null;
                                                }
                                            });
                                        }
                                    });
                                    setsPanel.addView(view);
                                }

                            }
                            @Override
                            public void cleanup() {}
                        };
                    }
                }, R.layout.item_agenda_exercise);

        view_list(R.id.list_workout_agenda).setAdapter(mAgendaAdapter);
    }

    private void cancelDetailsEdit() {
        unblockDrawer();
        mAwaitingEditDoneClosure.execute(null);
        mShadowLayerAnimator.hide();
        mEditPanelAnimator.hide();
    }

    private void editDetails(RoutineExercise.ExerciseDetails details, Closure<RoutineExercise.ExerciseDetails, Void> onDone) {
        closeDrawerIfRequired();
        blockDrawer();
        mResultEditPresenter.setup(details);
        mAwaitingEditDoneClosure = onDone;
        mShadowLayerAnimator.show();
        mEditPanelAnimator.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        agendaDataChangeObserver = new Data.DataChangeObserver<TrainingExecutionService.TrainingPlan.Agenda>() {
            @Override
            public void onDataInvalid() {
                updateAgenda();
            }

            @Override
            public void onData(TrainingExecutionService.TrainingPlan.Agenda agenda) {

            }
        };

        application().getTrainingPlan().getAgenda().addDataChangeObserver(agendaDataChangeObserver);

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
        updateRoutineCover();
        updateDetails();
        updateAgenda();
    }



    private void updateAgenda() {
        application().getTrainingPlan().getAgenda().fetch(true,
                new PocketFitApp.FetchObserver<TrainingExecutionService.TrainingPlan.Agenda>(application()) {
            @Override
            public void onFetch(TrainingExecutionService.TrainingPlan.Agenda agenda) {
                mAgendaAdapter.clear();
                for (TrainingExecutionService.TrainingPlan.AgendaExercise agendaExercise : agenda.exerciseList) {
                    mAgendaAdapter.add(agendaExercise);
                }
                mAgendaAdapter.notifyDataSetChanged();
            }
        });
    }

    private void updateDetails() {
        view_text(R.id.text_description).setText(application().getTrainingPlan().getRoutineDay().description);
    }


    private void updateRoutineCover() {
        Routine mRoutine = application().getTrainingRoutine();
        if (mRoutine.imageId != null) {
            String wasId = (String) view(R.id.image_cover, ImageView.class).getTag();
            if (mRoutine.imageId.equals(wasId)) return;
            view(R.id.image_cover, ImageView.class).setImageResource(R.drawable.covert_loading);
            application().loadToBitmap(mRoutine.imageId,
                    DisplayUtils.screenHeight(getResources()),
                    DisplayUtils.screenHeight(getResources()), new PocketFitApp.DataAction<Pair<String, Bitmap>>() {
                        @Override
                        public void data(Pair<String, Bitmap> data) {
                            view(R.id.image_cover, ImageView.class).setImageBitmap(data.second);
                            view(R.id.image_cover, ImageView.class).setTag(data.first);
                        }
                    });
        }else{
            view(R.id.image_cover, ImageView.class).setTag(null);
            view(R.id.image_cover, ImageView.class).setImageResource(R.drawable.no_covert);
        }
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
                if (application().getTrainingPlan().isPaused())mPauseClockAnimator.show();
            }else {
                mTrainingClockAnimator.showWithoutAnimation();
                if (application().getTrainingPlan().isPaused())mPauseClockAnimator.showWithoutAnimation();
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
        unblockDrawer();
        mTrainingDurationClockPresenter.resetClock();
        if (application().getTrainingPlan() != null) {
            application().getTrainingPlan().setTrainingPlanListener(null);
            application().getTrainingPlan().getAgenda().removeDataChangeObserver(agendaDataChangeObserver);
        }
    }

    private Class<? extends BodyFragment> calculateCurrentFragment() {
        TrainingExecutionService.TrainingPlan trainingPlan = application().getTrainingPlan();

        if (!application().getTrainingPlan().hasMoreExercises()){
            return TrainingEndFragment.class;
        }else{
            return TrainingExerciseFragment.class;
        }
/*
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
                    case time:
                        return TrainingTileTimeResultFragment.class;
                    case distance:
                        return TrainingTileDistanceResultFragment.class;
                    default:
                        throw new IllegalStateException();
                }
            }else {
                //show set execution
                switch (exerciseType){
                    case weight_times:
                        return TrainingTilePowerExecuteFragment.class;
                    case time:
                        return TrainingTileTimeExecuteFragment.class;
                    case distance:
                        return TrainingTileDistanceExecuteFragment.class;
                    default:
                        throw new IllegalStateException();
                }
            }
        }
        //return TrainingTileLoadingRoutineExerciseFragment.class;
        */
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
            } else if (currentFragment == TrainingTilePowerResultFragment.class && nextTileFragment == TrainingTilePowerExecuteFragment.class ){
                animationRequest = animation_flip_out();
            } else if (nextTileFragment == TrainingTilePowerResultFragment.class
                    || nextTileFragment == TrainingTileDistanceResultFragment.class
                    || nextTileFragment == TrainingTileTimeResultFragment.class
                    ){
                animationRequest = animation_flip_in();
            }
        }
        currentFragment = nextTileFragment;
        replaceBodyFragment(new FragmentItem(nextTileFragment), animationRequest);
        updateClock(true);
    }

    public void stopTraining(boolean completelyDone) {
        application().getTrainingPlan().setTrainingPlanListener(null);
        application().stopTraining(completelyDone);
        finish();
    }

    public void cancelTraining() {
        application().getTrainingPlan().setTrainingPlanListener(null);
        application().cancelTraining();
        finish();
    }

    @Override
    public void onBackPressed() {
        if (view(R.id.layer_edit).getVisibility() == View.VISIBLE){
            cancelDetailsEdit();
            return;
        }

        if (!closeDrawerIfRequired()) {
            super.onBackPressed();
        }
    }

    private boolean closeDrawerIfRequired() {
        boolean isOpened = false;
        DrawerLayout drawer = view(R.id.drawer_layout, DrawerLayout.class);
        if (drawer.isDrawerVisible(view(R.id.left_drawer))){
            drawer.closeDrawer(view(R.id.left_drawer));
            isOpened = true;
        }
        return isOpened;
    }

    private void unblockDrawer() {
        DrawerLayout drawer = view(R.id.drawer_layout, DrawerLayout.class);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, view(R.id.left_drawer));
    }


    private void blockDrawer() {
        DrawerLayout drawer = view(R.id.drawer_layout, DrawerLayout.class);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, view(R.id.left_drawer));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        unblockDrawer();
        super.onSaveInstanceState(outState);
    }
}
