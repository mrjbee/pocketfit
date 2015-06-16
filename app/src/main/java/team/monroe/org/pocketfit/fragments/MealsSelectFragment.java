package team.monroe.org.pocketfit.fragments;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import team.monroe.org.pocketfit.FoodActivity;
import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.RoutinesActivity;
import team.monroe.org.pocketfit.view.SlideOffListView;

public class MealsSelectFragment extends BodyFragment<RoutinesActivity> {

    private SlideOffListView mMealsListView;
    private View mNoItemsView;

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
                        ownerContract(FoodActivity.class).open_MealEditor(routine);
                    }
                }));
            }
        });


        mMealsListView = view(R.id.list_routine, SlideOffListView.class);
        mMealsListView.setVisibility(View.INVISIBLE);

        mNoItemsView = view(R.id.panel_no_items);
        mNoItemsView.setVisibility(View.VISIBLE);

        mMealsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
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
