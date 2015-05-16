package team.monroe.org.pocketfit.fragments;


import android.os.Bundle;
import android.view.View;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.Routine;

public class RoutinesFragment extends BodyFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_routines;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view(R.id.panel_new_routine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                application().function_createEmptyRoutine(application().observe_function(new PocketFitApp.DataAction<Routine>() {
                    @Override
                    public void data(Routine routine) {
                        owner().open_Routine(routine.id);
                    }
                }));
            }
        });
    }

    @Override
    protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    protected String getHeaderName() {
        return "Workout Routines";
    }

}
