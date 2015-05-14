package team.monroe.org.pocketfit;

import android.os.Bundle;

import org.monroe.team.android.box.app.ActivitySupport;

public class Dashboard extends ActivitySupport<PocketFitApp> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

}
