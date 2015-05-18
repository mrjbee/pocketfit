package team.monroe.org.pocketfit.fragments;

import org.monroe.team.android.box.data.Data;

import java.util.List;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.presentations.Exercise;

public class ExercisesListFragment extends GenericListFragment<Exercise> {

    private Data.DataChangeObserver<List> observer_exercise;

    @Override
    protected String item_text(Exercise item) {
        return item.description;
    }

    @Override
    protected String item_subCaption(Exercise item) {
        return item.type.human;
    }

    @Override
    protected String item_caption(Exercise item) {
        return item.title;
    }

    @Override
    protected String getNewItemCaption() {
        return "Do you want to create new exercise?";
    }

    @Override
    protected boolean isHeaderSecondary() {
        return getBoolArgument("chooserMode");
    }

    @Override
    protected String getHeaderName() {
        return !getBoolArgument("chooserMode")?"Exercises":"Choose Exercise";
    }

    @Override
    public void onStart() {
        super.onStart();
        observer_exercise = observe_data_change(State.STOP, new Data.DataChangeObserver<List>() {
            @Override
            public void onDataInvalid() {
                fetch_Exercises();
            }

            @Override
            public void onData(List list) {
            }
        });
        application().data_exercises().addDataChangeObserver(observer_exercise);
        fetch_Exercises();
    }

    @Override
    public void onStop() {
        super.onStop();
        application().data_exercises().removeDataChangeObserver(observer_exercise);
    }

    private void fetch_Exercises() {
        application().data_exercises().fetch(true, observe_data_fetch(State.STOP, new PocketFitApp.DataAction<List>() {
            @Override
            public void data(List data) {
                updateItems(data);
            }
        }));
    }
}
