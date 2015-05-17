package team.monroe.org.pocketfit;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import org.monroe.team.android.box.app.ActivitySupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import team.monroe.org.pocketfit.fragments.BodyFragment;
import team.monroe.org.pocketfit.fragments.DashboardFragment;
import team.monroe.org.pocketfit.fragments.HeaderFragment;
import team.monroe.org.pocketfit.fragments.RoutineEditorFragment;
import team.monroe.org.pocketfit.fragments.RoutineTrainingFragment;
import team.monroe.org.pocketfit.fragments.RoutineTrainingsFragment;
import team.monroe.org.pocketfit.fragments.RoutinesFragment;

public class RootActivity extends ActivitySupport<PocketFitApp> {

    private static final int PICK_IMAGE = 30;
    private ArrayList<FragmentBackStackItem> backStack = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        if (isFirstRun(savedInstanceState)){
            DashboardFragment fragment = new DashboardFragment();
            fragment.feature_headerUpdate(BodyFragment.HeaderUpdateRequest.SET);
            fragment.feature_tileAnimation(true);
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container_body, fragment)
                    .commit();

        } else {
            backStack = (ArrayList<FragmentBackStackItem>) savedInstanceState.getSerializable("back_stack");
        }

    }

    @Override
    public void onBackPressed() {
        if (backStack.isEmpty()) {
            super.onBackPressed();
        } else {
           FragmentBackStackItem backStackItem = backStack.remove(backStack.size()-1);
           getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_from_right, R.animator.slide_out_to_left)
                    .replace(R.id.fragment_container_body, fragment_instance(
                            backStackItem.fragmentClass,
                            BodyFragment.HeaderUpdateRequest.ANIMATE,
                            backStackItem.getArgumentBundle()))
                    .commit();
        }
    }


    public void open_Routines() {
        backStack.add(new FragmentBackStackItem(DashboardFragment.class));
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_to_right)
                .replace(R.id.fragment_container_body,fragment_instance(RoutinesFragment.class, BodyFragment.HeaderUpdateRequest.ANIMATE) )
                .commit();
    }

    public void open_Routine(String routineId) {
        Bundle bundle = new Bundle();
        bundle.putString("routine_id",routineId);
        backStack.add(new FragmentBackStackItem(RoutinesFragment.class));
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_to_right)
                .replace(R.id.fragment_container_body,
                        fragment_instance(RoutineEditorFragment.class, BodyFragment.HeaderUpdateRequest.ANIMATE,bundle),"body_fragment")
                .commit();
    }

    public void open_RoutineDay(String routineId, String routineDayId) {
        Bundle bundle = new Bundle();
        bundle.putString("routine_id",routineId);
        bundle.putString("day_id",routineDayId);
        backStack.add(new FragmentBackStackItem(RoutineEditorFragment.class).addArgument("routine_id",routineId));
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.slide_in_from_left, R.animator.slide_out_to_right)
                .replace(R.id.fragment_container_body,
                        fragment_instance(RoutineTrainingFragment.class, BodyFragment.HeaderUpdateRequest.ANIMATE,bundle),"body_fragment")
                .commit();
    }

    public void header(String headerText, boolean secondary) {
        HeaderFragment fragment = getHeaderFragment();
        fragment.changeCaption(headerText, secondary, false);
    }

    public void animateHeader(String headerText, boolean secondary) {
        HeaderFragment fragment = getHeaderFragment();
        fragment.changeCaption(headerText, secondary, true);
    }

    private HeaderFragment getHeaderFragment() {
        return (HeaderFragment) getFragmentManager().findFragmentById(R.id.fragment_header);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("back_stack", backStack);
    }


    public static BodyFragment fragment_instance(Class<? extends BodyFragment> fragmentClass, BodyFragment.HeaderUpdateRequest request) {
        return fragment_instance(fragmentClass,request,null);
    }

    public static BodyFragment fragment_instance(Class<? extends BodyFragment> fragmentClass, BodyFragment.HeaderUpdateRequest request, Bundle arguments){
        try {
            BodyFragment fragment = fragmentClass.newInstance();
            fragment.feature_headerUpdate(request);
            if (arguments != null){
                fragment.setArguments(arguments);
            }
            return fragment;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
            BodyFragment bodyFragment = (BodyFragment) getFragmentManager().findFragmentByTag("body_fragment");
            bodyFragment.onImageResult(_uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static class FragmentBackStackItem implements Serializable{

        private final Class<? extends BodyFragment> fragmentClass;
        private final Map<String,Serializable> argumentMap = new HashMap<>();

        public FragmentBackStackItem(Class<? extends BodyFragment> fragmentClass) {
            this.fragmentClass = fragmentClass;
        }

        public Bundle getArgumentBundle(){
            if (argumentMap.size() == 0) return null;
            Bundle bundle = new Bundle();
            for (Map.Entry<String, Serializable> entry : argumentMap.entrySet()) {
                bundle.putSerializable(entry.getKey(), entry.getValue());
            }
            return bundle;
        }

        public FragmentBackStackItem addArgument(String key, Serializable value){
            argumentMap.put(key, value);
            return this;
        }

    }

}
