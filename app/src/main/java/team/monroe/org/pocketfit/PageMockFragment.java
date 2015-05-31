package team.monroe.org.pocketfit;

import android.os.Bundle;

import team.monroe.org.pocketfit.fragments.DefaultPageFragment;

public class PageMockFragment extends DefaultPageFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        configureHeader("Something Else", null);
    }

    @Override
    protected int getTileLayoutId() {
        return R.layout.page_content_history;
    }

    @Override
    public void onMainButton() {

    }


}
