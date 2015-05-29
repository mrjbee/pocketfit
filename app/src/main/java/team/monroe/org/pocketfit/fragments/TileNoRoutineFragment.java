package team.monroe.org.pocketfit.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.monroe.team.android.box.data.Data;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.Routine;

public class TileNoRoutineFragment extends DashboardNoBottomTileFragment {


    private Data.DataChangeObserver<Routine> observer_activeRoutineObserver;

    @Override
    protected String getHeaderName() {
        return "Workout";
    }

    @Override
    protected int getTileLayoutId() {
        return R.layout.tile_content_no_active_routine;
    }

    @Override
    public View build_HeaderActionsView(ViewGroup actionPanel, LayoutInflater layoutInflater) {
        View view = layoutInflater.inflate(R.layout.actions_no_routines,actionPanel, false);
        view.findViewById(R.id.action_edit_exercises).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                owner().openExerciseEditor();
            }
        });
        return view;
    }

    @Override
    public void onMainButton() {
        owner().hideMainButton(new Runnable() {
            @Override
            public void run() {
                owner().openRoutinesEditor();
            }
        });
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
                if (routine.id != null){
                    owner().hideMainButton(new Runnable() {
                        @Override
                        public void run() {
                            owner().switch_workoutTile();
                        }
                    });
                } else {
                    owner().showMainButton(R.drawable.round_btn_gear, null);
                }
            }
        }));
    }
}
