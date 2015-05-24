package team.monroe.org.pocketfit.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.monroe.team.android.box.app.ActivitySupport;
import org.monroe.team.android.box.data.Data;
import org.monroe.team.android.box.utils.DisplayUtils;
import org.monroe.team.corebox.utils.DateUtils;

import team.monroe.org.pocketfit.DashboardActivity;
import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.RoutineSchedule;

public class TileRoutineFragment extends DashboardTileFragment {

    private Data.DataChangeObserver<RoutineSchedule> observer_activeRoutineObserver;
    private RoutineSchedule mSchedule;

    @Override
    protected String getHeaderName() {
        return "Workout";
    }

    @Override
    protected int getTileLayoutId() {
        return R.layout.tile_content_active_routine;
    }

    @Override
    public View build_HeaderActionsView(ViewGroup actionPanel, LayoutInflater layoutInflater) {
        View view = layoutInflater.inflate(R.layout.actions_routine,actionPanel, false);
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
                                owner().switch_trainingExecution(true);

                            }
                        });
                    }
                });
                final DashboardActivity activity = owner();
                activity.runLastOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.openTrainingRunner();
                    }
                }, 1000);
            }
        }
    }


    private void onDaysText() {
        if (!mSchedule.isDefined()) onMainButton();
        owner().hideMainButton(new Runnable() {
            @Override
            public void run() {
                owner().switch_activeRoutineSchedule();
            }
        });
    }

    private void onCalendarButton() {
        if (!mSchedule.isDefined()) return;
        owner().hideMainButton(new Runnable() {
            @Override
            public void run() {
                owner().switch_activeRoutineSchedule();
            }
        });
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view(R.id.action_calendar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCalendarButton();
            }
        });
        view(R.id.text_days_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDaysText();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        observer_activeRoutineObserver = observe_data_change(State.STOP, new Data.DataChangeObserver<RoutineSchedule>() {

            @Override
            public void onDataInvalid() {
                fetch_ActiveRoutine();
            }

            @Override
            public void onData(RoutineSchedule routine) {

            }
        });
        application().data_activeRoutineSchedule().addDataChangeObserver(observer_activeRoutineObserver);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetch_ActiveRoutine();
    }

    @Override
    public void onStop() {
        super.onStop();
        application().data_activeRoutineSchedule().removeDataChangeObserver(observer_activeRoutineObserver);
    }

    private void fetch_ActiveRoutine() {
        application().data_activeRoutineSchedule().fetch(true, observe_data_fetch(State.STOP, new PocketFitApp.DataAction<RoutineSchedule>() {
            @Override
            public void data(RoutineSchedule schedule) {
                mSchedule = schedule;
                if (mSchedule.isNull()){
                    owner().hideMainButton(new Runnable() {
                        @Override
                        public void run() {
                            owner().switch_noRoutineTile();
                        }
                    });
                }else {
                    view_text(R.id.text_title).setText(mSchedule.mRoutine.title);
                    view_text(R.id.text_description).setText(mSchedule.mRoutine.description);

                    updateRoutineCover();

                    if (!mSchedule.isDefined()){
                        view_button(R.id.text_days_left).setText("Routine has no trainings");
                        view(R.id.action_calendar).setVisibility(View.GONE);
                        owner().showMainButton(R.drawable.round_btn_pen, null);
                    } else {
                        view(R.id.action_calendar).setVisibility(View.VISIBLE);
                        int daysBeforeTraining = schedule.getDaysBeforeNextTrainingDay();
                        if (daysBeforeTraining == 0){
                            view_button(R.id.text_days_left).setText("Today training");
                            owner().showMainButton(R.drawable.round_btn_play, null);
                        }else{
                            view_button(R.id.text_days_left).setText(daysBeforeTraining+" days before training");
                        }
                    }
                }
            }
        }));

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
            view(R.id.image_cover, ImageView.class).setImageResource(R.drawable.no_covert);
        }
    }


}
