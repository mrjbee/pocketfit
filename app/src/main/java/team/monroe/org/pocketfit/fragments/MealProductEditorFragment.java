package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;

import team.monroe.org.pocketfit.FoodActivity;
import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.RoutinesActivity;
import team.monroe.org.pocketfit.presentations.Meal;
import team.monroe.org.pocketfit.presentations.MealProduct;
import team.monroe.org.pocketfit.presentations.Product;
import team.monroe.org.pocketfit.presentations.RoutineDay;
import team.monroe.org.pocketfit.presentations.RoutineExercise;
import team.monroe.org.pocketfit.view.presenter.ViewPresenter;

public class MealProductEditorFragment extends BodyFragment<FoodActivity> {

    private String mMealId;
    private String mMealProductId;
    private String mProductId;
    private MealProduct mMealProduct;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    protected String getHeaderName() {
        return "Meal Product";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_meal_product;
    }

    @Override
    public void onStart() {
        super.onStart();
        mMealId = getStringArgument("meal_id");
        mMealProductId = getStringArgument("meal_product_id");
        if (mMealProductId == null)throw new NullPointerException();
        mProductId = getStringArgument("product_id");
        application().function_getMealProduct(mMealProductId, observe_function(State.STOP, new PocketFitApp.DataAction<MealProduct>() {
            @Override
            public void data(MealProduct mealProduct) {
                mMealProduct = mealProduct;
                if (mMealProduct == null) {
                    mMealProduct = new MealProduct(mMealProductId);
                }
                if (mProductId != null && !mProductId.equals(mMealProduct.getProductId())) {
                    //changing exercise
                    application().function_getProduct(mProductId, observe_function(State.STOP, new PocketFitApp.DataAction<Product>() {
                        @Override
                        public void data(Product product) {
                            mMealProduct.product = product;
                            updateProductDetailsUI();
                        }
                    }));
                } else {
                    if (mMealProduct.product == null) throw new NullPointerException();
                    updateProductDetailsUI();
                }
            }
        }));


        view(R.id.action_change_exercise).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                owner().open_productsAsChooser(mMealId, mMealProductId, false);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mMealProduct.gram = readPositiveFloat(R.id.edit_weight);
        application().function_updateMealProduct(mMealProduct, mMealId, false, observe_function(State.ANY, new PocketFitApp.DataAction<Void>() {
            @Override
            public void data(Void data) {

            }
        }));

    }

    private void updateProductDetailsUI() {
        view_text(R.id.text_title).setText(mMealProduct.product.title);
        updateTextView(R.id.edit_weight, mMealProduct.gram);
    }

    protected Integer readPositiveInteger(int r_text) {
        Integer value;
        String text = view_text(r_text).getText().toString();
        try {
            value = Math.abs(Integer.parseInt(text));
        }catch (Exception e){
            value = null;
        }
        return value;
    }

    protected Float readPositiveFloat(int r_text) {
        Float value;
        String text = view_text(r_text).getText().toString();
        try {
            value = Math.abs(Float.parseFloat(text));
        }catch (Exception e){
            value = null;
        }
        return value;
    }

    protected void updateTextView(int r, Object value) {
        view_text(r).setText(value == null ? "" : value.toString());
    }


}
