package team.monroe.org.pocketfit.fragments;

import org.monroe.team.android.box.data.Data;

import java.util.List;

import team.monroe.org.pocketfit.FragmentActivity;
import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.fragments.contract.ExerciseOwnerContract;
import team.monroe.org.pocketfit.presentations.Exercise;

public class ExercisesListFragment extends GenericListFragment<Exercise, FragmentActivity> {

    private Data.DataChangeObserver<List> observer_exercise;

    @Override
    protected void onNewItemClick() {
        application().function_createId("exercise",observe_function(State.STOP, new PocketFitApp.DataAction<String>() {
            @Override
            public void data(String id) {
                exerciseOwner().editExercise(id);
            }
        }));
    }

    @Override
    protected void onItemClick(Exercise exercise) {
        if (getBoolArgument("chooserMode")){
            exerciseOwner().onExerciseSelected(exercise.id);
        }else {
            exerciseOwner().editExercise(exercise.id);
        }
    }

    @Override
    protected void onItemEdit(Exercise exercise) {
        exerciseOwner().editExercise(exercise.id);
    }

    @Override
    protected boolean isInlineEditAllowed() {
        return getBoolArgument("chooserMode");
    }

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

    private final ExerciseOwnerContract exerciseOwner(){
        return (ExerciseOwnerContract) activity();
    }
}
