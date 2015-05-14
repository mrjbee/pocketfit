package team.monroe.org.pocketfit.view.presenter;
import android.view.View;

public class ViewPresenter {

    private final View rootView;

    public ViewPresenter(View rootView) {
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

    public View getRootView() {
        return rootView;
    }
}
