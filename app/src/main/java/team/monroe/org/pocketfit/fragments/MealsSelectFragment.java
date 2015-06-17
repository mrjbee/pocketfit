package team.monroe.org.pocketfit.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.monroe.team.android.box.app.FragmentSupport;
import org.monroe.team.android.box.app.ui.GenericListViewAdapter;
import org.monroe.team.android.box.app.ui.GetViewImplementation;
import org.monroe.team.android.box.app.ui.SlideTouchGesture;
import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;
import org.monroe.team.android.box.data.Data;
import org.monroe.team.android.box.utils.DisplayUtils;

import java.util.List;

import team.monroe.org.pocketfit.FoodActivity;
import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.Meal;
import team.monroe.org.pocketfit.view.SlideOffListView;

import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.animateAppearance;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.duration_constant;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.interpreter_accelerate_decelerate;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.xSlide;

public class MealsSelectFragment extends BodyFragment<FoodActivity> {

    private GenericListViewAdapter<Meal, GetViewImplementation.ViewHolder<Meal>> mMealAdapter;
    private SlideOffListView mMealsListView;
    private View mNoItemsView;
    private Data.DataChangeObserver<List> listDataChangeObserver;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_meals_and_drinks;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view(R.id.panel_new_routine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                application().function_createId("meal",observe_function(State.PAUSE, new PocketFitApp.DataAction<String>() {
                    @Override
                    public void data(String routine) {
                        ownerContract(FoodActivity.class).open_mealEditor(routine);
                    }
                }));
            }
        });



        mMealsListView = view(R.id.list_routine, SlideOffListView.class);
        mMealsListView.setVisibility(View.INVISIBLE);

        mNoItemsView = view(R.id.panel_no_items);
        mNoItemsView.setVisibility(View.VISIBLE);

        mMealAdapter = new GenericListViewAdapter<Meal, GetViewImplementation.ViewHolder<Meal>>(activity(),new GetViewImplementation.ViewHolderFactory<GetViewImplementation.ViewHolder<Meal>>() {
            @Override
            public GetViewImplementation.ViewHolder<Meal> create(final View convertView) {
                return new GetViewImplementation.GenericViewHolder<Meal>() {

                    TextView caption = (TextView) convertView.findViewById(R.id.item_caption);
                    TextView subCaption = (TextView) convertView.findViewById(R.id.item_sub_caption);
                    TextView text = (TextView) convertView.findViewById(R.id.item_text);
                    TextView sub_text = (TextView) convertView.findViewById(R.id.item_sub_text);
                    ImageView imageView = (ImageView) convertView.findViewById(R.id.item_image);
                    View panelDetails = convertView.findViewById(R.id.item_panel_details);
                    String lastInstalledImageId;

                    AppearanceController slidePanelAC = animateAppearance(panelDetails,xSlide(0f,100f))
                            .showAnimation(duration_constant(100), interpreter_accelerate_decelerate()).build();

                    @Override
                    public void cleanup() {
                        slidePanelAC.showWithoutAnimation();
                    }

                    @Override
                    public void update(final Meal meal, int position) {
                        caption.setText(meal.title);
                        subCaption.setText(meal.calories()+" cal");
                        text.setText("Fats " + meal.fats()+" Carbs "+meal.carbs()+" Protein "+meal.protein());
                        sub_text.setText("Products: "+meal.products());
                        panelDetails.setOnTouchListener(new SlideTouchGesture(DisplayUtils.dpToPx(100, getResources()),
                                SlideTouchGesture.Axis.X_LEFT) {
                            @Override
                            protected float applyFraction() {
                                return 0.95f;
                            }

                            @Override
                            protected void onEnd(float x, float y, float slideValue, float fraction) {
                                mMealsListView.disabled = false;
                            }

                            @Override
                            protected void onApply(float x, float y, float slideValue, float fraction) {
                                owner().open_mealEditor(meal.id);
                            }

                            @Override
                            protected void onProgress(float x, float y, float slideValue, float fraction) {
                                if (Math.abs(slideValue) < 80) return;
                                mMealsListView.disabled = true;
                                panelDetails.setTranslationX(-(slideValue));
                            }
                            @Override
                            protected void onCancel(float x, float y, float slideValue, float fraction) {
                                super.onCancel(x, y, slideValue, fraction);
                                slidePanelAC.show();
                            }
                        });
                        if (meal.imageId == null){
                            lastInstalledImageId = "";
                            imageView.setImageResource(R.drawable.foodcover_no_cover);
                        }else{
                            final String finalImageId = meal.imageId;
                            if (finalImageId.equals(lastInstalledImageId)) return;

                            imageView.setImageResource(R.drawable.foodcover_loading);
                            application().loadToBitmap(meal.imageId,
                                    DisplayUtils.dpToPx(100, getResources()),
                                    DisplayUtils.dpToPx(100, getResources()),
                                    new PocketFitApp.DataAction<Pair<String, Bitmap>>() {
                                        @Override
                                        public void data(Pair<String, Bitmap> data) {
                                            if (finalImageId.equals(data.first)){
                                                lastInstalledImageId = finalImageId;
                                                imageView.setImageBitmap(data.second);
                                            }
                                        }
                                    });
                        }
                    }
                };
            }
        },R.layout.item_meal);

        mMealsListView.setAdapter(mMealAdapter);
        mMealsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        listDataChangeObserver = new Data.DataChangeObserver<List>() {
            @Override
            public void onDataInvalid() {
                updateMealList();
            }

            @Override
            public void onData(List list) {

            }
        };
        application().data_meals().addDataChangeObserver(listDataChangeObserver);
        updateMealList();
    }

    private void updateMealList() {
        application().data_meals().fetch(true, observe_data_fetch(State.STOP, new PocketFitApp.DataAction<List>() {
            @Override
            public void data(List data) {
                List<Meal> mealList = data;
                if (data.isEmpty()) {
                    mMealsListView.setVisibility(View.INVISIBLE);
                    mNoItemsView.setVisibility(View.VISIBLE);

                    mMealAdapter.clear();
                    mMealAdapter.notifyDataSetChanged();
                } else {
                    mMealsListView.setVisibility(View.VISIBLE);
                    mNoItemsView.setVisibility(View.INVISIBLE);
                    mMealAdapter.clear();
                    mMealAdapter.addAll(mealList);
                    mMealAdapter.notifyDataSetChanged();
                }
            }
        }));
    }

    @Override
    public void onStop() {
        super.onStop();
        application().data_meals().removeDataChangeObserver(listDataChangeObserver);
    }


    @Override
    protected boolean isHeaderSecondary() {
        return false;
    }

    @Override
    protected String getHeaderName() {
        return "Meals & Drinks";
    }

}
