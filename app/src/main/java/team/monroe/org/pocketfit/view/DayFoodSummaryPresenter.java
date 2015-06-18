package team.monroe.org.pocketfit.view;

import android.view.View;
import android.widget.TextView;

import org.monroe.team.android.box.data.Data;

import java.util.Date;
import java.util.List;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.AteMeal;
import team.monroe.org.pocketfit.view.presenter.ViewPresenter;

public class DayFoodSummaryPresenter extends ViewPresenter<View>{

    private final PocketFitApp app;
    private final TextView textCalories;
    private final TextView textFats;
    private final TextView textCarbs;
    private final TextView textProtein;
    private Data<List<AteMeal>> mMealData;
    private Data.DataChangeObserver<List<AteMeal>> mDataObserver;

    public DayFoodSummaryPresenter(View rootView, PocketFitApp app) {
        super(rootView);
        this.app = app;
        textCalories = (TextView) rootView.findViewById(R.id.text_calories_value);
        textFats = (TextView) rootView.findViewById(R.id.text_fats_value);
        textCarbs = (TextView) rootView.findViewById(R.id.text_carbs_value);
        textProtein = (TextView) rootView.findViewById(R.id.text_protein_value);
    }

    public void init(Date date) {
        if (mMealData != null) throw new IllegalStateException();
        mMealData = app.data_ate_meal(date);
        mDataObserver = new Data.DataChangeObserver<List<AteMeal>>() {
            @Override
            public void onDataInvalid() {
                doFetch();
            }

            @Override
            public void onData(List<AteMeal> ateMeals) {

            }
        };
        mMealData.addDataChangeObserver(mDataObserver);
        doFetch();
    }

    private void doFetch() {
        mMealData.fetch(true, new PocketFitApp.FetchObserver<List<AteMeal>>(app){
            @Override
            public void onFetch(List<AteMeal> ateMeals) {
                int caloriesSummaries = 0;
                for (AteMeal ateMeal : ateMeals) {
                    caloriesSummaries +=ateMeal.meal.calories();
                }
                textCalories.setText(caloriesSummaries+"");
            }
        });
    }

    public void deinit() {
        mMealData.removeDataChangeObserver(mDataObserver);
        mDataObserver = null;
        mMealData = null;
    }

    public void destroy() {

    }
}
