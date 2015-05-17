package team.monroe.org.pocketfit;

import android.graphics.Bitmap;
import android.util.Pair;

import org.monroe.team.android.box.BitmapUtils;
import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.data.Data;
import org.monroe.team.android.box.utils.FileUtils;
import org.monroe.team.corebox.services.BackgroundTaskManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;

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

    public void function_updateRoutine(Routine routine, ValueObserver<Void> observer) {
        fetchValue(UpdateRoutine.class, routine, new NoOpValueAdapter<Void>(){
            @Override
            public Void adapt(Void value) {
                data_routines().invalidate();
                return super.adapt(value);
            }
        }, observer );
    }


    public void saveImage(final InputStream fromIs, final DataAction<String> imageIdAction) {
        model().usingService(BackgroundTaskManager.class).execute(new Callable<String>() {
            @Override
            public String call() throws Exception {
                File saveFile =  FileUtils.storageFile(getApplicationContext(), FileUtils.timeName());
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(saveFile);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                byte[] buffer = new byte[1024];
                int length;

                try {
                    while((length = fromIs.read(buffer)) > 0){
                        outputStream.write(buffer, 0, length);
                    }
                    return saveFile.getAbsolutePath();
                } catch (IOException e) {
                    if (outputStream != null){
                        try {
                            outputStream.close();
                            outputStream = null;
                        } catch (IOException e1) {}
                        saveFile.delete();
                    }
                    throw new RuntimeException(e);
                } finally {
                    if (outputStream != null){
                        try {
                            outputStream.flush();
                        } catch (IOException e) {throw new RuntimeException("Flush error", e);}
                        try {
                            outputStream.close();
                        } catch (IOException e) {}
                    }
                    if (fromIs != null){
                        try {
                            fromIs.close();
                        } catch (IOException e) {}
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
