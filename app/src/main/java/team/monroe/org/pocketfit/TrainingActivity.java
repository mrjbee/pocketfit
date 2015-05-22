package team.monroe.org.pocketfit;

import android.view.View;
import android.view.ViewGroup;

import team.monroe.org.pocketfit.fragments.TrainingTileRoutineExerciseFragment;

public class TrainingActivity extends FragmentActivity {

    @Override
    protected FragmentItem customize_startupFragment() {
        return new FragmentItem(TrainingTileRoutineExerciseFragment.class);
    }

    @Override
    protected int customize_rootLayout() {
        return R.layout.activity_fragment_training_execution;
    }

    @Override
    public void header(String headerText, boolean secondary) {}

    @Override
    public void animateHeader(String headerText, boolean secondary) {}

    @Override
    public View buildHeaderActionsView(ViewGroup actionPanel) {
        return null;
    }


}
