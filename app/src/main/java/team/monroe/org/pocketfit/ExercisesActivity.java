package team.monroe.org.pocketfit;

import android.content.Intent;

import team.monroe.org.pocketfit.fragments.ExerciseEditorFragment;
import team.monroe.org.pocketfit.fragments.ExercisesListFragment;
import team.monroe.org.pocketfit.fragments.NoActiveRoutineFragment;
import team.monroe.org.pocketfit.fragments.contract.ExerciseOwnerContract;

public class ExercisesActivity extends FragmentActivity implements ExerciseOwnerContract {
    @Override
    protected FragmentItem customize_startupFragment() {
        return new FragmentItem(ExercisesListFragment.class);
    }

    @Override
    protected int customize_rootLayout() {
        return R.layout.activity_fragment_general;
    }

    @Override
    public void editExercise(String exerciseId) {
        updateBodyFragment(new FragmentItem(ExerciseEditorFragment.class)
                .addArgument("exercise_id", exerciseId), change_slide_from_right());
    }

    @Override
    public void onExerciseSelected(String results) {
        throw new IllegalStateException("Not Supported");
    }
}
