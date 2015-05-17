package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.monroe.team.android.box.app.ui.GenericListViewAdapter;
import org.monroe.team.android.box.app.ui.GetViewImplementation;

import java.util.List;

import team.monroe.org.pocketfit.R;

public abstract class GenericListFragment<ItemsType> extends BodyFragment{

    private GenericListViewAdapter<ItemsType, GetViewImplementation.ViewHolder<ItemsType>> mAdapter;
    private ListView mListView;
    private View mNoItemsView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ((TextView)view.findViewById(R.id.caption_new_item)).setText(getNewItemCaption());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new GenericListViewAdapter<ItemsType, GetViewImplementation.ViewHolder<ItemsType>>(activity(),new GetViewImplementation.ViewHolderFactory<GetViewImplementation.ViewHolder<ItemsType>>() {
            @Override
            public GetViewImplementation.ViewHolder<ItemsType> create(final View convertView) {
                return new GetViewImplementation.GenericViewHolder<ItemsType>() {

                    TextView caption = (TextView) convertView.findViewById(R.id.item_caption);
                    TextView subCaption = (TextView) convertView.findViewById(R.id.item_sub_caption);
                    TextView text = (TextView) convertView.findViewById(R.id.item_text);

                    @Override
                    public void update(ItemsType itemsType, int position) {
                        caption.setText(item_caption(itemsType));
                        subCaption.setText(item_subCaption(itemsType));
                        text.setText(item_text(itemsType));
                    }
                };
            }
        }, R.layout.fragment_generic_list);

        mListView = view(R.id.list_items, ListView.class);
        mListView.setAdapter(mAdapter);
        mListView.setVisibility(View.INVISIBLE);

        mNoItemsView = view(R.id.panel_no_items);
        mNoItemsView.setVisibility(View.VISIBLE);
    }

    protected abstract String item_text(ItemsType item);
    protected abstract String item_subCaption(ItemsType item);
    protected abstract String item_caption(ItemsType item);

    protected abstract String getNewItemCaption();

    @Override
    protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_generic_list;
    }

    protected void updateItems(List<ItemsType> data){
        if (data.isEmpty()){
            mListView.setVisibility(View.INVISIBLE);
            mNoItemsView.setVisibility(View.VISIBLE);
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
        }else {
            mListView.setVisibility(View.VISIBLE);
            mNoItemsView.setVisibility(View.INVISIBLE);
            mAdapter.clear();
            mAdapter.addAll(data);
            mAdapter.notifyDataSetChanged();
        }
    }

}
