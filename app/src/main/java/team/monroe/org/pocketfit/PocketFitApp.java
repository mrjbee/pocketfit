package team.monroe.org.pocketfit;

import org.monroe.team.android.box.app.ApplicationSupport;

public class PocketFitApp extends ApplicationSupport<PocketFitModel>{

    @Override
    protected PocketFitModel createModel() {
        return new PocketFitModel(getApplicationContext());
    }

}
