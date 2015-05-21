package team.monroe.org.pocketfit.fragments;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.app.FragmentSupport;
import org.monroe.team.android.box.data.Data;

import java.io.Serializable;

import team.monroe.org.pocketfit.FragmentActivity;

import team.monroe.org.pocketfit.PocketFitApp;

public abstract class BodyFragment<OwnerActivity extends FragmentActivity>  extends FragmentSupport<PocketFitApp> {

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
                ((FragmentActivity) activity()).header(getHeaderName(), isHeaderSecondary());
            }else {
                ((FragmentActivity) activity()).animateHeader(getHeaderName(), isHeaderSecondary());
            }
        }
        headerUpdateRequest = HeaderUpdateRequest.NOT_SET;
    }

    protected abstract boolean isHeaderSecondary();
    protected abstract String getHeaderName();

    public void onImageResult(Uri uri) {}

    public String getStringArgument(String key) {
       if (getArguments() == null)return null;
       return getArguments().getString(key);
    }

    public android.view.View build_HeaderActionsView(ViewGroup actionPanel, LayoutInflater layoutInflater) {
        return null;
    }

    public boolean getBoolArgument(String chooserMode) {
        if (getArguments() == null) return false;
        return Boolean.parseBoolean(getArguments().getString(chooserMode,"false"));
    }


    public static enum HeaderUpdateRequest implements Serializable {
        SET, NOT_SET, ANIMATE
    }

    public OwnerActivity owner(){
        return (OwnerActivity) activity();
    }

    public <OwnerType extends FragmentActivity> OwnerType owner(Class<OwnerType> ownerClass){
        return (OwnerType) activity();
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
