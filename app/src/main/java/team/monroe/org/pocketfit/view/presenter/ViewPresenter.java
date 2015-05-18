package team.monroe.org.pocketfit.view.presenter;
import android.view.View;

public class ViewPresenter <RootViewType extends View> {

    private final RootViewType rootView;

    public ViewPresenter(RootViewType rootView) {
        this.rootView = rootView;
    }

    protected <ViewType extends View> ViewType find(int id){
        return (ViewType) rootView.findViewById(id);
    }

    public void setVisibility(int visibility){
        rootView.setVisibility(visibility);
    }

    public void hide(){
        setVisibility(View.INVISIBLE);
    }

    public void gone(){
        setVisibility(View.GONE);
    }

    public void show(){
        setVisibility(View.VISIBLE);
    }

    public RootViewType getRootView() {
        return rootView;
    }
}
