package team.monroe.org.pocketfit.fragments;

import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.app.FragmentSupport;

import team.monroe.org.pocketfit.RootActivity;
import team.monroe.org.pocketfit.PocketFitApp;

public abstract class BodyFragment  extends FragmentSupport<PocketFitApp> {

    private HeaderUpdateRequest headerUpdateRequest = HeaderUpdateRequest.NOT_SET;
    private boolean stopped = true;

    public void feature_headerUpdate(HeaderUpdateRequest headerUpdateRequest) {
        this.headerUpdateRequest = headerUpdateRequest;
    }

    @Override
    public void onStart() {
        stopped = false;
        super.onStart();
        if (headerUpdateRequest != HeaderUpdateRequest.NOT_SET){
            if (headerUpdateRequest == HeaderUpdateRequest.SET){
                ((RootActivity) activity()).header(getHeaderName(), isHeaderSecondary());
            }else {
                ((RootActivity) activity()).animateHeader(getHeaderName(), isHeaderSecondary());
            }
        }
        headerUpdateRequest = HeaderUpdateRequest.NOT_SET;
    }

    protected abstract boolean isHeaderSecondary();
    protected abstract String getHeaderName();

    public static enum HeaderUpdateRequest {
        SET, NOT_SET, ANIMATE
    }

    public RootActivity owner(){
        return (RootActivity) activity();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopped = true;
    }

    public <DataType> ApplicationSupport.ValueObserver<DataType> observe_function(final PocketFitApp.DataAction<DataType> observeAction){
        return application().observe_function(new PocketFitApp.DataAction<DataType>() {
            @Override
            public void data(DataType data) {
                if (!stopped) observeAction.data(data);
            }
        });
    }


}
