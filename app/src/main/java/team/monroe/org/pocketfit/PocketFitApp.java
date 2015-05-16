package team.monroe.org.pocketfit;

import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.data.Data;

import java.util.List;

import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.uc.CreateRoutineId;
import team.monroe.org.pocketfit.uc.GetRoutineById;
import team.monroe.org.pocketfit.uc.GetRoutineList;
import team.monroe.org.pocketfit.uc.UpdateRoutine;

public class PocketFitApp extends ApplicationSupport<PocketFitModel>{

    private Data<Routine> data_activeRoutine;
    private Data<List> data_routines;

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
                if (routineId == null) return new Routine(null);
                Routine routine = model().execute(GetRoutineById.class, routineId);
                return routine;
            }
        };

        data_routines = new Data<List>(List.class, model()) {
            @Override
            protected List<Routine> provideData() {
                return model().execute(GetRoutineList.class,null);
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

    public Data<List> data_routines() {
        return data_routines;
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

    public void function_createEmptyRoutine(ValueObserver<String> routineValueObserver) {
        fetchValue(CreateRoutineId.class,null,new NoOpValueAdapter<String>(){
            @Override
            public String adapt(String value) {
                data_routines().invalidate();
                return super.adapt(value);
            }
        }, routineValueObserver);
    }

    public void function_getRoutine(String routineId, ValueObserver<Routine> observer) {
        fetchValue(GetRoutineById.class, routineId, new NoOpValueAdapter<Routine>(), observer);
    }

    public void function_updateRoutine(Routine routine, ValueObserver<Routine> observer) {
        fetchValue(UpdateRoutine.class, routine, new NoOpValueAdapter<Routine>(){
            @Override
            public Routine adapt(Routine value) {
                data_routines().invalidate();
                return super.adapt(value);
            }
        }, observer );
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
