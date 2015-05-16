package team.monroe.org.pocketfit.fragments;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.monroe.team.android.box.app.ui.GenericListViewAdapter;
import org.monroe.team.android.box.app.ui.GetViewImplementation;
import org.monroe.team.android.box.data.Data;

import java.util.List;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.Routine;

public class RoutinesFragment extends BodyFragment {

    private Data.DataChangeObserver<List> observer_routines;
    private GenericListViewAdapter<Routine, GetViewImplementation.ViewHolder<Routine>> mRoutinesAdapter;
    private ListView mRoutineListView;
    private View mNoItemsView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_routines;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view(R.id.panel_new_routine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                application().function_createEmptyRoutine(observe_function(State.PAUSE, new PocketFitApp.DataAction<String>() {
                    @Override
                    public void data(String routine) {
                        owner().open_Routine(routine);
                    }
                }));
            }
        });

       mRoutinesAdapter = new GenericListViewAdapter<Routine, GetViewImplementation.ViewHolder<Routine>>(activity(),new GetViewImplementation.ViewHolderFactory<GetViewImplementation.ViewHolder<Routine>>() {
            @Override
            public GetViewImplementation.ViewHolder<Routine> create(final View convertView) {
                return new GetViewImplementation.GenericViewHolder<Routine>() {

                    TextView caption = (TextView) convertView.findViewById(R.id.item_caption);
                    TextView text = (TextView) convertView.findViewById(R.id.item_sub_caption);

                    @Override
                    public void update(Routine routine, int position) {
                        caption.setText(routine.title);
                        text.setText(routine.description);
                    }

                };
            }
        },R.layout.item_debug);

        mRoutineListView = view_list(R.id.list_routine);
        mRoutineListView.setAdapter(mRoutinesAdapter);
        mRoutineListView.setVisibility(View.INVISIBLE);

        mNoItemsView = view(R.id.panel_no_items);
        mNoItemsView.setVisibility(View.VISIBLE);

        mRoutineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Routine routine = mRoutinesAdapter.getItem(position);
                owner().open_Routine(routine.id);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        observer_routines = observe_data_change(State.STOP, new Data.DataChangeObserver<List>() {
            @Override
            public void onDataInvalid() {
                fetch_Routines();
            }

            @Override
            public void onData(List list) {
            }
        });
        application().data_routines().addDataChangeObserver(observer_routines);
        fetch_Routines();
    }

    @Override
    public void onStop() {
        super.onStop();
        application().data_routines().removeDataChangeObserver(observer_routines);
    }

    private void fetch_Routines() {
        application().data_routines().fetch(true, observe_data_fetch(State.STOP, new PocketFitApp.DataAction<List>() {
            @Override
            public void data(List data) {
                List<Routine> routineList = data;
                if (data.isEmpty()){
                    mRoutineListView.setVisibility(View.INVISIBLE);
                    mNoItemsView.setVisibility(View.VISIBLE);
                    mRoutinesAdapter.clear();
                    mRoutinesAdapter.notifyDataSetChanged();
                }else {
                    mRoutineListView.setVisibility(View.VISIBLE);
                    mNoItemsView.setVisibility(View.INVISIBLE);
                    mRoutinesAdapter.clear();
                    mRoutinesAdapter.addAll(routineList);
                    mRoutinesAdapter.notifyDataSetChanged();
                }
            }
        }));
    }

    @Override
    protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    protected String getHeaderName() {
        return "Workout Routines";
    }

}
