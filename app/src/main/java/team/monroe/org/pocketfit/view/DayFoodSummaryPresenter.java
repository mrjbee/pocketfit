package team.monroe.org.pocketfit.view;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.monroe.team.android.box.data.Data;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.AteMeal;
import team.monroe.org.pocketfit.view.presenter.ViewPresenter;

public class DayFoodSummaryPresenter extends ViewPresenter<View>{

    private final PocketFitApp app;
    private final TextView textCalories;
    private final TextView textCaloriesDelta;
    private final TextView textCaloriesLimit;
    private final HorProgressBarView barCalories;
    private final TextView textFats;
    private final TextView textCarbs;
    private final TextView textProtein;
    private final View editButton;
    private final View editLimitPanel;
    private final EditText editLimit;
    private Data<List<AteMeal>> mMealData;
    private Data.DataChangeObserver<List<AteMeal>> mDataObserver;
    private Integer mCaloriesLimit = 0;
    private List<AteMeal> mAteMeals = Collections.emptyList();
    private Data.DataChangeObserver<Integer> mCaloriesLimitObeserver;


    public DayFoodSummaryPresenter(View rootView, PocketFitApp appl) {
        super(rootView);
        this.app = appl;
        textCalories = (TextView) rootView.findViewById(R.id.text_calories_value);
        textCaloriesDelta = (TextView) rootView.findViewById(R.id.text_calories_delta);
        textCaloriesLimit = (TextView) rootView.findViewById(R.id.text_calories_limit);
        barCalories = (HorProgressBarView) rootView.findViewById(R.id.bar_calories);
        textFats = (TextView) rootView.findViewById(R.id.text_fats_value);
        textCarbs = (TextView) rootView.findViewById(R.id.text_carbs_value);
        textProtein = (TextView) rootView.findViewById(R.id.text_protein_value);
        editButton = rootView.findViewById(R.id.action_edit);
        editLimitPanel = rootView.findViewById(R.id.panel_edit_limit);
        updateCaloriesDetails();
        editLimit = ((EditText) rootView.findViewById(R.id.edit_limit));
        rootView.findViewById(R.id.action_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String limitText = editLimit.getText().toString();
                int limitValue = 0;
                if (!limitText.trim().isEmpty()){
                    limitValue = Integer.parseInt(limitText);
                }
                app.updateCaloriesLimit(limitValue);
                editLimitPanel.setVisibility(View.GONE);
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editLimitPanel.getVisibility() == View.GONE){
                    ((EditText)getRootView().findViewById(R.id.edit_limit)).setText("");
                    editLimitPanel.setVisibility(View.VISIBLE);
                }else{
                    editLimitPanel.setVisibility(View.GONE);
                }
            }
        });
    }

    public void init(Date date) {
        editLimitPanel.setVisibility(View.GONE);
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
        mCaloriesLimitObeserver = new Data.DataChangeObserver<Integer>() {
            @Override
            public void onDataInvalid() {
                doFetchCaloriesLimit();
            }

            @Override
            public void onData(Integer integer) {

            }
        };
        app.data_calories_limit().addDataChangeObserver(mCaloriesLimitObeserver);
        doFetch();
        doFetchCaloriesLimit();
    }

    private void doFetchCaloriesLimit() {
        app.data_calories_limit().fetch(true, new PocketFitApp.FetchObserver<Integer>(app) {
            @Override
            public void onFetch(Integer integer) {
                mCaloriesLimit = integer;
                updateCaloriesDetails();
            }
        });
    }

    private void updateCaloriesDetails() {

        int todayCalories = 0;
        for (AteMeal mAteMeal : mAteMeals) {
            todayCalories+=mAteMeal.meal.calories();
        }

        textCalories.setText(todayCalories+"");

        if (mCaloriesLimit == 0){
            textCaloriesLimit.setText("No Limit");
            textCaloriesDelta.setText("");
            barCalories.setProgress(todayCalories == 0?0f:1f);
        }else {
            textCaloriesLimit.setText("Limit "+mCaloriesLimit+" cal");
            int delta = mCaloriesLimit - todayCalories;
            if (delta > 0){
                textCaloriesDelta.setText("Left "+delta+" cal");
            }else {
                textCaloriesDelta.setText("Over "+(delta*-1)+" cal");
            }
            barCalories.setProgress((float) Math.max(0, Math.min(1d, ((double) todayCalories / (double) mCaloriesLimit))));
        }
    }

    public void onSelected() {

    }

    private void doFetch() {
        mMealData.fetch(true, new PocketFitApp.FetchObserver<List<AteMeal>>(app){
            @Override
            public void onFetch(List<AteMeal> ateMeals) {
                mAteMeals = ateMeals;
                updateCaloriesDetails();
            }
        });
    }

    private void updateListMeals() {
        int caloriesSummaries = 0;
        for (AteMeal ateMeal : mAteMeals) {
            caloriesSummaries +=ateMeal.meal.calories();
        }
    }

    public void deinit() {
        mMealData.removeDataChangeObserver(mDataObserver);
        app.data_calories_limit().removeDataChangeObserver(mCaloriesLimitObeserver);
        mDataObserver = null;
        mMealData = null;
    }

    public void destroy() {
     }


}
