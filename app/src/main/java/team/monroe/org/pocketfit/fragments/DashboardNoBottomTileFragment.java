package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import team.monroe.org.pocketfit.DashboardActivity;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.fragments.contract.MainButtonUserContract;

public abstract class DashboardNoBottomTileFragment extends AbstractDashboardTileFragment{
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_single_tile_no_bottom;
    }
}
