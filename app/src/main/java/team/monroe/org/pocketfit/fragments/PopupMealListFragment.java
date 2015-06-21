package team.monroe.org.pocketfit.fragments;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import org.monroe.team.android.box.app.ApplicationSupport;
import org.monroe.team.android.box.app.FragmentSupport;
import org.monroe.team.android.box.app.ui.GenericListViewAdapter;
import org.monroe.team.android.box.app.ui.GetViewImplementation;
import org.monroe.team.android.box.app.ui.SlideTouchGesture;
import org.monroe.team.android.box.app.ui.animation.AnimatorListenerSupport;
import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;
import org.monroe.team.android.box.data.Data;
import org.monroe.team.android.box.utils.DisplayUtils;
import org.monroe.team.corebox.utils.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import team.monroe.org.pocketfit.DashboardActivity;
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

    private GenericListViewAdapter<MealMenuItem, GetViewImplementation.ViewHolder<MealMenuItem>> mMealAdapter;
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

        mMealAdapter = new GenericListViewAdapter<MealMenuItem, GetViewImplementation.ViewHolder<MealMenuItem>>(activity(), new GetViewImplementation.ViewHolderFactory<GetViewImplementation.ViewHolder<MealMenuItem>>(){

            @Override
            public GetViewImplementation.ViewHolder<MealMenuItem> create(final View convertView) {
                return new GetViewImplementation.GenericViewHolder<MealMenuItem>() {

                    View panelDay = convertView.findViewById(R.id.panel_day);
                    View panelMeal = convertView.findViewById(R.id.panel_meal);

                    @Override
                    public void update(MealMenuItem mealMenuItem, int position) {
                        panelDay.setVisibility(View.GONE);
                        panelMeal.setVisibility(View.GONE);
                        if (mealMenuItem.ateMeal == null){
                            update(mealMenuItem.dayPart, position);
                        }else{
                            update(mealMenuItem.ateMeal, position);
                        }
                    }

                    TextView mealCaption = (TextView) convertView.findViewById(R.id.text_meal);
                    TextView mealTime = (TextView) convertView.findViewById(R.id.text_meal_time);
                    TextView mealCalories = (TextView) convertView.findViewById(R.id.text_meal_calories);
                    TextView mealCarbs = (TextView) convertView.findViewById(R.id.text_meal_carbs);
                    TextView mealFats = (TextView) convertView.findViewById(R.id.text_meal_fats);
                    TextView mealProtein = (TextView) convertView.findViewById(R.id.text_meal_protein);
                    String lastInstalledImageId;
                    ImageView imageView = (ImageView) convertView.findViewById(R.id.item_image);
                    View addAction = convertView.findViewById(R.id.action_add);
                    AppearanceController addButtonAnimation = animateAppearance(convertView.findViewById(R.id.panel_add_button), scale(1f,0f))
                            .hideAnimation(duration_constant(200), interpreter_accelerate(0.3f))
                            .build();

                    private void update(final AteMeal ateMeal, int position) {
                        panelMeal.setVisibility(View.VISIBLE);
                        mealCaption.setText(ateMeal.meal.title);
                        mealCalories.setText(ateMeal.meal.calories() + " cal");
                        mealCarbs.setText(String.format("%.2f", ateMeal.meal.carbs()));
                        mealFats.setText(String.format("%.2f", ateMeal.meal.fats()));
                        mealProtein.setText(String.format("%.2f", ateMeal.meal.protein()));
                        mealTime.setText(DateFormat.getTimeInstance().format(ateMeal.date));
                        addAction.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                addButtonAnimation.hideAndCustomize(new AppearanceController.AnimatorCustomization() {
                                    @Override
                                    public void customize(Animator animator) {
                                        animator.addListener(new AnimatorListenerSupport(){
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                application().function_deleteAteMeal(ateMeal, new PocketFitApp.FetchObserver<Void>(application()) {
                                                    @Override
                                                    public void onFetch(Void aVoid) {
                                                        mData.invalidate();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        if (ateMeal.meal.imageId == null){
                            lastInstalledImageId = "";
                            imageView.setImageResource(R.drawable.foodcover_no_cover);
                        }else{
                            final String finalImageId = ateMeal.meal.imageId;
                            if (finalImageId.equals(lastInstalledImageId)) return;

                            imageView.setImageResource(R.drawable.foodcover_loading);
                            application().loadToBitmap(ateMeal.meal.imageId,
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

                    TextView dayCaption = (TextView) convertView.findViewById(R.id.text_day);
                    TextView dayCalories = (TextView) convertView.findViewById(R.id.text_day_calories);
                    TextView dayCarbs = (TextView) convertView.findViewById(R.id.text_day_carbs);
                    TextView dayFats = (TextView) convertView.findViewById(R.id.text_day_fats);
                    TextView dayProtein = (TextView) convertView.findViewById(R.id.text_day_protein);

                    private void update(DayPart dayPart, int position) {
                        panelDay.setVisibility(View.VISIBLE);
                        dayCaption.setText(dayPart.title);
                        dayCalories.setText(dayPart.calories + " cal");
                        dayCarbs.setText(String.format("%.2f", dayPart.carbs));
                        dayFats.setText(String.format("%.2f", dayPart.fats));
                        dayProtein.setText(String.format("%.2f", dayPart.protein));
                    }
                };
            }
        },R.layout.item_menu);

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

                    Pair<Long, ArrayList<AteMeal>>[] ateMealsPerDay =new Pair[]{
                            new Pair<>(5L * (1000 * 60 *60) ,new ArrayList<AteMeal>()),
                            new Pair<>(12L * (1000 * 60 *60),new ArrayList<AteMeal>()),
                            new Pair<>(20L * (1000 * 60 *60),new ArrayList<AteMeal>()),
                            new Pair<>(24L * (1000 * 60 *60),new ArrayList<AteMeal>()),
                    };

                    for (AteMeal ateMeal : ateMeals) {
                        long ateTime = DateUtils.timeOnly(ateMeal.date);
                        for (Pair<Long, ArrayList<AteMeal>> longArrayListPair : ateMealsPerDay) {
                            if (ateTime < longArrayListPair.first){
                                longArrayListPair.second.add(ateMeal);
                                break;
                            }
                        }
                    }

                    ArrayList<MealMenuItem> menuItems = new ArrayList<MealMenuItem>();
                    for (int i = 0; i< ateMealsPerDay.length; i++){
                        Pair<Long, ArrayList<AteMeal>> mealPerTime = ateMealsPerDay[i];
                        if (!mealPerTime.second.isEmpty()){
                            menuItems.add(createDayPartMealItem(mealPerTime, i));
                            for (AteMeal ateMeal : mealPerTime.second) {
                                menuItems.add(new MealMenuItem(ateMeal, null));
                            }
                        }
                    }

                    mMealAdapter.addAll(menuItems);
                    mMealAdapter.notifyDataSetChanged();
                }
            }

            private MealMenuItem createDayPartMealItem(Pair<Long, ArrayList<AteMeal>> mealPerTime, int position) {
                String title = "";
                switch (position){
                    case 0:
                        title = "Night";
                        break;
                    case 1:
                        title = "Morning";
                        break;
                    case 2:
                        title = "Afternoon";
                        break;
                    case 3:
                        title = "Evening";
                        break;
                    default:
                        throw new IllegalStateException();

                }

                DayPart dayPart = new DayPart(title);
                for (AteMeal ateMeal : mealPerTime.second) {
                    dayPart.calories += ateMeal.meal.calories();
                    dayPart.fats += ateMeal.meal.fats();
                    dayPart.protein += ateMeal.meal.protein();
                    dayPart.carbs += ateMeal.meal.carbs();
                }

                return new MealMenuItem(null, dayPart);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mData.removeDataChangeObserver(dataChangeObserver);
        mData = null;
    }

    public class DayPart {

        private final String title;
        private float carbs =0;
        private float fats=0;
        private float protein=0;
        private int calories=0;

        public DayPart(String title) {
            this.title = title;
        }
    }

    public class MealMenuItem{

        private final AteMeal ateMeal;
        private final DayPart dayPart;

        public MealMenuItem(AteMeal ateMeal, DayPart dayPart) {
            this.ateMeal = ateMeal;
            this.dayPart = dayPart;
        }
    }


}
