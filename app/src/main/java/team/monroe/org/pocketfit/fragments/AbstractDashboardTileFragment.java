package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.monroe.team.android.box.utils.DisplayUtils;

import team.monroe.org.pocketfit.DashboardActivity;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.fragments.contract.BackButtonContract;
import team.monroe.org.pocketfit.fragments.contract.MainButtonUserContract;

public abstract class AbstractDashboardTileFragment extends AppFragment<DashboardActivity> implements MainButtonUserContract, BackButtonContract {

    private boolean mShown = true;
    private float mCaptionHeight;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCaptionHeight = DisplayUtils.dpToPx(100, getResources());
    }

    @Override
    final public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup group = (ViewGroup) view.findViewById(R.id.panel_tile);
        inflater.inflate(getTileLayoutId(),group,true);
        return view;
    }

    protected abstract int getTileLayoutId();

    @Override
    public boolean onBackButton() {
        return false;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dash_header;
    }

    public void configureHeader(String title, View actions, boolean topHeaderRequired, boolean light){

        int textColor = getResources().getColor(light? R.color.text_header_dash:R.color.text_header_dash_dark);

        view_text(R.id.secondary_caption).setTextColor(textColor);
        view_text(R.id.secondary_caption).setText(title);

        ViewGroup container = view(R.id.panel_actions, ViewGroup.class);
        container.removeAllViews();
        if (actions != null) {
            container.addView(actions);
        }

        if (!mShown){
            //appear animation required
            mShown = true;
        }

        if (DisplayUtils.isLandscape(getResources(), R.bool.class)){
            view_text(R.id.caption).setVisibility(View.GONE);
        }else {
            scaleCaption(1f);
            view_text(R.id.caption).setVisibility(topHeaderRequired?View.VISIBLE:View.GONE);
        }
    }

    public void updateAlpha(float alpha) {
        getFragmentView().setAlpha(alpha);
    }

    public void scaleCaption(float fraction) {
        getFragmentView().setTranslationY((1- fraction) * - mCaptionHeight);
    }
}
