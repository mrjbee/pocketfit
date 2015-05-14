package team.monroe.org.pocketfit;

import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.data.Data;

public class PocketFitApp extends ApplicationSupport<PocketFitModel>{

    private Data<RoutineDetails> data_routineDetails;

    @Override
    protected PocketFitModel createModel() {
        return new PocketFitModel(getApplicationContext());
    }

    @Override
    protected void onPostCreate() {
        data_routineDetails = new Data<RoutineDetails>(RoutineDetails.class, model()) {
            @Override
            protected RoutineDetails provideData() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {}
                return new RoutineDetails();
            }
        };
    }


    private void onFetchError(Data.FetchError fetchError) {

    }

    public Data<RoutineDetails> data_routineDetails() {
        return data_routineDetails;
    }

    public final static class RoutineDetails{

    }

    public static abstract class FetchObserver<ValueType> implements Data.FetchObserver<ValueType> {
        final PocketFitApp owner;

        protected FetchObserver(PocketFitApp app) {
            this.owner = app;
        }

        @Override
        public void onError(Data.FetchError fetchError) {
              owner.onFetchError(fetchError);
        }
    }


}
