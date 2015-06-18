package team.monroe.org.pocketfit;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Pair;

import org.monroe.team.android.box.BitmapUtils;
import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.data.Data;
import org.monroe.team.android.box.data.RangeDataProvider;
import org.monroe.team.android.box.utils.AndroidLogImplementation;
import org.monroe.team.android.box.utils.FileUtils;
import org.monroe.team.corebox.log.L;
import org.monroe.team.corebox.services.BackgroundTaskManager;
import org.monroe.team.corebox.utils.DateUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import team.monroe.org.pocketfit.presentations.AteMeal;
import team.monroe.org.pocketfit.presentations.Exercise;
import team.monroe.org.pocketfit.presentations.Meal;
import team.monroe.org.pocketfit.presentations.MealProduct;
import team.monroe.org.pocketfit.presentations.Product;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;
import team.monroe.org.pocketfit.presentations.RoutineExercise;
import team.monroe.org.pocketfit.presentations.RoutineSchedule;
import team.monroe.org.pocketfit.uc.CreateId;
import team.monroe.org.pocketfit.uc.DeleteEatMeal;
import team.monroe.org.pocketfit.uc.DeleteMeal;
import team.monroe.org.pocketfit.uc.EatMeal;
import team.monroe.org.pocketfit.uc.GetActiveRoutineSchedule;
import team.monroe.org.pocketfit.uc.GetAteMealByDate;
import team.monroe.org.pocketfit.uc.GetExerciseById;
import team.monroe.org.pocketfit.uc.GetExerciseList;
import team.monroe.org.pocketfit.uc.GetMealById;
import team.monroe.org.pocketfit.uc.GetMealList;
import team.monroe.org.pocketfit.uc.GetMealProductById;
import team.monroe.org.pocketfit.uc.GetProductById;
import team.monroe.org.pocketfit.uc.GetProductList;
import team.monroe.org.pocketfit.uc.GetRoutineById;
import team.monroe.org.pocketfit.uc.GetRoutineDayById;
import team.monroe.org.pocketfit.uc.GetRoutineExerciseById;
import team.monroe.org.pocketfit.uc.GetRoutineList;
import team.monroe.org.pocketfit.uc.IsExerciseSafeToChange;
import team.monroe.org.pocketfit.uc.UpdateExercise;
import team.monroe.org.pocketfit.uc.UpdateMeal;
import team.monroe.org.pocketfit.uc.UpdateMealProduct;
import team.monroe.org.pocketfit.uc.UpdateProduct;
import team.monroe.org.pocketfit.uc.UpdateRoutine;
import team.monroe.org.pocketfit.uc.UpdateRoutineDay;
import team.monroe.org.pocketfit.uc.UpdateRoutineExercise;

public class PocketFitApp extends ApplicationSupport<PocketFitModel>{

    static {
        L.setup(new AndroidLogImplementation());
    }

    private Data<Routine> data_activeRoutine;
    private Data<Pair> data_runningTraining;
    private Data<List> data_routines;
    private Data<List> data_exercises;
    private Data<RoutineSchedule> data_activeRoutineSchedule;
    private RangeDataProvider<Date,List<AteMeal>> data_range_ateMeal;

    private ServiceConnection mServiceConnection;
    private TrainingExecutionService.TrainingExecutionManager mTrainingExecutionManager;
    private Data<List> data_products;
    private Data<List> data_meals;
    private Data<Integer> data_calories_limit;

    @Override
    protected PocketFitModel createModel() {
        return new PocketFitModel(getApplicationContext());
    }

    @Override
    protected void onPostCreate() {

        data_activeRoutineSchedule = new Data<RoutineSchedule>(RoutineSchedule.class, model()) {
            @Override
            protected RoutineSchedule provideData() {
                RoutineSchedule routineSchedule = model().execute(GetActiveRoutineSchedule.class, null);
                if (routineSchedule == null) routineSchedule = new RoutineSchedule(null, -1, DateUtils.today());
                return routineSchedule;
            }
        };

        data_activeRoutine = new Data<Routine>(Routine.class, model()) {
            @Override
            protected Routine provideData() {
                String routineId = getSetting(Settings.ID_ACtIVE_ROUTINE);
                if (routineId == null) return new Routine(null);
                Routine routine = model().execute(GetRoutineById.class, routineId);
                return routine;
            }
        };

        data_activeRoutine().addDataChangeObserver(new Data.DataChangeObserver<Routine>() {
            @Override
            public void onDataInvalid() {
                data_activeRoutineSchedule.invalidate();
            }

            @Override
            public void onData(Routine routine) {

            }
        });

        data_routines = new Data<List>(List.class, model()) {
            @Override
            protected List<Routine> provideData() {
                return model().execute(GetRoutineList.class,null);
            }
        };

        data_meals = new Data<List>(List.class, model()) {
            @Override
            protected List<Meal> provideData() {
                return model().execute(GetMealList.class,null);
            }
        };

        data_exercises = new Data<List>(List.class, model()) {
            @Override
            protected List<Exercise> provideData() {
                return model().execute(GetExerciseList.class, null);
            }
        };

        data_products = new Data<List>(List.class, model()) {
            @Override
            protected List<Product> provideData() {
                return model().execute(GetProductList.class, null);
            }
        };

        data_calories_limit = new Data<Integer>(model()) {
            @Override
            protected Integer provideData() {
                return getSetting(Settings.CALORIES_DAY_LIMIT);
            }
        };

        data_runningTraining = new Data<Pair> (Pair.class, model()) {
            @Override
            protected Pair provideData() {
                if (!isTrainingRunning()) return new Pair(null,null);
                String routineId = mTrainingExecutionManager.getRoutineId();
                Routine routine = model().execute(GetRoutineById.class, routineId);
                return new Pair(routine, routine.getRoutineDay(mTrainingExecutionManager.getRoutineDayId()));
            }
        };

        data_range_ateMeal = new RangeDataProvider<Date, List<AteMeal>>() {

            @Override
            protected Data<List<AteMeal>> buildData(final Date date) {
                return new Data<List<AteMeal>>(model()) {
                    @Override
                    protected List<AteMeal> provideData() {
                        return model().execute(GetAteMealByDate.class, date);
                    }
                };
            }


            DateFormat dateFormat = new SimpleDateFormat("dd, MMMM yyyy");
            @Override
            protected String convertToStringKey(Date date) {
                return dateFormat.format(date);
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
    public Data<List> data_meals() {
        return data_meals;
    }

    public Data<List> data_exercises() {
        return data_exercises;
    }

    public Data<List> data_products() {
        return data_products;
    }

    public Data<RoutineSchedule> data_activeRoutineSchedule() {
        return data_activeRoutineSchedule;
    }

    public Data<Pair> data_runningTraining() {
        return data_runningTraining;
    }

    public Data<List<AteMeal>> data_ate_meal(Date date) {
        return data_range_ateMeal.getOrCreate(date);
    }

    public Data<Integer> data_calories_limit() {
        return data_calories_limit;
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

    public void function_createId(String prefix, ValueObserver<String> routineValueObserver) {
        fetchValue(CreateId.class, prefix,new NoOpValueAdapter<String>(){
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

    public void function_getMeal(String mealId, ValueObserver<Meal> observer) {
        fetchValue(GetMealById.class, mealId, new NoOpValueAdapter<Meal>(), observer);
    }

    public void function_updateRoutine(Routine routine, ValueObserver<Void> observer) {
        fetchValue(UpdateRoutine.class, routine, new NoOpValueAdapter<Void>(){
            @Override
            public Void adapt(Void value) {
                data_routines().invalidate();
                data_activeRoutine().invalidate();
                return super.adapt(value);
            }
        }, observer );
    }

    public void function_updateMeal(Meal meal, ValueObserver<Void> observer) {
        fetchValue(UpdateMeal.class, meal, new NoOpValueAdapter<Void>(){
            @Override
            public Void adapt(Void value) {
                data_meals.invalidate();
                return super.adapt(value);
            }
        }, observer );
    }

    public void function_getRoutineDay(String routineDayId, ValueObserver<RoutineDay> observer) {
        fetchValue(GetRoutineDayById.class, routineDayId, new NoOpValueAdapter<RoutineDay>(), observer);
    }

    public void function_updateRoutineDay(RoutineDay mRoutineDay, String routineId) {
        function_updateRoutineDay(mRoutineDay, routineId, UpdateRoutineDay.RoutineDayUpdate.INDEX_ADD_LAST, null);
    }

    public void function_updateRoutineDay(RoutineDay mRoutineDay, String routineId, int index, final DataAction<Void> onDone) {
        fetchValue(UpdateRoutineDay.class, new UpdateRoutineDay.RoutineDayUpdate(mRoutineDay, routineId, index), new NoOpValueAdapter<Void>(){},observe_function(new DataAction<Void>() {
            @Override
            public void data(Void data) {
                data_activeRoutine().invalidate();
                if (onDone != null) onDone.data(null);
            }
        }));
    }


    public void saveImage(final InputStream fromIs, final DataAction<String> imageIdAction) {
        model().usingService(BackgroundTaskManager.class).execute(new Callable<String>() {
            @Override
            public String call() throws Exception {
                File saveFile = FileUtils.storageFile(getApplicationContext(), FileUtils.timeName());
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(saveFile);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                byte[] buffer = new byte[1024];
                int length;

                try {
                    while ((length = fromIs.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                    return saveFile.getAbsolutePath();
                } catch (IOException e) {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                            outputStream = null;
                        } catch (IOException e1) {
                        }
                        saveFile.delete();
                    }
                    throw new RuntimeException(e);
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.flush();
                        } catch (IOException e) {
                            throw new RuntimeException("Flush error", e);
                        }
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                        }
                    }
                    if (fromIs != null) {
                        try {
                            fromIs.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        }, new BackgroundTaskManager.TaskCompletionNotificationObserver<String>() {
            @Override
            public void onSuccess(final String imagePath) {
                model().ui(new Runnable() {
                    @Override
                    public void run() {
                        imageIdAction.data(imagePath);
                    }
                });
            }

            @Override
            public void onFails(final Exception e) {
                model().ui(new Runnable() {
                    @Override
                    public void run() {
                        error(e);
                    }
                });
            }
        });
    }

    public void loadToBitmap(final String imageId, final float reqHeight, final float reqWidth, final DataAction<Pair<String, Bitmap>> observer) {
        model().usingService(BackgroundTaskManager.class).execute(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                File file = new File(imageId);
                if (!file.exists()){
                    throw new RuntimeException("File not exists = "+imageId);
                }
                return BitmapUtils.decodeBitmap(BitmapUtils.fromFile(file),
                        (int)reqWidth,
                        (int)reqHeight);
            }
        }, new BackgroundTaskManager.TaskCompletionNotificationObserver<Bitmap>() {
            @Override
            public void onSuccess(final Bitmap bitmap) {
                model().ui(new Runnable() {
                    @Override
                    public void run() {
                        observer.data(new Pair<String, Bitmap>(imageId, bitmap));
                    }
                });
            }

            @Override
            public void onFails(final Exception e) {
                error(e);
            }
        });
    }

    public void function_getExercise(String exerciseId, ValueObserver<Exercise> observer) {
        fetchValue(GetExerciseById.class, exerciseId, new NoOpValueAdapter<Exercise>(), observer);
    }

    public void function_getProduct(String productId, ValueObserver<Product> observer) {
        fetchValue(GetProductById.class, productId, new NoOpValueAdapter<Product>(), observer);
    }

    public void function_getExerciseTypeEditable(String exerciseId, ValueObserver<Boolean> observer) {
        fetchValue(IsExerciseSafeToChange.class, exerciseId, new NoOpValueAdapter<Boolean>() ,observer);
    }

    public void function_updateExercise(Exercise mExercise, ValueObserver<Void> voidValueObserver) {
        fetchValue(UpdateExercise.class, mExercise, new NoOpValueAdapter<Void>(){
            @Override
            public Void adapt(Void value) {
                data_exercises().invalidate();
                return super.adapt(value);
            }
        } ,voidValueObserver);
    }

    public void function_updateProduct(Product product, ValueObserver<Void> voidValueObserver) {
        fetchValue(UpdateProduct.class, product, new NoOpValueAdapter<Void>(){
            @Override
            public Void adapt(Void value) {
                data_products().invalidate();
                return super.adapt(value);
            }
        } ,voidValueObserver);
    }

    public void function_getRoutineExercise(String routineExerciseId, ValueObserver<RoutineExercise> routineExerciseValueObserver) {
        fetchValue(GetRoutineExerciseById.class,routineExerciseId,new NoOpValueAdapter<RoutineExercise>(),routineExerciseValueObserver);
    }

    public void function_getMealProduct(String mealProductId, ValueObserver<MealProduct> observer) {
        fetchValue(GetMealProductById.class, mealProductId, new NoOpValueAdapter<MealProduct>(), observer);
    }

    public void function_updateRoutineExercise(RoutineExercise routineExercise, String dayId, int index, ValueObserver<Void> observer) {
        fetchValue(UpdateRoutineExercise.class, new UpdateRoutineExercise.RoutineExerciseUpdate(routineExercise, dayId, index), new NoOpValueAdapter<Void>(),observer);
    }

    public void function_updateMealProduct(MealProduct mealProduct, String mealId, boolean remove, ValueObserver<Void> observer) {
        fetchValue(UpdateMealProduct.class, new UpdateMealProduct.Update(mealProduct, mealId,remove), new NoOpValueAdapter<Void>(),observer);
    }


    public boolean hasActiveRoutine() {
        return getSetting(Settings.ID_ACtIVE_ROUTINE) != null;
    }

    public boolean isTrainingRunning() {
        return mTrainingExecutionManager != null;
    }


    public void startTraining(final Routine routine, final RoutineDay trainingDay, final Runnable postRunAction) {
        if(mServiceConnection != null) throw new IllegalStateException("Already under execution");

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mTrainingExecutionManager = (TrainingExecutionService.TrainingExecutionManager) service;
                mTrainingExecutionManager.startExecution(routine, trainingDay);
                data_runningTraining().invalidate();
                if (postRunAction != null) postRunAction.run();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mTrainingExecutionManager = null;
                mServiceConnection = null;
            }
        };
        bindService(new Intent(this, TrainingExecutionService.class),mServiceConnection,BIND_AUTO_CREATE);
    }

    public void stopTraining(boolean completelyDone) {

        List<TrainingExecutionService.TrainingPlan.ExerciseResult> exerciseResults = getTrainingPlan().getResultRecords();

        if (completelyDone || !exerciseResults.isEmpty()) {
            String routineId = getTrainingPlan().getRoutine().id;
            String routineDayId = getTrainingPlan().getRoutineDay().id;
            if (hasActiveRoutine()) {
                String activeRoutineId = getSetting(Settings.ID_ACtIVE_ROUTINE);
                if (routineId.equals(activeRoutineId)) {
                    setSetting(Settings.ID_ACTIVE_ROUTINE_LAST_DAY, routineDayId);
                    setSetting(Settings.DATE_ACTIVE_ROUTINE_LAST_TRAINING, DateUtils.now().getTime());
                }
            }
        }

        data_activeRoutineSchedule().invalidate();

        //TODO: store exercise results
        if (completelyDone){
            //TODO: store routine results
        }

        cancelTraining();
    }

    public void cancelTraining() {
        mTrainingExecutionManager.stopExecution();
        unbindService(mServiceConnection);
        mTrainingExecutionManager = null;
        mServiceConnection = null;
    }

    public Pair<String, String> getTrainingIds() {
        return new Pair<>(mTrainingExecutionManager.getRoutineId(), mTrainingExecutionManager.getRoutineDayId());
    }

    public Routine getTrainingRoutine() {
        return mTrainingExecutionManager.getRoutine();
    }

    public TrainingExecutionService.TrainingPlan getTrainingPlan() {
        if (mTrainingExecutionManager == null) return null;
        return mTrainingExecutionManager.getTrainingPlan();
    }


    public void function_deleteAteMeal(AteMeal meal, final FetchObserver<Void> fetchObserver) {
        fetchValue(DeleteEatMeal.class,meal, new NoOpValueAdapter<Void>(){
            @Override
            public Void adapt(Void value) {
                Data<List<AteMeal>> data = data_range_ateMeal.get(DateUtils.today());
                if (data != null){
                    data.invalidate();
                }
                return null;
            }
        } ,new ValueObserver<Void>() {
            @Override
            public void onSuccess(Void value) {
                fetchObserver.onFetch(null);
            }

            @Override
            public void onFail(Throwable exception) {
                fetchObserver.onError(new Data.ExceptionFetchError(exception));
            }
        });
    }
    public void function_eatMeal(Meal meal, ValueObserver<AteMeal> observer) {
        fetchValue(EatMeal.class,meal, new NoOpValueAdapter<AteMeal>(){
            @Override
            public AteMeal adapt(AteMeal value) {
                Data<List<AteMeal>> data = data_range_ateMeal.get(DateUtils.today());
                if (data != null){
                    data.invalidate();
                }
                return super.adapt(value);
            }
        } ,observer);
    }

    public void updateCaloriesLimit(int limitValue) {
        setSetting(Settings.CALORIES_DAY_LIMIT, limitValue);
        data_calories_limit().invalidate();
    }

    public void function_removeMeal(String mealId, ValueObserver<Boolean> observer) {
        fetchValue(DeleteMeal.class, mealId, new NoOpValueAdapter<Boolean>(){
            @Override
            public Boolean adapt(Boolean value) {
                data_meals().invalidate();
                data_range_ateMeal.invalidateAll();
                return super.adapt(value);
            }
        }, observer);
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
