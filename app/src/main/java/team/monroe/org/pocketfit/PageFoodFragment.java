package team.monroe.org.pocketfit;

import android.os.Bundle;

import team.monroe.org.pocketfit.fragments.DefaultPageFragment;

public class PageFoodFragment extends DefaultPageFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        configureHeader("Calories Diary", null);
    }

    @Override
    protected int getTileLayoutId() {
        return R.layout.page_content_history;
    }

    @Override
    public void onMainButton() {
        hideMainButton(new Runnable() {
            @Override
            public void run() {
                owner().openMealsSelect();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMainButton();
    }

    @Override
    public void updateMainButton() {
        showMainButton(R.drawable.round_btn_plus, null);
    }
}
