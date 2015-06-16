package team.monroe.org.pocketfit;

import team.monroe.org.pocketfit.fragments.ExerciseEditorFragment;
import team.monroe.org.pocketfit.fragments.ExercisesListFragment;
import team.monroe.org.pocketfit.fragments.MealsSelectFragment;
import team.monroe.org.pocketfit.fragments.contract.ExerciseOwnerContract;

public class FoodSelectActivity extends FragmentActivity {
    @Override
    protected FragmentItem customize_startupFragment() {
        return new FragmentItem(MealsSelectFragment.class);
    }

    @Override
    protected int customize_rootLayout() {
        return R.layout.activity_fragment_general;
    }

}
