package team.monroe.org.pocketfit.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import org.monroe.team.android.box.data.Data;
import org.monroe.team.android.box.utils.DisplayUtils;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;

public class TileRoutineExecutedFragment extends DashboardTileFragment {

    private Data.DataChangeObserver<Pair> observer_activeRoutineObserver;
    private Routine mRoutine;
    private RoutineDay mDay;

    @Override
    protected String getHeaderName() {
        return "Workout In Progress";
    }

    @Override
    protected int getTileLayoutId() {
        return R.layout.tile_content_active_routine;
    }


    @Override
    public void onMainButton() {
    }


    private void onDescriptionLink() {
        owner().openTrainingRunner();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view(R.id.action_calendar).setVisibility(View.INVISIBLE);
        view(R.id.text_days_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDescriptionLink();
            }
        });
        view_button(R.id.text_days_left).setText("Continue Workout");
    }

    @Override
    public void onStart() {
        super.onStart();
        observer_activeRoutineObserver = observe_data_change(State.STOP, new Data.DataChangeObserver<Pair>() {

            @Override
            public void onDataInvalid() {
                fetch_ActiveRoutine();
            }

            @Override
            public void onData(Pair routine) {

            }
        });
        application().data_runningTraining().addDataChangeObserver(observer_activeRoutineObserver);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetch_ActiveRoutine();
    }

    @Override
    public void onStop() {
        super.onStop();
        application().data_runningTraining().removeDataChangeObserver(observer_activeRoutineObserver);
    }

    private void fetch_ActiveRoutine() {
        application().data_runningTraining().fetch(true, observe_data_fetch(State.STOP, new PocketFitApp.DataAction<Pair>() {
            @Override
            public void data(Pair pair) {
                Pair<Routine,RoutineDay> trainingPair = pair;
                mRoutine = trainingPair.first;
                mDay = trainingPair.second;
                if (mRoutine.isNull()){
                    application().error(new IllegalStateException());
                }

                view_text(R.id.text_title).setText(mRoutine.title);
                view_text(R.id.text_description).setText(mRoutine.description);

                updateRoutineCover();
            }
        }));

    }

    private void updateRoutineCover() {
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
            view(R.id.image_cover, ImageView.class).setImageResource(R.drawable.no_covert);
        }
    }

}
