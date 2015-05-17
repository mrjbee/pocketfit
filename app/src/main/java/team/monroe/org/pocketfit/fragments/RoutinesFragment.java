package team.monroe.org.pocketfit.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.monroe.team.android.box.app.ui.GenericListViewAdapter;
import org.monroe.team.android.box.app.ui.GetViewImplementation;
import org.monroe.team.android.box.app.ui.SlideTouchGesture;
import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.*;
import org.monroe.team.android.box.data.Data;
import org.monroe.team.android.box.utils.DisplayUtils;

import java.util.List;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.view.SlideOffListView;

public class RoutinesFragment extends BodyFragment {

    private Data.DataChangeObserver<List> observer_routines;
    private GenericListViewAdapter<Routine, GetViewImplementation.ViewHolder<Routine>> mRoutinesAdapter;
    private SlideOffListView mRoutineListView;
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
                    TextView subCaption = (TextView) convertView.findViewById(R.id.item_sub_caption);
                    TextView text = (TextView) convertView.findViewById(R.id.item_text);
                    ImageView imageView = (ImageView) convertView.findViewById(R.id.item_image);
                    View panelDetails = convertView.findViewById(R.id.item_panel_details);

                    AppearanceController slidePanelAC = animateAppearance(panelDetails,xSlide(0f,100f))
                            .showAnimation(duration_constant(100), interpreter_accelerate_decelerate()).build();

                    @Override
                    public void cleanup() {
                        slidePanelAC.showWithoutAnimation();
                        imageView.setImageResource(R.drawable.covert_loading);
                    }

                    @Override
                    public void update(final Routine routine, int position) {
                        caption.setText(routine.title);
                        text.setText(routine.description);
                        //TODO: add routine real training day count
                        subCaption.setText(0+" training days");
                        panelDetails.setOnTouchListener(new SlideTouchGesture(DisplayUtils.dpToPx(100, getResources()),
                                SlideTouchGesture.Axis.X_LEFT) {
                            @Override
                            protected float applyFraction() {
                                return 0.95f;
                            }

                            @Override
                            protected void onEnd(float x, float y, float slideValue, float fraction) {
                                mRoutineListView.disabled = false;
                            }

                            @Override
                            protected void onApply(float x, float y, float slideValue, float fraction) {
                                owner().open_Routine(routine.id);
                            }

                            @Override
                            protected void onProgress(float x, float y, float slideValue, float fraction) {
                                if (Math.abs(slideValue) < 80) return;
                                mRoutineListView.disabled = true;
                                panelDetails.setTranslationX(-(slideValue));
                            }
                            @Override
                            protected void onCancel(float x, float y, float slideValue, float fraction) {
                                super.onCancel(x, y, slideValue, fraction);
                                slidePanelAC.show();
                            }
                        });
                        if (routine.imageId == null){
                            imageView.setImageResource(R.drawable.no_covert);
                        }else{
                            final String finalImageId = routine.imageId;
                            application().loadToBitmap(routine.imageId,
                                    DisplayUtils.dpToPx(100, getResources()),
                                    DisplayUtils.dpToPx(100, getResources()),
                                    new PocketFitApp.DataAction<Pair<String, Bitmap>>() {
                                        @Override
                                        public void data(Pair<String, Bitmap> data) {
                                            if (finalImageId.equals(data.first)){
                                                imageView.setImageBitmap(data.second);
                                            }
                                        }
                                    });
                        }
                    }

                };
            }
        },R.layout.item_routine);

        mRoutineListView = view(R.id.list_routine, SlideOffListView.class);
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
