package team.monroe.org.pocketfit;

import team.monroe.org.pocketfit.fragments.ExerciseEditorFragment;
import team.monroe.org.pocketfit.fragments.ExercisesListFragment;
import team.monroe.org.pocketfit.fragments.MealEditorFragment;
import team.monroe.org.pocketfit.fragments.MealsSelectFragment;
import team.monroe.org.pocketfit.fragments.RoutineEditorFragment;
import team.monroe.org.pocketfit.fragments.contract.ExerciseOwnerContract;

public class FoodActivity extends FragmentActivity {
    @Override
    protected FragmentItem customize_startupFragment() {
        return new FragmentItem(MealsSelectFragment.class);
    }

    @Override
    protected int customize_rootLayout() {
        return R.layout.activity_fragment_general;
    }

    public void open_MealEditor(String mealId) {
        updateBodyFragment(
                new FragmentItem(
                        MealEditorFragment.class).addArgument("meal_id", mealId),
                animation_slide_from_right()
        );
    }
}
