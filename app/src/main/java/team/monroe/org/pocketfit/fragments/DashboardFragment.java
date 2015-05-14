package team.monroe.org.pocketfit.fragments;


import android.view.View;
import android.view.ViewGroup;

import org.monroe.team.android.box.app.FragmentSupport;
import org.monroe.team.android.box.data.Data;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.view.presenter.TileCaptionViewPresenter;
import team.monroe.org.pocketfit.view.presenter.TileNoRoutineViewPresenter;
import team.monroe.org.pocketfit.view.presenter.ViewPresenter;

public class DashboardFragment extends FragmentSupport<PocketFitApp>{

    private final Data.DataChangeObserver<PocketFitApp.RoutineDetails> observer_RoutineDetails = new Data.DataChangeObserver<PocketFitApp.RoutineDetails>() {
        @Override
        public void onDataInvalid() {
            fetchRoutineDetails();
        }

        @Override
        public void onData(PocketFitApp.RoutineDetails routineDetails) {

        }
    };
    private TileCaptionViewPresenter routineCaptionPresenter;
    private TileNoRoutineViewPresenter routineNoTilePresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dashboard;
    }


    @Override
    public void onStart() {
        super.onStart();
        application().data_routineDetails().addDataChangeObserver(observer_RoutineDetails);
        fetchRoutineDetails();
    }

    @Override
    public void onStop() {
        super.onStop();
        application().data_routineDetails().removeDataChangeObserver(observer_RoutineDetails);
    }

    private void fetchRoutineDetails() {
        application().data_routineDetails().fetch(true, new PocketFitApp.FetchObserver<PocketFitApp.RoutineDetails>(application()){
            @Override
            public void onFetch(PocketFitApp.RoutineDetails routineDetails) {
                if (routineCaptionPresenter == null) {
                    routineCaptionPresenter = new TileCaptionViewPresenter(inflateView(R.layout.tile_caption));
                    routineCaptionPresenter.hide();
                    routineCaptionPresenter.setCaption("Workout Routine");
                    addTile(routineCaptionPresenter);

                    routineNoTilePresenter = new TileNoRoutineViewPresenter(inflateView(R.layout.tile_workout_not_set));
                    routineNoTilePresenter.hide();
                    addTile(routineNoTilePresenter);

                    if (activity().isFirstRun()) {
                        routineCaptionPresenter.showWithAnimation();
                        routineNoTilePresenter.showWithAnimation();
                    }else {
                        routineCaptionPresenter.show();
                        routineNoTilePresenter.show();
                    }
                } else {

                }
            }
        });
    }


    private void addTile(ViewPresenter presenter) {
        view(R.id.tile_container, ViewGroup.class).addView(presenter.getRootView());
    }

    private View inflateView(int id) {
        return activity().getLayoutInflater().inflate(id, (android.view.ViewGroup) view(R.id.tile_container),false);
    }

}
