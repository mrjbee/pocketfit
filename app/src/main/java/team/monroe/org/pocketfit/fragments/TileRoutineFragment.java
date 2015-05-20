package team.monroe.org.pocketfit.fragments;

import android.graphics.Bitmap;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.monroe.team.android.box.data.Data;
import org.monroe.team.android.box.utils.DisplayUtils;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.Routine;

public class TileRoutineFragment extends DashboardTileFragment {

    private Data.DataChangeObserver<Routine> observer_activeRoutineObserver;

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
                owner().openRoutineEditor();
            }
        });
        return view;
    }

    @Override
    public Integer icon_mainButton() {
        return !application().hasActiveRoutine()? null : R.drawable.round_btn_gear;
    }

    @Override
    public void onMainButton() {

    }


    @Override
    public void onStart() {
        super.onStart();
        observer_activeRoutineObserver = observe_data_change(State.STOP, new Data.DataChangeObserver<Routine>() {

            @Override
            public void onDataInvalid() {
                fetch_ActiveRoutine();
            }

            @Override
            public void onData(Routine routine) {

            }
        });
        application().data_activeRoutine().addDataChangeObserver(observer_activeRoutineObserver);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetch_ActiveRoutine();
    }

    @Override
    public void onStop() {
        super.onStop();
        application().data_activeRoutine().removeDataChangeObserver(observer_activeRoutineObserver);
    }

    private void fetch_ActiveRoutine() {
        application().data_activeRoutine().fetch(true, observe_data_fetch(State.STOP, new PocketFitApp.DataAction<Routine>() {
            @Override
            public void data(Routine routine) {
                if (routine.id == null){
                    runLastOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            owner().switchNoRoutineTile();
                        }
                    },300);
                }else {
                    application().loadToBitmap(routine.imageId,
                            DisplayUtils.screenHeight(getResources()),
                            DisplayUtils.screenHeight(getResources()), new PocketFitApp.DataAction<Pair<String, Bitmap>>() {
                                @Override
                                public void data(Pair<String, Bitmap> data) {
                                    view(R.id.image_cover, ImageView.class).setImageBitmap(data.second);
                                }
                            });
                }
            }
        }));
    }


}
