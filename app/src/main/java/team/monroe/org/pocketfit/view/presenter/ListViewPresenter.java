package team.monroe.org.pocketfit.view.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

public abstract class ListViewPresenter<DataType> extends ViewPresenter<ViewGroup> {

    public ListViewPresenter(ViewGroup rootView) {
        super(rootView);
    }

    public void synchronizeItems(List<DataType> items) {
        if (items == null) items = Collections.emptyList();
        for (int i = 0; i < items.size(); i++){
            View childView = getRootView().getChildAt(i);
            if (childView == null){
                View presentationView = buildPresentationView(items, i);
                getRootView().addView(presentationView, i);
            }else {
               DataType dataType = (DataType) childView.getTag();
               String newDataId = data_to_id(items.get(i));
               String wasDataId = data_to_id(dataType);
               if (!newDataId.equals(wasDataId)){
                   //change items
                   View presentationView = buildPresentationView(items, i);
                   getRootView().removeViewAt(i);
                   getRootView().addView(presentationView, i);
               }
            }
        }
        if (items.size() < getRootView().getChildCount()){
            int overCount = getRootView().getChildCount() - items.size();
            for (int i = 0; i < overCount; i++){
                //remove child at index items.size()
                getRootView().removeViewAt(items.size());
            }
        }
    }

    private View buildPresentationView(List<DataType> items, int index) {
        DataType dataType = items.get(index);
        return buildPresentationView(dataType, index);
    }

    private View buildPresentationView(DataType dataType, int index) {
        LayoutInflater inflater = LayoutInflater.from(getRootView().getContext());
        View presentationView = data_to_view(index,dataType, getRootView(), inflater);
        presentationView.setTag(dataType);
        return presentationView;
    }

    protected abstract View data_to_view(int index, DataType dataType, ViewGroup owner, LayoutInflater inflater);


    //item 1
    //item 2
    //item 3

    protected abstract String data_to_id(DataType dataType);
}
