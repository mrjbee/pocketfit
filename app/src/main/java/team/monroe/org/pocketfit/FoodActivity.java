package team.monroe.org.pocketfit;

import java.util.HashMap;
import java.util.Map;

import team.monroe.org.pocketfit.fragments.ExerciseEditorFragment;
import team.monroe.org.pocketfit.fragments.ExercisesListFragment;
import team.monroe.org.pocketfit.fragments.MealEditorFragment;
import team.monroe.org.pocketfit.fragments.MealsSelectFragment;
import team.monroe.org.pocketfit.fragments.ProductEditorFragment;
import team.monroe.org.pocketfit.fragments.ProductListFragment;
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

    public void onProductSelected(String productId) {
        Map<String, String> results = new HashMap<>();
        results.put("product_id", productId);
        onChooseResult(results);
    }

    public void editProduct(String productId) {
        updateBodyFragment(new FragmentItem(ProductEditorFragment.class)
                .addArgument("product_id", productId), animation_slide_from_right());
    }

    public void open_mealEditor(String mealId) {
        updateBodyFragment(
                new FragmentItem(
                        MealEditorFragment.class).addArgument("meal_id", mealId),
                animation_slide_from_right()
        );
    }

    public void open_productsAsChooser(String mealId, boolean moveToProductMealConfiguration) {

        FragmentItem fragmentBackStackItem = new FragmentItem(ProductListFragment.class)
                .addArgument("meal_id", mealId)
                .addArgument("chooserMode", "true");
        if (moveToProductMealConfiguration){
            //fragmentBackStackItem.addArgument("fragment_class", RoutineExerciseEditorFragment.class);
        }

        if (moveToProductMealConfiguration){
            updateBodyFragment(fragmentBackStackItem, animation_slide_from_right());
        }else {
            updateBodyFragment(fragmentBackStackItem, animation_flip_in());
        }
    }
}
