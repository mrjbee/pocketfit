package team.monroe.org.pocketfit.fragments;

import org.monroe.team.android.box.app.ActivitySupport;
import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.app.FragmentSupport;
import org.monroe.team.android.box.data.Data;

import team.monroe.org.pocketfit.PocketFitApp;

public abstract class AppFragment<OwnerActivity extends ActivitySupport<PocketFitApp>>  extends FragmentSupport<PocketFitApp> {


    public String getStringArgument(String key) {
        if (getArguments() == null)return null;
        return getArguments().getString(key);
    }


    public boolean getBoolArgument(String key) {
        if (getArguments() == null) return false;
        return Boolean.parseBoolean(getArguments().getString(key,"false"));
    }

    public OwnerActivity owner(){
        return (OwnerActivity) activity();
    }

    public <ContractType> ContractType ownerContract(Class<ContractType> contract){
        return (ContractType) activity();
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
