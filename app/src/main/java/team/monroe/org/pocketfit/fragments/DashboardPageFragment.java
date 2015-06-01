package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.monroe.team.android.box.utils.DisplayUtils;
import org.monroe.team.corebox.log.L;

import team.monroe.org.pocketfit.DashboardActivity;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.fragments.contract.BackButtonContract;
import team.monroe.org.pocketfit.fragments.contract.MainButtonUserContract;

public abstract class DashboardPageFragment extends AppFragment<DashboardActivity> implements MainButtonUserContract, BackButtonContract {

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


    final protected void configureHeader(String title, View actions){

        int textColor = getResources().getColor(headerLightVersion() ? R.color.text_header_dash:R.color.text_header_dash_dark);

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
            view_text(R.id.caption).setVisibility(headerFullVersion() ?View.VISIBLE:View.GONE);
        }
    }


    protected final ViewGroup getHeaderContainer() {
        return (ViewGroup) view(R.id.header);
    }

    protected final TextView getSecondaryHeaderView(){
        return view_text(R.id.secondary_caption);
    }

    public void onPageMoveToShow(float top, boolean pageMotionDirectionUp) {

        if (top == 0){
            getHeaderContainer().setRotation(0);
        }else{
            getHeaderContainer().setRotation(pageMotionDirectionUp? 5:-5);
        }

        int headerHeight = getHeaderContainer().getHeight();
        float alpha = 0;
        if (pageMotionDirectionUp){
            if ((getFragmentView().getHeight()-top) > headerHeight){
                alpha = 1;
            }else {
                alpha = (float)(getFragmentView().getHeight()-top)/(float)headerHeight;
            }
        }else{
            if (Math.abs(top) > headerHeight){
                alpha = 0;
            }else {
                alpha = 1f - (float)Math.abs(top)/(float)headerHeight;
            }
        }
       // L.DEBUG.d("HEADER [show] alpha:"+alpha+" top:"+top);
        getHeaderContainer().setAlpha(alpha);
    }

    public void onPageMoveToHide(float top, boolean pageMotionDirectionUp) {
        if (top == 0){
            getHeaderContainer().setRotation(0);
        }else{
            getHeaderContainer().setRotation(pageMotionDirectionUp? 5:-5);
        }
        int headerHeight = getHeaderContainer().getHeight();
        float alpha = 0;
        if (!pageMotionDirectionUp){
            if ((getFragmentView().getHeight()-top) > headerHeight){
                alpha = 1;
            }else {
                alpha = (float)(getFragmentView().getHeight()-top)/(float)headerHeight;
            }
        }else{
            if (Math.abs(top) > headerHeight){
                alpha = 0;
            }else {
                alpha = 1f - (float)Math.abs(top)/(float)headerHeight;
            }
        }
        // L.DEBUG.d("HEADER [show] alpha:"+alpha+" top:"+top);
        getHeaderContainer().setAlpha(alpha);
    }

    public void onPageHide(boolean pageMotionDirectionUp) {}

    public void onPageShow(boolean pageMotionDirectionUp) {}

    protected boolean headerFullVersion() {
        return true;
    }

    protected boolean headerLightVersion() {
        return true;
    }


}
