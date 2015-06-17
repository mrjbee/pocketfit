package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.monroe.team.android.box.app.FragmentSupport;

import team.monroe.org.pocketfit.FoodActivity;
import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.RoutinesActivity;
import team.monroe.org.pocketfit.presentations.Meal;
import team.monroe.org.pocketfit.presentations.MealProduct;
import team.monroe.org.pocketfit.view.presenter.ListViewPresenter;

public class MealEditorFragment extends BodyFragment<FoodActivity> {


    private Meal mMeal;
    private ListViewPresenter<MealProduct> listViewPresenter;

    @Override
    protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    protected String getHeaderName() {
        return "Edit Meal";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_meal;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view(R.id.button_add_product).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = view_text(R.id.edit_title).getText().toString();
                if (!title.trim().isEmpty()){
                    application().function_createId("mp", observe_function(State.STOP, new PocketFitApp.DataAction<String>() {
                        @Override
                        public void data(String data) {
                            ownerContract(FoodActivity.class).open_productsAsChooser(mMeal.id, data, true);
                        }
                    }));
                }else{
                    Toast.makeText(getActivity(), "Please add meal title first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        view(R.id.image_select_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  ownerContract(RoutinesActivity.class).performImageSelection();
            }
        });

        listViewPresenter = new ListViewPresenter<MealProduct>(view(R.id.panel_days, ViewGroup.class)) {
            @Override
            protected View data_to_view(int index, final MealProduct mealProduct, final ViewGroup owner, LayoutInflater inflater) {
                View view = inflater.inflate(R.layout.item_product,owner, false);
                ((TextView)view.findViewById(R.id.item_caption)).setText(mealProduct.product.title);
                ((TextView)view.findViewById(R.id.item_text)).setText(mealProduct.gram+" gram");
                ((TextView)view.findViewById(R.id.item_sub_caption)).setText(mealProduct.calories()+" cal");

                view.findViewById(R.id.item_action).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        owner().open_mealProductEditor(mMeal.id, mealProduct.id);
                    }
                });
                view.findViewById(R.id.item_trash).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        application().function_updateMealProduct(mealProduct,mMeal.id,true,observe_function(State.STOP, new PocketFitApp.DataAction<Void>() {
                            @Override
                            public void data(Void data) {
                                updateMeal(mMeal.id);
                            }
                        }));
                    }
                });
                return view;
            }

            @Override
            protected String data_to_id(MealProduct mealProduct) {
                return mealProduct.id;
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        final String mealId = super.getStringArgument("meal_id");
        if (mealId == null){
            application().error("No meal id");
        }
        updateMeal(mealId);
    }

    private void updateMeal(final String mealId) {
        application().function_getMeal(mealId,observe_function(State.STOP, new PocketFitApp.DataAction<Meal>() {
            @Override
            public void data(Meal data) {
               mMeal = data;
               if (data == null){
                 mMeal = new Meal(mealId);
               }
               view_text(R.id.edit_title).setText(mMeal.title);
                view_text(R.id.text_calories).setText(mMeal.calories()+" cal");
                view_text(R.id.text_nutrition).setText("Fats " + mMeal.fats()+" Carbs "+mMeal.carbs()+" Protein "+mMeal.protein());
                view_text(R.id.text_total_products).setText(mMeal.mealProducts.size()+"");
                listViewPresenter.synchronizeItems(mMeal.mealProducts);
            }
        }));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMeal != null){
            mMeal.title = view_text(R.id.edit_title).getText().toString();
            application().function_updateMeal(mMeal, observe_function(State.ANY, new PocketFitApp.DataAction<Void>() {
                @Override
                public void data(Void nothing) {
                    //Do nothing here
                }
            }));
        }
    }
}
