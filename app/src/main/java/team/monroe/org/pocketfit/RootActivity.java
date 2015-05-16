package team.monroe.org.pocketfit;

import android.os.Bundle;

import org.monroe.team.android.box.app.ActivitySupport;

import java.io.Serializable;
import java.util.ArrayList;

import team.monroe.org.pocketfit.fragments.BodyFragment;
import team.monroe.org.pocketfit.fragments.DashboardFragment;
import team.monroe.org.pocketfit.fragments.HeaderFragment;
import team.monroe.org.pocketfit.fragments.RoutineEditorFragment;
import team.monroe.org.pocketfit.fragments.RoutinesFragment;

public class RootActivity extends ActivitySupport<PocketFitApp> {

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
                    .replace(R.id.fragment_container_body, fragment_instance(backStackItem.fragmentClass, BodyFragment.HeaderUpdateRequest.ANIMATE))
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
                        fragment_instance(RoutineEditorFragment.class, BodyFragment.HeaderUpdateRequest.ANIMATE,bundle) )
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


    public static class FragmentBackStackItem implements Serializable{

        private final Class<? extends BodyFragment> fragmentClass;

        public FragmentBackStackItem(Class<? extends BodyFragment> fragmentClass) {
            this.fragmentClass = fragmentClass;
        }

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
}
