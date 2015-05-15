package team.monroe.org.pocketfit;

import android.os.Bundle;

import org.monroe.team.android.box.app.ActivitySupport;

import team.monroe.org.pocketfit.fragments.DashboardFragment;
import team.monroe.org.pocketfit.fragments.HeaderFragment;
import team.monroe.org.pocketfit.fragments.RoutineEditFragment;

public class Dashboard extends ActivitySupport<PocketFitApp> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        if (isFirstRun(savedInstanceState)){
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container_body, new DashboardFragment())
                    .commit();
        }
    }


    public void goToRoutineEditor(String routineId) {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.slide_in, R.animator.slide_out)
                .replace(R.id.fragment_container_body, new RoutineEditFragment())
                .commit();
    }

    public void setHeader(String headerText, boolean secondary) {
        HeaderFragment fragment = (HeaderFragment) getFragmentManager().findFragmentById(R.id.fragment_header);
        fragment.changeCaption(headerText, secondary);
    }
}
