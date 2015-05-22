package team.monroe.org.pocketfit.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.TrainingActivity;

public abstract class TrainingTileFragment extends BodyFragment<TrainingActivity>{


    @Override
    final protected String getHeaderName() {
        return "Training";
    }

    @Override
    final protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    final protected int getLayoutId() {
        return R.layout.fragment_training_tile;
    }

    @Override
    final public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup group = (ViewGroup) view.findViewById(R.id.panel_tile);
        inflater.inflate(getTileLayoutId(),group,true);
        return view;
    }

    protected abstract int getTileLayoutId();
}
