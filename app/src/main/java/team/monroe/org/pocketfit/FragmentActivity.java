package team.monroe.org.pocketfit;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import org.monroe.team.android.box.app.ActivitySupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import team.monroe.org.pocketfit.fragments.BodyFragment;
import team.monroe.org.pocketfit.fragments.HeaderFragment;

public abstract class FragmentActivity extends ActivitySupport<PocketFitApp> {

    private ArrayList<FragmentItem> backStack = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(customize_rootLayout());
        if (isFirstRun(savedInstanceState)){
            FragmentItem startupFragment = customize_startupFragment();
            backStack.add(startupFragment);
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container_body, fragment_instance(startupFragment.fragmentClass,
                            BodyFragment.HeaderUpdateRequest.SET,
                            startupFragment.getArgumentBundle()))
                    .commit();
        } else {
            backStack = (ArrayList<FragmentItem>) savedInstanceState.getSerializable("back_stack");
        }

    }

    protected abstract FragmentItem customize_startupFragment();
    protected abstract int customize_rootLayout();

    @Override
    public void onBackPressed() {
        if (backStack.size() == 1) {
            super.onBackPressed();
        } else {

           FragmentItem backStackItem = backStack.remove(backStack.size()-1);
           backStackItem = backStack.get(backStack.size()-1);

           BodyFragmentChangeRequest changeRequest = change_slide_from_left();
           updateBodyFragmentNoHistory(backStackItem, changeRequest);
        }
    }

    final protected void updateBodyFragment(FragmentItem backStackItem, BodyFragmentChangeRequest changeRequest) {
        backStack.add(backStackItem);
        updateBodyFragmentNoHistory(backStackItem, changeRequest);
    }

    final protected void updateBodyFragmentNoHistory(FragmentItem backStackItem, BodyFragmentChangeRequest changeRequest) {
        getFragmentManager().beginTransaction()
                 .setCustomAnimations(changeRequest.in_animation, changeRequest.out_animation)
                 .replace(R.id.fragment_container_body, fragment_instance(
                         backStackItem.fragmentClass,
                         BodyFragment.HeaderUpdateRequest.ANIMATE,
                         backStackItem.getArgumentBundle()))
                 .commit();
    }

    final protected BodyFragmentChangeRequest change_slide_from_left() {
        return new BodyFragmentChangeRequest(R.animator.slide_in_from_right, R.animator.slide_out_to_left, BodyFragment.HeaderUpdateRequest.ANIMATE);
    }

    final protected BodyFragmentChangeRequest change_slide_from_right() {
        return new BodyFragmentChangeRequest(R.animator.slide_in_from_left, R.animator.slide_out_to_right, BodyFragment.HeaderUpdateRequest.ANIMATE);
    }

    final protected BodyFragmentChangeRequest change_flip_in() {
        return new BodyFragmentChangeRequest(R.animator.card_flip_in_right, R.animator.card_flip_out_right, BodyFragment.HeaderUpdateRequest.ANIMATE);
    }


    final public void onResult(Map<String, String> results) {

        FragmentItem producerBackStack = backStack.remove(backStack.size()-1);
        Class<BodyFragment> consumerFragmentClass = (Class<BodyFragment>) producerBackStack.argumentMap.get("fragment_class");

        FragmentItem consumerBackStackItem;
        if (consumerFragmentClass != null){
            consumerBackStackItem = new FragmentItem(consumerFragmentClass);
            for (Map.Entry<String, Serializable> stringSerializableEntry : producerBackStack.argumentMap.entrySet()) {
                consumerBackStackItem.addArgument(stringSerializableEntry.getKey(), stringSerializableEntry.getValue());
            }
            backStack.add(consumerBackStackItem);
        }else {
            consumerBackStackItem = backStack.get(backStack.size()-1);
        }

        for (Map.Entry<String, String> stringSerializableEntry : results.entrySet()) {
            consumerBackStackItem.addArgument(stringSerializableEntry.getKey(), stringSerializableEntry.getValue());
        }

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.card_flip_in, R.animator.card_flip_out)
                .replace(R.id.fragment_container_body, fragment_instance(
                        consumerBackStackItem.fragmentClass,
                        BodyFragment.HeaderUpdateRequest.ANIMATE,
                        consumerBackStackItem.getArgumentBundle()))
                .commit();
    }

    final public void header(String headerText, boolean secondary) {
        HeaderFragment fragment = getHeaderFragment();
        fragment.changeCaption(headerText, secondary, false);
    }

    final public void animateHeader(String headerText, boolean secondary) {
        HeaderFragment fragment = getHeaderFragment();
        fragment.changeCaption(headerText, secondary, true);
    }

    final public View build_actions(ViewGroup actionPanel) {
        if (getBodyFragment() == null) return null;
        return getBodyFragment().build_HeaderActionsView(actionPanel, getLayoutInflater());
    }

    protected final HeaderFragment getHeaderFragment() {
        return (HeaderFragment) getFragmentManager().findFragmentById(R.id.fragment_header);
    }

    protected final BodyFragment getBodyFragment() {
        return (BodyFragment) getFragmentManager().findFragmentById(R.id.fragment_container_body);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("back_stack", backStack);
    }


    private BodyFragment fragment_instance(Class<? extends BodyFragment> fragmentClass, BodyFragment.HeaderUpdateRequest request, Bundle arguments){
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



    public static class FragmentItem implements Serializable{

        private final Class<? extends BodyFragment> fragmentClass;
        private final Map<String,Serializable> argumentMap = new HashMap<>();

        public FragmentItem(Class<? extends BodyFragment> fragmentClass) {
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

        public FragmentItem addArgument(String key, Serializable value){
            argumentMap.put(key, value);
            return this;
        }

    }

    protected static class BodyFragmentChangeRequest {

        private final int in_animation;
        private final int out_animation;
        private final BodyFragment.HeaderUpdateRequest headerUpdateRequest;

        public BodyFragmentChangeRequest(int in_animation, int out_animation, BodyFragment.HeaderUpdateRequest headerUpdateRequest) {
            this.in_animation = in_animation;
            this.out_animation = out_animation;
            this.headerUpdateRequest = headerUpdateRequest;
        }
    }


}
