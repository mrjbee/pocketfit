package team.monroe.org.pocketfit;

import android.os.Bundle;

import team.monroe.org.pocketfit.fragments.DefaultPageFragment;

public class PageHistoryFragment extends DefaultPageFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        configureHeader("My Fit Diary", null);
        showMainButton(R.drawable.round_btn_pen, null);
    }

    @Override
    protected int getTileLayoutId() {
        return R.layout.page_content_history;
    }

    @Override
    public void onMainButton() {

    }

    @Override
    public void updateMainButton() {
        showMainButton(R.drawable.round_btn_pen, null);
    }
}
