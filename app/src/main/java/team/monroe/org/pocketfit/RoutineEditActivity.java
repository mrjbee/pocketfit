package team.monroe.org.pocketfit;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import team.monroe.org.pocketfit.fragments.BodyFragment;
import team.monroe.org.pocketfit.fragments.ExerciseEditorFragment;
import team.monroe.org.pocketfit.fragments.ExercisesListFragment;
import team.monroe.org.pocketfit.fragments.RoutineDayEditorFragment;
import team.monroe.org.pocketfit.fragments.RoutineEditorFragment;
import team.monroe.org.pocketfit.fragments.RoutineExerciseEditorFragment;
import team.monroe.org.pocketfit.fragments.RoutinesFragment;
import team.monroe.org.pocketfit.fragments.contract.ExerciseOwnerContract;

public class RoutineEditActivity extends RoutinesActivity {

    @Override
    protected FragmentItem customize_startupFragment() {
        return new FragmentItem(RoutineEditorFragment.class).addArgument("routine_id", getFromIntent("routine_id", (String)null));
    }
}
