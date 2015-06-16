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
import team.monroe.org.pocketfit.RoutinesActivity;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;
import team.monroe.org.pocketfit.uc.UpdateRoutineDay;
import team.monroe.org.pocketfit.view.presenter.ListViewPresenter;

public class MealEditorFragment extends BodyFragment<RoutinesActivity> {

    //private ListViewPresenter<RoutineDay> listViewPresenter;

    @Override
    protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    protected String getHeaderName() {
        return "Edit Meal";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_meal;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view(R.id.image_select_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  ownerContract(RoutinesActivity.class).performImageSelection();
            }
        });
        /*listViewPresenter = new ListViewPresenter<RoutineDay>(view(R.id.panel_days, ViewGroup.class)) {
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

                view.findViewById(R.id.item_action).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ownerContract(RoutinesActivity.class).open_RoutineDay(mRoutine.id, routineDay.id);
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
        */
    }

    @Override
    public void onStart() {
        super.onStart();
        final String meailId = super.getStringArgument("meal_id");
        if (meailId == null){
            application().error("No meal id");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
