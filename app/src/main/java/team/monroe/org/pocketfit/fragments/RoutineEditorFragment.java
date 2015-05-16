package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.widget.Toast;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.Routine;

public class RoutineEditorFragment extends BodyFragment {

    private Routine mRoutine;

    @Override
    protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    protected String getHeaderName() {
        return "Edit Routine";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_routine;
    }



    @Override
    public void onResume() {
        super.onResume();
        String routineId = getArguments().getString("routine_id");
        if (routineId == null){
            application().error("No routine id");
        }
        application().function_getRoutine(routineId, observe_function(new PocketFitApp.DataAction<Routine>() {
            @Override
            public void data(Routine routine) {
                mRoutine = routine;
                view_text(R.id.edit_title).setText(routine.title);
                view_text(R.id.edit_description).setText(routine.description);
            }
        }));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRoutine != null){
            mRoutine.title = view_text(R.id.edit_title).getText().toString();
            mRoutine.description = view_text(R.id.edit_description).getText().toString();
            application().function_updateRoutine(mRoutine, observe_function(new PocketFitApp.DataAction<Routine>() {
                @Override
                public void data(Routine data) {
                    //Do nothing here
                }
            }));
        }
    }
}
