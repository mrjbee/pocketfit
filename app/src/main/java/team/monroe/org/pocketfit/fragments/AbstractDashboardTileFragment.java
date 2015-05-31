package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import team.monroe.org.pocketfit.DashboardActivity;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.fragments.contract.BackButtonContract;
import team.monroe.org.pocketfit.fragments.contract.MainButtonUserContract;

public abstract class AbstractDashboardTileFragment extends AppFragment<DashboardActivity> implements MainButtonUserContract, BackButtonContract {

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

}
