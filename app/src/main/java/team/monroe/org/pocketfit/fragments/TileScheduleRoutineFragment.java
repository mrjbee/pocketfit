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
import team.monroe.org.pocketfit.presentations.RoutineSchedule;

public class TileScheduleRoutineFragment extends DashboardTileFragment {

    private Data.DataChangeObserver<RoutineSchedule> observer_activeRoutineObserver;
    private RoutineSchedule mSchedule;

    @Override
    protected String getHeaderName() {
        return "Workout Schedule";
    }

    @Override
    protected int getTileLayoutId() {
        return R.layout.tile_content_active_routine;
    }

    @Override
    public View build_HeaderActionsView(ViewGroup actionPanel, LayoutInflater layoutInflater) {
        View view = layoutInflater.inflate(R.layout.actions_routine_schedule,actionPanel, false);
        view.findViewById(R.id.action_edit_routine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                owner().openRoutineEditor(mSchedule.routine.id);
            }
        });

        return view;
    }

    @Override
    public void onMainButton() {

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

            }
        }));

    }

}
