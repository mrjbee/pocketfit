package team.monroe.org.pocketfit.fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.monroe.team.android.box.utils.DisplayUtils;

import java.io.FileNotFoundException;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.RoutineSetupActivity;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;
import team.monroe.org.pocketfit.uc.UpdateRoutineDay;
import team.monroe.org.pocketfit.view.presenter.ListViewPresenter;

public class RoutineEditorFragment extends BodyFragment<RoutineSetupActivity> {

    private Routine mRoutine;
    private ListViewPresenter<RoutineDay> listViewPresenter;

    @Override
    protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    protected String getHeaderName() {
        return "Edit Routine";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_routine;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view(R.id.image_select_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                owner(RoutineSetupActivity.class).performImageSelection();
            }
        });
        listViewPresenter = new ListViewPresenter<RoutineDay>(view(R.id.panel_days, ViewGroup.class)) {
            @Override
            protected View data_to_view(int index, final RoutineDay routineDay, final ViewGroup owner, LayoutInflater inflater) {
                View view = inflater.inflate(R.layout.item_day,owner, false);
                ((TextView)view.findViewById(R.id.item_caption)).setText(routineDay.exerciseList.size() + " exercises");
                ((TextView)view.findViewById(R.id.item_sub_caption)).setText("Rest " +routineDay.restDays + " days");
                ((TextView)view.findViewById(R.id.item_text)).setText(routineDay.description);
                ((TextView)view.findViewById(R.id.item_index)).setText(""+(index + 1));
                if (index == 0){
                    view.findViewById(R.id.item_image).setBackgroundResource(R.drawable.step_top);
                } else if (index == mRoutine.trainingDays.size()-1){
                    view.findViewById(R.id.item_image).setBackgroundResource(R.drawable.step_bottom);
                }else {
                    view.findViewById(R.id.item_image).setBackgroundResource(R.drawable.step);
                }

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        owner(RoutineSetupActivity.class).open_RoutineDay(mRoutine.id, routineDay.id);
                    }
                });
                view.findViewById(R.id.item_trash).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        application().function_updateRoutineDay(routineDay,mRoutine.id, UpdateRoutineDay.RoutineDayUpdate.INDEX_DELETE, new PocketFitApp.DataAction<Void>() {
                            @Override
                            public void data(Void data) {
                                updateRoutineDetails(mRoutine.id);
                            }
                        });
                    }
                });
                return view;
            }

            @Override
            protected String data_to_id(RoutineDay routineDay) {
                return routineDay.id;
            }
        };
    }

    @Override
    public void onImageResult(Uri uri) {
        view(R.id.image_cover, ImageView.class).setImageResource(R.drawable.covert_loading);
        try {
            application().saveImage(activity().getContentResolver().openInputStream(uri), observe_data_action(State.STOP, new PocketFitApp.DataAction<String>() {
                @Override
                public void data(String imageId) {
                    restoreImage(imageId);
                    mRoutine.imageId = imageId;
                }
            }));
        } catch (FileNotFoundException e) {
            view(R.id.image_cover, ImageView.class).setImageResource(R.drawable.no_covert);
            Toast.makeText(activity(),"Image not found. Please try again", Toast.LENGTH_LONG).show();
        }
    }

    private void restoreImage(String imageId) {
        application().loadToBitmap(imageId, DisplayUtils.dpToPx(300, getResources()), DisplayUtils.dpToPx(300,getResources()), observe_data_action(State.STOP,new PocketFitApp.DataAction<Pair<String, Bitmap>>() {
            @Override
            public void data(Pair<String, Bitmap> data) {
                if (!data.first.equals(mRoutine.imageId)) return;
                view(R.id.image_cover, ImageView.class).setImageBitmap(data.second);
            }
        }));
    }


    @Override
    public void onStart() {
        super.onStart();
        final String routineId = super.getStringArgument("routine_id");
        if (routineId == null){
            application().error("No routine id");
        }
        updateRoutineDetails(routineId);
    }

    private void updateRoutineDetails(final String routineId) {
        application().function_getRoutine(routineId, observe_function(State.STOP, new PocketFitApp.DataAction<Routine>() {
            @Override
            public void data(Routine routine) {
                mRoutine = routine;
                if (mRoutine == null){
                    mRoutine = new Routine(routineId);
                }
                view_text(R.id.edit_title).setText(mRoutine.title);
                view_text(R.id.edit_description).setText(mRoutine.description);
                view(R.id.button_add_day).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        application().function_createId("day",observe_function(State.PAUSE, new PocketFitApp.DataAction<String>() {
                            @Override
                            public void data(String id) {
                                String title = view_text(R.id.edit_title).getText().toString();
                                if (!title.trim().isEmpty()){
                                    owner(RoutineSetupActivity.class).open_RoutineDay(mRoutine.id, id);
                                }else{
                                    Toast.makeText(getActivity(), "Please add title first", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }));
                    }
                });
                listViewPresenter.synchronizeItems(mRoutine.trainingDays);
                if (mRoutine.imageId != null){
                    restoreImage(mRoutine.imageId);
                }else{
                    view(R.id.image_cover, ImageView.class).setImageResource(R.drawable.no_covert);
                }

                //Training days details
                int totalCycle = 0;
                int totalDays = 0;

                for (RoutineDay trainingDay : mRoutine.trainingDays) {
                    totalCycle += 1+trainingDay.restDays;
                    totalDays ++;
                }

                view_text(R.id.text_total_cycle).setText(totalCycle + " days");
                view_text(R.id.text_total_days).setText(totalDays+"");
            }
        }));
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mRoutine != null){
            mRoutine.title = view_text(R.id.edit_title).getText().toString();
            mRoutine.description = view_text(R.id.edit_description).getText().toString();
            application().function_updateRoutine(mRoutine, observe_function(State.ANY, new PocketFitApp.DataAction<Void>() {
                @Override
                public void data(Void nothing) {
                    //Do nothing here
                }
            }));
        }
    }
}
