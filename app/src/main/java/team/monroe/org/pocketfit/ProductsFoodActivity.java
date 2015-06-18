package team.monroe.org.pocketfit;

import team.monroe.org.pocketfit.fragments.MealsSelectFragment;
import team.monroe.org.pocketfit.fragments.ProductListFragment;

public class ProductsFoodActivity extends FoodActivity {

    @Override
    protected FragmentItem customize_startupFragment() {
        return new FragmentItem(ProductListFragment.class);
    }

}
