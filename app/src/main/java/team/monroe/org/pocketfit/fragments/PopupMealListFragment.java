package team.monroe.org.pocketfit.fragments;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.monroe.team.android.box.app.FragmentSupport;
import org.monroe.team.android.box.app.ui.GenericListViewAdapter;
import org.monroe.team.android.box.app.ui.GetViewImplementation;
import org.monroe.team.android.box.app.ui.SlideTouchGesture;
import org.monroe.team.android.box.app.ui.animation.AnimatorListenerSupport;
import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;
import org.monroe.team.android.box.data.Data;
import org.monroe.team.android.box.utils.DisplayUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import team.monroe.org.pocketfit.DashboardActivity;
import team.monroe.org.pocketfit.FoodActivity;
import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.AteMeal;
import team.monroe.org.pocketfit.presentations.Meal;
import team.monroe.org.pocketfit.view.SlideOffListView;

import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.animateAppearance;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.duration_constant;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.interpreter_accelerate;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.interpreter_accelerate_decelerate;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.scale;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.xSlide;

public class PopupMealListFragment extends FragmentSupport<PocketFitApp> {

    private GenericListViewAdapter<AteMeal, GetViewImplementation.ViewHolder<AteMeal>> mMealAdapter;
    private SlideOffListView mMealsListView;
    private View mNoItemsView;
    private Date mDate;
    private Data<List<AteMeal>> mData;
    private Data.DataChangeObserver<List<AteMeal>> dataChangeObserver;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_popup;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view(R.id.action_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) activity()).closePopup();
            }
        });
        mDate = new Date(getArguments().getLong("date_time"));

        DateFormat dateFormat = new SimpleDateFormat("EEE dd, MMMM yyyy");
        view_text(R.id.text_day).setText(dateFormat.format(mDate));

        mMealsListView = view(R.id.list_routine, SlideOffListView.class);
        mMealsListView.setVisibility(View.INVISIBLE);

        mNoItemsView = view(R.id.panel_no_items);
        mNoItemsView.setVisibility(View.VISIBLE);

        mMealAdapter = new GenericListViewAdapter<AteMeal, GetViewImplementation.ViewHolder<AteMeal>>(activity(),new GetViewImplementation.ViewHolderFactory<GetViewImplementation.ViewHolder<AteMeal>>() {
            @Override
            public GetViewImplementation.ViewHolder<AteMeal> create(final View convertView) {
                return new GetViewImplementation.GenericViewHolder<AteMeal>() {

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
                    public void update(final AteMeal ateMeal, int position) {
                        final Meal meal = ateMeal.meal;
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

                                application().function_deleteAteMeal(ateMeal, new PocketFitApp.FetchObserver<Void>(application()) {
                                    @Override
                                    public void onFetch(Void aVoid) {
                                        mData.invalidate();
                                    }
                                });
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
        },R.layout.item_meal_menu);

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
        mData = application().data_ate_meal(mDate);
        dataChangeObserver = new Data.DataChangeObserver<List<AteMeal>>() {
            @Override
            public void onDataInvalid() {
                fetchDataMeal();
            }

            @Override
            public void onData(List<AteMeal> ateMeals) {

            }
        };
        mData.addDataChangeObserver(dataChangeObserver);
        fetchDataMeal();
    }

    private void fetchDataMeal() {
        mData.fetch(true, new PocketFitApp.FetchObserver<List<AteMeal>>(application()) {
            @Override
            public void onFetch(List<AteMeal> ateMeals) {
                if (ateMeals.isEmpty()) {
                    mMealsListView.setVisibility(View.INVISIBLE);
                    mNoItemsView.setVisibility(View.VISIBLE);

                    mMealAdapter.clear();
                    mMealAdapter.notifyDataSetChanged();
                } else {
                    mMealsListView.setVisibility(View.VISIBLE);
                    mNoItemsView.setVisibility(View.INVISIBLE);
                    mMealAdapter.clear();
                    mMealAdapter.addAll(ateMeals);
                    mMealAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mData.removeDataChangeObserver(dataChangeObserver);
        mData = null;
    }
}
