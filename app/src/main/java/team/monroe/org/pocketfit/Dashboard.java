package team.monroe.org.pocketfit;

import android.app.Fragment;
import android.os.Bundle;

import org.monroe.team.android.box.app.ActivitySupport;

import java.io.Serializable;
import java.util.ArrayList;

import team.monroe.org.pocketfit.fragments.BodyFragment;
import team.monroe.org.pocketfit.fragments.DashboardFragment;
import team.monroe.org.pocketfit.fragments.HeaderFragment;
import team.monroe.org.pocketfit.fragments.RoutineEditFragment;

public class Dashboard extends ActivitySupport<PocketFitApp> {

    private ArrayList<FragmentBackStackItem> backStack = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        if (isFirstRun(savedInstanceState)){
            DashboardFragment fragment = new DashboardFragment();
            fragment.feature_headerUpdate(BodyFragment.HeaderUpdateRequest.SET);
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
                    .setCustomAnimations(R.animator.slide_in, R.animator.slide_out)
                    .replace(R.id.fragment_container_body, fragment_instance(backStackItem.fragmentClass, BodyFragment.HeaderUpdateRequest.ANIMATE))
                    .commit();
        }
    }


    public void open_Routines(String routineId) {
        backStack.add(new FragmentBackStackItem(DashboardFragment.class));
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.slide_in, R.animator.slide_out)
                .replace(R.id.fragment_container_body,fragment_instance(RoutineEditFragment.class, BodyFragment.HeaderUpdateRequest.ANIMATE) )
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


    public static BodyFragment fragment_instance(Class<? extends BodyFragment> fragmentClass, BodyFragment.HeaderUpdateRequest request){
        try {
            BodyFragment fragment = fragmentClass.newInstance();
            fragment.feature_headerUpdate(request);
            return fragment;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
