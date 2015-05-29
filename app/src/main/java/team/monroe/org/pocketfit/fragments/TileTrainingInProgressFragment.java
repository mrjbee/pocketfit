package team.monroe.org.pocketfit.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import org.monroe.team.android.box.utils.DisplayUtils;

import team.monroe.org.pocketfit.DashboardActivity;
import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;

public class TileTrainingInProgressFragment extends DashboardNoBottomTileFragment {

    private RoutineDay mDay;
    private Routine mRoutine;
    private PopupWindow mPopupWindow;

    @Override
    protected String getHeaderName() {
        return "Workout In Progress";
    }

    @Override
    protected int getTileLayoutId() {
        return R.layout.tile_content_trainining_in_progress;
    }


    @Override
    public void onMainButton() {}


    private void onDescriptionLink() {
        owner().openTrainingRunner();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view(R.id.text_days_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDescriptionLink();
            }
        });
        view_button(R.id.text_days_left).setText("Continue Workout");
        view(R.id.action_options).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow ==null) {
                    View view = activity().getLayoutInflater().inflate(R.layout.panel_popup_training, null);
                    view.findViewById(R.id.action_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPopupWindow.dismiss();
                            cancelTraining();
                        }
                    });
                    view.findViewById(R.id.action_stop).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPopupWindow.dismiss();
                            stopTraining(false);
                        }
                    });
                    mPopupWindow = new PopupWindow(view,
                            (int) DisplayUtils.dpToPx(200, getResources()),
                            (int) DisplayUtils.dpToPx(120, getResources()),
                            true);
                }
                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setFocusable(true);
                mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.tile_white));
                mPopupWindow.showAsDropDown(view(R.id.action_options));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (application().getTrainingPlan()!=null) {
            mRoutine = application().getTrainingPlan().getRoutine();
            mDay = application().getTrainingPlan().getRoutineDay();
            updateUI();
        }else {
            final DashboardActivity activity = owner();
            activity.runLastOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.switch_toRoutines();
                }
            }, 500);
        }
    }

    private void updateUI() {
        if (mRoutine == null){
            application().error(new IllegalStateException());
        }

        view_text(R.id.text_title).setText(mRoutine.title);
        view_text(R.id.text_description).setText(mDay.description);

        updateRoutineCover();
    }

    private void updateRoutineCover() {
        if (mRoutine.imageId != null) {
            String wasId = (String) view(R.id.image_cover, ImageView.class).getTag();
            if (mRoutine.imageId.equals(wasId)) return;
            view(R.id.image_cover, ImageView.class).setImageResource(R.drawable.covert_loading);
            application().loadToBitmap(mRoutine.imageId,
                    DisplayUtils.screenHeight(getResources()),
                    DisplayUtils.screenHeight(getResources()), new PocketFitApp.DataAction<Pair<String, Bitmap>>() {
                        @Override
                        public void data(Pair<String, Bitmap> data) {
                            view(R.id.image_cover, ImageView.class).setImageBitmap(data.second);
                            view(R.id.image_cover, ImageView.class).setTag(data.first);
                        }
                    });
        }else{
            view(R.id.image_cover, ImageView.class).setImageResource(R.drawable.no_covert);
        }
    }


    public void stopTraining(boolean completelyDone) {
        application().getTrainingPlan().setTrainingPlanListener(null);
        application().stopTraining(completelyDone);
        owner().switch_workoutTile();
    }

    public void cancelTraining() {
        application().getTrainingPlan().setTrainingPlanListener(null);
        application().cancelTraining();
        owner().switch_workoutTile();
    }

}
