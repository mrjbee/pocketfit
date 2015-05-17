package team.monroe.org.pocketfit.fragments;

import android.net.Uri;

import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.app.FragmentSupport;
import org.monroe.team.android.box.data.Data;

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

    public void onImageResult(Uri uri) {}

    public String getStringArgument(String routine_id) {
       if (getArguments() == null)return null;
       return getArguments().getString("routine_id");
    }


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

    public <DataType> ApplicationSupport.ValueObserver<DataType> observe_function(final State activeUntilState, final PocketFitApp.DataAction<DataType> observeAction){
        return application().observe_function(new PocketFitApp.DataAction<DataType>() {
            @Override
            public void data(DataType data) {
                if (state_before(activeUntilState)) observeAction.data(data);
            }
        });
    }

    public <DataType> PocketFitApp.DataAction<DataType> observe_data_action(final State activeUntilState, final PocketFitApp.DataAction<DataType> observeAction){
        return new PocketFitApp.DataAction<DataType>(){

            @Override
            public void data(DataType data) {
                if (state_before(activeUntilState)){
                    observeAction.data(data);
                }
            }
        };
    }

    public <DataType> PocketFitApp.FetchObserver<DataType> observe_data_fetch(final State activeUntilState, final PocketFitApp.DataAction<DataType> observeAction){
        return new PocketFitApp.FetchObserver<DataType>(application()) {
            @Override
            public void onFetch(DataType dataType) {
                if (state_before(activeUntilState)){
                    observeAction.data(dataType);
                }
            }
        };
    }


    public <DataType> Data.DataChangeObserver<DataType> observe_data_change(final State activeUntilState, final Data.DataChangeObserver<DataType> dataChangeObserver) {
        return new Data.DataChangeObserver<DataType>() {
            @Override
            public void onDataInvalid() {
                if (state_before(activeUntilState)){
                    dataChangeObserver.onDataInvalid();
                }
            }

            @Override
            public void onData(DataType data) {
                if (state_before(activeUntilState)){
                    dataChangeObserver.onData(data);
                }
            }
        };
    }

}
