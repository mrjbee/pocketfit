package team.monroe.org.pocketfit;

import team.monroe.org.pocketfit.fragments.RoutineDayEditorFragment;
import team.monroe.org.pocketfit.fragments.RoutineEditorFragment;
import team.monroe.org.pocketfit.presentations.RoutineDay;

public class RoutineDayEditActivity extends RoutinesActivity {

    @Override
    protected FragmentItem customize_startupFragment() {
        return new FragmentItem(RoutineDayEditorFragment.class).
                addArgument("routine_id", getFromIntent("routine_id", (String) null)).
                addArgument("day_id", getFromIntent("day_id", (String) null));
    }
}
