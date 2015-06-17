package team.monroe.org.pocketfit;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import team.monroe.org.pocketfit.fragments.BodyFragment;
import team.monroe.org.pocketfit.fragments.ExerciseEditorFragment;
import team.monroe.org.pocketfit.fragments.ExercisesListFragment;
import team.monroe.org.pocketfit.fragments.MealEditorFragment;
import team.monroe.org.pocketfit.fragments.MealProductEditorFragment;
import team.monroe.org.pocketfit.fragments.MealsSelectFragment;
import team.monroe.org.pocketfit.fragments.ProductEditorFragment;
import team.monroe.org.pocketfit.fragments.ProductListFragment;
import team.monroe.org.pocketfit.fragments.RoutineDayEditorFragment;
import team.monroe.org.pocketfit.fragments.RoutineEditorFragment;
import team.monroe.org.pocketfit.fragments.contract.ExerciseOwnerContract;

public class FoodActivity extends FragmentActivity {
    private static final int PICK_IMAGE = 300;

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

    public void open_productsAsChooser(String mealId, String mealProductId, boolean moveToProductMealConfiguration) {

        FragmentItem fragmentBackStackItem = new FragmentItem(ProductListFragment.class)
                .addArgument("meal_id", mealId)
                .addArgument("meal_product_id", mealProductId)
                .addArgument("chooserMode", "true");
        if (moveToProductMealConfiguration){
            fragmentBackStackItem.addArgument("fragment_class", MealProductEditorFragment.class);
        }

        if (moveToProductMealConfiguration){
            updateBodyFragment(fragmentBackStackItem, animation_slide_from_right());
        }else {
            updateBodyFragment(fragmentBackStackItem, animation_flip_in());
        }
    }

    public void open_mealProductEditor(String mealId, String mealProductId) {
        updateBodyFragment(new FragmentItem(MealProductEditorFragment.class)
                .addArgument("meal_id", mealId)
                .addArgument("meal_product_id", mealProductId),
                animation_slide_from_right()
        );
    }

    public void performImageSelection() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra("return-data", false);
        try {
            startActivityForResult(Intent.createChooser(intent, "Pick cover"), PICK_IMAGE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application for image selection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_IMAGE && data != null && resultCode == Activity.RESULT_OK) {
            Uri _uri = data.getData();
            if (_uri == null) return;
            BodyFragment bodyFragment = getBodyFragment();
            bodyFragment.onImageResult(_uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
