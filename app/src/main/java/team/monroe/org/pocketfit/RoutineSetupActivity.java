package team.monroe.org.pocketfit;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import team.monroe.org.pocketfit.fragments.BodyFragment;
import team.monroe.org.pocketfit.fragments.DashboardFragment;
import team.monroe.org.pocketfit.fragments.RoutineExerciseEditorFragment;
import team.monroe.org.pocketfit.fragments.ExerciseEditorFragment;
import team.monroe.org.pocketfit.fragments.ExercisesListFragment;
import team.monroe.org.pocketfit.fragments.RoutineDayEditorFragment;
import team.monroe.org.pocketfit.fragments.RoutineEditorFragment;
import team.monroe.org.pocketfit.fragments.RoutinesFragment;

public class RoutineSetupActivity extends FragmentActivity {

    private static final int PICK_IMAGE = 30;

    @Override
    protected FragmentItem customize_startupFragment() {
        return new FragmentItem(DashboardFragment.class);
    }

    @Override
    protected int customize_rootLayout() {
        return R.layout.activity_fragment_general;
    }

    public void open_Routines() {
        updateBodyFragment(new FragmentItem(RoutinesFragment.class), change_slide_from_right());
    }

    public void open_Routine(String routineId) {
        updateBodyFragment(
                new FragmentItem(
                        RoutineEditorFragment.class).addArgument("routine_id", routineId),
                change_slide_from_right()
        );
    }

    public void open_RoutineDay(String routineId, String routineDayId) {
        updateBodyFragment(
                new FragmentItem(
                        RoutineDayEditorFragment.class).addArgument("routine_id", routineId).addArgument("day_id", routineDayId),
                change_slide_from_right()
        );
    }

    public void open_RoutineExercise(String dayId, String routineId) {
        FragmentItem fragmentBackStackItem = new FragmentItem(RoutineExerciseEditorFragment.class)
                .addArgument("routine_exercise_id", routineId)
                .addArgument("day_id",dayId);
        updateBodyFragment(fragmentBackStackItem, change_slide_from_right());
    }

    public void open_exercisesAsEditor() {
        updateBodyFragment(new FragmentItem(ExercisesListFragment.class), change_slide_from_right());
    }

    public void open_exercisesAsChooser(String routineDayId, String routineExerciseId, boolean moveToExerciseConfigFragment) {

        FragmentItem fragmentBackStackItem = new FragmentItem(ExercisesListFragment.class)
                .addArgument("routine_exercise_id", routineExerciseId)
                .addArgument("day_id",routineDayId)
                .addArgument("chooserMode", "true");
        if (moveToExerciseConfigFragment){
            fragmentBackStackItem.addArgument("fragment_class", RoutineExerciseEditorFragment.class);
        }

        if (moveToExerciseConfigFragment){
            updateBodyFragment(fragmentBackStackItem, change_slide_from_right());
        }else {
            updateBodyFragment(fragmentBackStackItem, change_flip_in());
        }
    }

    public void open_exercisesEditor(String exerciseId) {
        Bundle bundle = new Bundle();
        bundle.putString("exercise_id",exerciseId);
        updateBodyFragment(new FragmentItem(ExerciseEditorFragment.class).addArgument("exercise_id", exerciseId), change_slide_from_right());
    }

    public void performImageSelection() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra("return-data", false);
        try {
            startActivityForResult(Intent.createChooser(intent, "Pick cover"), PICK_IMAGE);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application for image selection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_IMAGE && data != null && resultCode == Activity.RESULT_OK) {
            Uri _uri = data.getData();
            if (_uri == null) return;
            BodyFragment bodyFragment = getBodyFragment();
            bodyFragment.onImageResult(_uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
