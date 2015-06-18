package team.monroe.org.pocketfit.view;

import android.view.View;
import android.widget.TextView;

import java.util.Date;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.view.presenter.ViewPresenter;

public class DayFoodSummaryPresenter extends ViewPresenter<View>{

    private final PocketFitApp app;
    private final TextView textCalories;
    private final TextView textFats;
    private final TextView textCarbs;
    private final TextView textProtein;

    public DayFoodSummaryPresenter(View rootView, PocketFitApp app) {
        super(rootView);
        this.app = app;
        textCalories = (TextView) rootView.findViewById(R.id.text_calories_value);
        textFats = (TextView) rootView.findViewById(R.id.text_fats_value);
        textCarbs = (TextView) rootView.findViewById(R.id.text_carbs_value);
        textProtein = (TextView) rootView.findViewById(R.id.text_protein_value);
    }

    public void init(Date date) {

    }

    public void deinit() {

    }

    public void destroy() {

    }
}
