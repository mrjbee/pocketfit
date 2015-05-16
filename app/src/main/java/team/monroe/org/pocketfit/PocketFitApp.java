package team.monroe.org.pocketfit;

import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.data.Data;

import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.uc.CreateEmptyRoutine;
import team.monroe.org.pocketfit.uc.GetRoutineById;
import team.monroe.org.pocketfit.uc.UpdateRoutine;

public class PocketFitApp extends ApplicationSupport<PocketFitModel>{

    private Data<Routine> data_activeRoutine;

    @Override
    protected PocketFitModel createModel() {
        return new PocketFitModel(getApplicationContext());
    }

    @Override
    protected void onPostCreate() {
        data_activeRoutine = new Data<Routine>(Routine.class, model()) {
            @Override
            protected Routine provideData() {
                String routineId = getSetting(Settings.ACTIVE_ROUTINE_ID);
                if (routineId == null) return null;
                Routine routine = model().execute(GetRoutineById.class, routineId);
                return routine;
            }
        };
    }


    public void error(Data.FetchError fetchError) {
        throw new RuntimeException(new Data.FetchException(fetchError));
    }

    public void error(Throwable exception) {
        throw new RuntimeException(exception);
    }

    public void error(String message) {
        error(new IllegalStateException("Logic error:"+message));
    }

    public Data<Routine> data_activeRoutine() {
        return data_activeRoutine;
    }


    public <DataType> ValueObserver<DataType> observe_function(final DataAction<DataType> dataAction) {
        return new ValueObserver<DataType>() {
            @Override
            public void onSuccess(DataType value) {
                dataAction.data(value);
            }

            @Override
            public void onFail(Throwable exception) {
                error(exception);
            }
        };
    }

    public void function_createEmptyRoutine(ValueObserver<Routine> routineValueObserver) {
        fetchValue(CreateEmptyRoutine.class,null,new NoOpValueAdapter<Routine>(),routineValueObserver);
    }

    public void function_getRoutine(String routineId, ValueObserver<Routine> observer) {
        fetchValue(GetRoutineById.class, routineId, new NoOpValueAdapter<Routine>(), observer);
    }

    public void function_updateRoutine(Routine routine, ValueObserver<Routine> observer) {
        fetchValue(UpdateRoutine.class, routine, new NoOpValueAdapter<Routine>(), observer );
    }




    public static abstract class FetchObserver<ValueType> implements Data.FetchObserver<ValueType> {
        final PocketFitApp owner;

        protected FetchObserver(PocketFitApp app) {
            this.owner = app;
        }

        @Override
        public void onError(Data.FetchError fetchError) {
              owner.error(fetchError);
        }
    }

    public static interface DataAction<DataType>{
        public void data(DataType data);
    }


}
