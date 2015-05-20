package team.monroe.org.pocketfit.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import team.monroe.org.pocketfit.DashboardActivity;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.fragments.contract.MainButtonUserContract;

public class NoActiveRoutineFragment extends BodyFragment<DashboardActivity> implements MainButtonUserContract {

    @Override
    protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    protected String getHeaderName() {
        return "Workout";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_no_active_routine;
    }

    @Override
    public View build_HeaderActionsView(ViewGroup actionPanel, LayoutInflater layoutInflater) {
        View view = layoutInflater.inflate(R.layout.actions_no_routines,actionPanel, false);
        view.findViewById(R.id.action_edit_exercises).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                owner().openExerciseEditor();
            }
        });
        return view;
    }

    @Override
    public Integer icon_mainButton() {
        return R.drawable.round_btn_gear;
    }

    @Override
    public void onMainButton() {
        owner().openRoutineEditor();
    }
}
