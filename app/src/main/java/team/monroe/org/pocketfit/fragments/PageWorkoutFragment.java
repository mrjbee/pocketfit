package team.monroe.org.pocketfit.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.*;

import org.monroe.team.android.box.app.ui.GenericListViewAdapter;
import org.monroe.team.android.box.app.ui.GetViewImplementation;
import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;
import org.monroe.team.android.box.app.ui.animation.apperrance.SceneDirector;
import org.monroe.team.android.box.data.Data;
import org.monroe.team.android.box.utils.DisplayUtils;
import org.monroe.team.corebox.utils.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;
import team.monroe.org.pocketfit.presentations.RoutineSchedule;

public class PageWorkoutFragment extends DashboardStartPageFragment {

    private Data.DataChangeObserver<RoutineSchedule> observer_activeRoutineSchedule;
    private RoutineSchedule mSchedule;


    private TransformationState mState;
    private AppearanceController acSeparatorSecondary;
    private AppearanceController acTopTitle;
    private AppearanceController acBottomTitle;
    private AppearanceController acCover;
    private AppearanceController acOptions;
    private AppearanceController acActions;
    private AppearanceController acDescription;
    private AppearanceController acSchedule;
    private GenericListViewAdapter<Day, GetViewImplementation.ViewHolder<Day>> mDayListViewAdapter;
    private PopupWindow mOptionsPopupWindow;
    private AppearanceController acNoWorkout;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        configureHeader("My Workout", build_HeaderActionsView());

        acNoWorkout= animateAppearance(view(R.id.panel_no_routine),
                ySlide(0, DisplayUtils.screenHeight(getResources())))
                .showAnimation(duration_constant(400), interpreter_accelerate(0.5f))
                .hideAnimation(duration_constant(400))
                .hideAndGone().build();

        acTopTitle = animateAppearance(view(R.id.text_title_top), heightSlide((int) DisplayUtils.dpToPx(50, getResources()), 0))
                .showAnimation(duration_constant(300), interpreter_accelerate(0.5f))
                .hideAnimation(duration_constant(200))
                .hideAndGone().build();

        acBottomTitle = animateAppearance(view(R.id.text_title_bottom), heightSlide((int) DisplayUtils.dpToPx(50, getResources()), 0))
                .showAnimation(duration_constant(200))
                .hideAnimation(duration_constant(200))
                .hideAndGone().build();

        acDescription = animateAppearance(view(R.id.text_description), alpha(1,0))
                .showAnimation(duration_constant(200))
                .hideAnimation(duration_constant(200))
                .hideAndGone().build();


        acSchedule = animateAppearance(view(R.id.panel_schedule), alpha(1,0))
                .showAnimation(duration_constant(200))
                .hideAnimation(duration_constant(200))
                .hideAndGone().build();

        if (activity().isLandscape(R.bool.class)){
            acCover = animateAppearance(view(R.id.image_cover), widthSlide(
                    (int) DisplayUtils.dpToPx(250, getResources()),
                    (int) DisplayUtils.dpToPx(100, getResources())))
                    .showAnimation(duration_constant(500), interpreter_accelerate_decelerate())
                    .hideAnimation(duration_constant(500), interpreter_overshot())
                    .build();
        }else {
            acCover = animateAppearance(view(R.id.image_cover), heightSlide(
                    (int) DisplayUtils.dpToPx(220, getResources()),
                    (int) DisplayUtils.dpToPx(100, getResources())))
                    .showAnimation(duration_constant(500), interpreter_accelerate_decelerate())
                    .hideAnimation(duration_constant(500), interpreter_overshot())
                    .build();
        }

        acOptions = animateAppearance(view(R.id.action_options), widthSlide((int) DisplayUtils.dpToPx(50, getResources()),0))
                .showAnimation(duration_constant(200))
                .hideAnimation(duration_constant(200))
                .hideAndGone().build();


        acActions = animateAppearance(view(R.id.panel_actions), heightSlide((int) DisplayUtils.dpToPx(50, getResources()), 0))
                .showAnimation(duration_constant(200))
                .hideAnimation(duration_constant(200))
                .hideAndGone().build();

        acSeparatorSecondary = animateAppearance(view(R.id.separator_secondary), alpha(1,0))
                .showAnimation(duration_constant(200))
                .hideAnimation(duration_constant(200))
                .hideAndGone().build();

        mDayListViewAdapter = new GenericListViewAdapter<Day, GetViewImplementation.ViewHolder<Day>>(getActivity(),new GetViewImplementation.ViewHolderFactory<GetViewImplementation.ViewHolder<Day>>() {


            @Override
            public GetViewImplementation.ViewHolder<Day> create(final View convertView) {
                return new GetViewImplementation.GenericViewHolder<Day>() {

                    TextView caption = (TextView) convertView.findViewById(R.id.item_caption);
                    TextView text = (TextView) convertView.findViewById(R.id.item_text);
                    TextView shortDay = (TextView) convertView.findViewById(R.id.item_day);
                    ImageView dayBackground = (ImageView) convertView.findViewById(R.id.item_day_background);
                    View play = convertView.findViewById(R.id.action_play);

                    @Override
                    public void update(final Day day, int position) {
                        play.setFocusable(false);
                        if (day.routineDay != null){
                            shortDay.setTextColor(getResources().getColor(R.color.text_color_day_short_training));
                            text.setVisibility(View.VISIBLE);
                            dayBackground.setImageResource(R.drawable.day_training);
                            caption.setTextColor(getResources().getColor(R.color.text_color_date_training));
                        } else {
                            shortDay.setTextColor(getResources().getColor(R.color.text_color_day_short));
                            dayBackground.setImageResource(R.drawable.day_not_training);
                            text.setVisibility(View.INVISIBLE);
                            caption.setTextColor(getResources().getColor(R.color.text_color_date));
                        }

                        if (day.isPlayAvailable()){
                            play.setVisibility(View.VISIBLE);
                            play.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    application().startTraining(day.routine, day.routineDay, new Runnable() {
                                        @Override
                                        public void run() {
                                            fill_progress_state();
                                            transform_progress_state(true, new Runnable() {
                                                @Override
                                                public void run() {
                                                    action_open_workout();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }else{
                            play.setVisibility(View.INVISIBLE);
                            play.setOnClickListener(null);
                        }

                        if (day.dayDateString.equals("Today")){
                            shortDay.setTextColor(getResources().getColor(R.color.text_color_day_short_training));
                            dayBackground.setImageResource(R.drawable.day_today);
                        }

                        caption.setText(day.dayDateString);
                        text.setText(day.dayDescription);
                        shortDay.setText(day.shortDay);
                        convertView.requestLayout();
                    }
                };
            }
        }, R.layout.item_schedule){
            @Override
            public int getCount() {
                return isScheduleAvailable() ? 0: 200;
            }

            @Override
            public Day getItem(int position) {
                return buildDayFor(DateUtils.mathDays(getStartDate(), position));
            }

            @Override
            public boolean isEmpty() {
                return !isScheduleAvailable();
            }

            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public boolean isEnabled(int position) {
                Day day = getItem(position);
                return day.routineDay != null;
            }
        };

        view_list(R.id.list_items).setAdapter(mDayListViewAdapter);
        view_list(R.id.list_items).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Day day = mDayListViewAdapter.getItem(position);
                if (day.routineDay != null){
                    onRoutineDaySelect(day.routineDay.id);
                }
            }
        });


        view(R.id.action_options).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOptionsPopupWindow ==null) {
                    View view = activity().getLayoutInflater().inflate(R.layout.panel_popup_training, null);
                    view.findViewById(R.id.action_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOptionsPopupWindow.dismiss();
                            application().cancelTraining();
                            fill_about_state();
                            transform_about_state(true);
                        }
                    });
                    view.findViewById(R.id.action_stop).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOptionsPopupWindow.dismiss();
                            application().stopTraining(false);
                            fill_about_state();
                            transform_about_state(true);
                        }
                    });
                    mOptionsPopupWindow = new PopupWindow(view,
                            (int) DisplayUtils.dpToPx(200, getResources()),
                            (int) DisplayUtils.dpToPx(120, getResources()),
                            true);
                }
                mOptionsPopupWindow.setOutsideTouchable(true);
                mOptionsPopupWindow.setFocusable(true);
                mOptionsPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_white));
                mOptionsPopupWindow.showAsDropDown(view(R.id.action_options));
            }
        });


        int state = getArguments().getInt("state");
        mState = TransformationState.values()[state];

        switch (mState){
            case ABOUT:
                transform_about_state(false);
                break;
            case PROGRESS:
                transform_progress_state(false, null);
                break;
            case SCHEDULE:
                transform_schedule_state(false);
                break;
            case NOT_SET:
                transform_not_set_state(false);
                break;
            default:
                throw new IllegalStateException();
        }

    }

    private void transform_not_set_state(boolean animate) {
        updateState(TransformationState.NOT_SET);
        if (!animate){
            acNoWorkout.showWithoutAnimation();
            acCover.showWithoutAnimation();
            acActions.hideWithoutAnimation();
            acBottomTitle.hideWithoutAnimation();
            acDescription.hideWithoutAnimation();
            acSchedule.hideWithoutAnimation();
            acTopTitle.hideWithoutAnimation();
            acOptions.hideWithoutAnimation();
            acSeparatorSecondary.hideWithoutAnimation();
        } else {
            SceneDirector.scenario()
                        .hide(acOptions, acActions, acBottomTitle, acDescription, acSchedule, acTopTitle, acOptions, acSeparatorSecondary)
                        .then()
                            .show(acCover)
                            .then()
                                .show(acNoWorkout)
                    .play();
        }
    }

    private void updateState(TransformationState transformationState){
        mState = transformationState;
        getArguments().putInt("state",mState.ordinal());
        //ownerContract().updateWorkoutTileState(mState);
    }

    private void transform_about_state(boolean animate) {
        updateState(TransformationState.ABOUT);
        if (!animate){
            acNoWorkout.hideWithoutAnimation();
            acCover.showWithoutAnimation();
            acActions.showWithoutAnimation();
            acBottomTitle.showWithoutAnimation();
            acDescription.showWithoutAnimation();
            acSchedule.hideWithoutAnimation();
            acTopTitle.hideWithoutAnimation();
            acOptions.hideWithoutAnimation();
            acSeparatorSecondary.hideWithoutAnimation();
        } else {
            SceneDirector.scenario()
                        .hide(acNoWorkout)
                        .then()
                        .hide(acTopTitle)
                        .hide(acSchedule)
                        .then()
                            .show(acActions, acBottomTitle, acDescription)
                            .hide(acOptions, acSeparatorSecondary)
                            .then()
                                .show(acCover)
                        .play();
            }
    }

    private void transform_schedule_state(boolean animation) {
        updateState(TransformationState.SCHEDULE);
        if (animation){
            SceneDirector.scenario()
                        .hide(acNoWorkout)
                        .then()
                        .hide(acActions)
                        .hide(acTopTitle)
                        .hide(acOptions)
                        .then()
                            .show(acBottomTitle,acSeparatorSecondary)
                            .hide(acDescription)
                            .then()
                                .hide(acCover)
                                    .then()
                                        .show(acSchedule)
                        .play();
        }else {
            acNoWorkout.hideWithoutAnimation();
            acTopTitle.hideWithoutAnimation();
            acCover.hideWithoutAnimation();
            acActions.hideWithoutAnimation();
            acBottomTitle.showWithoutAnimation();
            acDescription.hideWithoutAnimation();
            acSchedule.showWithoutAnimation();
            acOptions.hideWithoutAnimation();
            acSeparatorSecondary.showWithoutAnimation();
        }
    }

    private void transform_progress_state(boolean animation, final Runnable postAnimationAction) {
        updateState(TransformationState.PROGRESS);
        if (animation){
            SceneDirector.scenario()
                    .hide(acNoWorkout)
                    .then()
                    .hide(acSchedule, acCover)
                    .then()
                        .show(acBottomTitle, acActions)
                        .hide(acSeparatorSecondary)
                        .then()
                            .show(acOptions)
                            .then()
                                .show(acDescription)
                                .then()
                                    .show(acTopTitle)
                                    .then()
                                        .action(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (postAnimationAction != null) {
                                                    postAnimationAction.run();
                                                }
                                            }
                                        })
                    .play();
        }else{
            acNoWorkout.hideWithoutAnimation();
            acDescription.showWithoutAnimation();
            acSchedule.hideWithoutAnimation();
            acTopTitle.showWithoutAnimation();
            acCover.hideWithoutAnimation();
            acActions.showWithoutAnimation();
            acOptions.showWithoutAnimation();
            acBottomTitle.showWithoutAnimation();
            acSeparatorSecondary.hideWithoutAnimation();
        }
    }

    @Deprecated
    protected String getHeaderName() {
        return "Workout";
    }

    @Override
    protected int getTileLayoutId() {
        return R.layout.page_content_workout;
    }

    public View build_HeaderActionsView() {
        View view = activity().getLayoutInflater().inflate(R.layout.actions_routine, null);
        view.findViewById(R.id.action_edit_exercises).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                owner().openExerciseEditor();
            }
        });
        view.findViewById(R.id.action_edit_routines).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                owner().openRoutinesEditor();
            }
        });

        return view;
    }

    @Override
    public void onMainButton() {
        if (mState == TransformationState.NOT_SET) {
            owner().hideMainButton(new Runnable() {
                @Override
                public void run() {
                    owner().openRoutinesEditor();
                }
            });
        }else if (mState == TransformationState.ABOUT){
            if (!mSchedule.isDefined()){
                owner().hideMainButton(new Runnable() {
                    @Override
                    public void run() {
                        owner().openRoutineEditor(mSchedule.mRoutine.id);
                    }
                });
            } else {
                if (mSchedule.getDaysBeforeNextTrainingDay() == 0){
                    owner().hideMainButton(new Runnable() {
                        @Override
                        public void run() {
                            application().startTraining(mSchedule.mRoutine, mSchedule.getTrainingDay(DateUtils.now()), new Runnable() {
                                @Override
                                public void run() {
                                   fill_progress_state();
                                   transform_progress_state(true,new Runnable() {
                                        @Override
                                        public void run() {
                                            action_open_workout();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        }
    }



    private void fill_progress_state() {
        view_text(R.id.text_title_top).setText(application().getTrainingPlan().getRoutine().title);
        view_text(R.id.text_title_bottom).setText("Workout Description");
        view_text(R.id.text_description).setText(application().getTrainingPlan().getRoutineDay().description);
        view_button(R.id.action).setText("Continue Workout");
        view_button(R.id.action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action_open_workout();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        observer_activeRoutineSchedule = observe_data_change(State.STOP, new Data.DataChangeObserver<RoutineSchedule>() {

            @Override
            public void onDataInvalid() {
                fetch_ActiveRoutineSchedule();
            }

            @Override
            public void onData(RoutineSchedule routine) {

            }
        });
        application().data_activeRoutineSchedule().addDataChangeObserver(observer_activeRoutineSchedule);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetch_ActiveRoutineSchedule();
    }

    @Override
    public void onStop() {
        super.onStop();
        application().data_activeRoutineSchedule().removeDataChangeObserver(observer_activeRoutineSchedule);
    }

    private void fetch_ActiveRoutineSchedule() {
        application().data_activeRoutineSchedule().fetch(true, observe_data_fetch(State.STOP, new PocketFitApp.DataAction<RoutineSchedule>() {
            @Override
            public void data(RoutineSchedule schedule) {
                mSchedule = schedule;
                if (mSchedule.isNull()){
                    fill_not_set_state();
                    transform_not_set_state(true);
                } else {
                    updateRoutineCover();
                    switch (mState){
                        case ABOUT:
                        case NOT_SET:
                            fill_about_state();
                            transform_about_state(true);
                            break;
                        case PROGRESS:
                            //After training activity closed
                            if (mState == TransformationState.PROGRESS && application().getTrainingPlan() == null){
                                runLastOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        fill_about_state();
                                        transform_about_state(true);

                                    }
                                }, 1000);
                            }else {
                                fill_progress_state();
                            }
                            break;
                        case SCHEDULE:
                            fill_schedule_state();
                            break;
                        default:
                            throw new IllegalStateException();
                    }

                }
            }
        }));

    }

    private void fill_not_set_state() {
        owner().showMainButton(R.drawable.round_btn_gear, null);
        mDayListViewAdapter.clear();
        mDayListViewAdapter.notifyDataSetChanged();
    }

    private void fill_about_state() {
        view_text(R.id.text_title_bottom).setText(mSchedule.mRoutine.title);
        view_text(R.id.text_description).setText(mSchedule.mRoutine.description);
        if (!mSchedule.isDefined()){
            view_button(R.id.action).setText("Configure trainings");
            view_button(R.id.action).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action_edit_routine();
                }
            });
            owner().showMainButton(R.drawable.round_btn_pen, null);
        }else{
            int daysBeforeTraining = mSchedule.getDaysBeforeNextTrainingDay();
            if (daysBeforeTraining == 0){
                view_button(R.id.action).setText("Today training");
                owner().showMainButton(R.drawable.round_btn_play, null);
            }else{
                owner().hideMainButton(null);
                view_button(R.id.action).setText(daysBeforeTraining+" days before training");
            }
            view_button(R.id.action).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fill_schedule_state();
                    transform_schedule_state(true);
                }
            });
        }
    }

    private void fill_schedule_state() {
        owner().hideMainButton(null);
        view_text(R.id.text_title_bottom).setText(mSchedule.mRoutine.title);
        mDayListViewAdapter.notifyDataSetChanged();
    }


    private void action_edit_routine() {
        owner().hideMainButton(new Runnable() {
            @Override
            public void run() {
                owner().openRoutineEditor(mSchedule.mRoutine.id);
            }
        });
    }

    private void action_open_workout() {
        owner().openTrainingRunner();
    }

    @Override
    public boolean onBackButton() {
        if (mState == TransformationState.SCHEDULE){
            fill_about_state();
            transform_about_state(true);
            return true;
        }
        return super.onBackButton();
    }

    private void updateRoutineCover() {
        if (mSchedule.mRoutine.imageId != null) {
            String wasId = (String) view(R.id.image_cover, ImageView.class).getTag();
            if (mSchedule.mRoutine.imageId.equals(wasId)) return;
            view(R.id.image_cover, ImageView.class).setImageResource(R.drawable.covert_loading);
            application().loadToBitmap(mSchedule.mRoutine.imageId,
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

    private void onRoutineDaySelect(String id) {
        owner().openDayEditor(mSchedule.mRoutine.id, id);
    }

    private DateFormat dateFormat= DateFormat.getDateInstance();
    private DateFormat dayOfWeekFormat = new SimpleDateFormat("EE");

    private Day buildDayFor(Date date) {
        String dateString = dateFormat.format(date);
        if (DateUtils.isToday(date)){
            dateString = "Today";
        }
        String shortDay = dayOfWeekFormat.format(date);

        RoutineDay routineDay = mSchedule.getTrainingDay(date);
        String description = routineDay != null ? routineDay.description:"";

        return new Day(dateString, description, shortDay, routineDay == null ? null : routineDay,
                mSchedule.mRoutine,
                mSchedule.wasLastTrainingOn(date));
    }

    private boolean isScheduleAvailable() {
        return mSchedule == null || mSchedule.isNull() || !mSchedule.isDefined();
    }

    public Date getStartDate() {
        return DateUtils.today();
    }

    public void backonPageMoveToHide(float top, boolean pageMotionDirectionUp) {
        TextView secondaryHeader = getSecondaryHeaderView();
        float viewPosition = DisplayUtils.dpToPx(110, getResources()) - (secondaryHeader.getBottom() + ((View)secondaryHeader.getParent()).getTop());
        if (top < viewPosition){
            translateHeaderPosition(top);
        }
    }

    private void translateHeaderPosition(float offset) {
        ViewGroup headerPanel = getHeaderContainer();
        headerPanel.setTranslationY(offset);
    }

    @Override
    public void onPageMoveToShow(float top, boolean pageMotionDirectionUp) {
        TextView secondaryHeader = getSecondaryHeaderView();
        float viewPosition = DisplayUtils.dpToPx(108, getResources()) - (secondaryHeader.getBottom() + ((View)secondaryHeader.getParent()).getTop());
        if (Math.abs(top) < viewPosition){
            translateHeaderPosition(Math.abs(top));
        }else{
            translateHeaderPosition(viewPosition);
        }
    }

    @Override
    public void onPageMoveToHide(float top, boolean pageMotionDirectionUp) {
        TextView secondaryHeader = getSecondaryHeaderView();
        float viewPosition = DisplayUtils.dpToPx(108, getResources()) - (secondaryHeader.getBottom() + ((View)secondaryHeader.getParent()).getTop());
        if (Math.abs(top) < viewPosition){
            translateHeaderPosition(Math.abs(top));
        }
    }

    public void backonPageMoveToShow(float top, boolean pageMotionDirectionUp) {
        TextView secondaryHeader = getSecondaryHeaderView();
        float viewPosition = DisplayUtils.dpToPx(110, getResources()) - (secondaryHeader.getBottom() + ((View)secondaryHeader.getParent()).getTop());
        float fragmentHeight = getFragmentView().getHeight();
        if (fragmentHeight - top < viewPosition){
            translateHeaderPosition(fragmentHeight - top);
        }else{
            translateHeaderPosition(viewPosition);
        }
    }


    private class Day{

        private final String dayDateString;
        private final String shortDay;
        private final String dayDescription;
        private final RoutineDay routineDay;
        private final Routine routine;
        private final boolean doneInThisDay;


        private Day(String dateString, String trainingDescription, String shortDay, RoutineDay routineDay, Routine routine, boolean doneInThisDay) {
            this.dayDateString = dateString;
            this.shortDay = shortDay;
            this.dayDescription = trainingDescription;
            this.routineDay = routineDay;
            this.routine = routine;
            this.doneInThisDay = doneInThisDay;
        }

        public boolean isPlayAvailable() {
            return routineDay != null && !doneInThisDay;
        }
    }


    public static enum TransformationState{
        ABOUT, SCHEDULE, PROGRESS, NOT_SET
    }


}
