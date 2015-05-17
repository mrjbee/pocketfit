package team.monroe.org.pocketfit.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.monroe.team.android.box.app.FragmentSupport;
import org.monroe.team.android.box.utils.DisplayUtils;

import java.io.FileNotFoundException;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.Routine;

public class RoutineEditorFragment extends BodyFragment {

    private Routine mRoutine;

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
        return R.layout.fragment_routine;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view(R.id.image_select_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                owner().performImageSelection();
            }
        });
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
        final String routineId = getArguments().getString("routine_id");
        if (routineId == null){
            application().error("No routine id");
        }
        application().function_getRoutine(routineId, observe_function(State.STOP, new PocketFitApp.DataAction<Routine>() {
            @Override
            public void data(Routine routine) {
                mRoutine = routine;
                if (mRoutine == null){
                    mRoutine = new Routine(routineId);
                }
                view_text(R.id.edit_title).setText(mRoutine.title);
                view_text(R.id.edit_description).setText(mRoutine.description);
                if (mRoutine.imageId != null){
                    restoreImage(mRoutine.imageId);
                }else{
                    view(R.id.image_cover, ImageView.class).setImageResource(R.drawable.no_covert);
                }
            }
        }));
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mRoutine != null){
            mRoutine.title = view_text(R.id.edit_title).getText().toString();
            mRoutine.description = view_text(R.id.edit_description).getText().toString();
            application().function_updateRoutine(mRoutine, observe_function(State.ANY, new PocketFitApp.DataAction<Routine>() {
                @Override
                public void data(Routine data) {
                    //Do nothing here
                }
            }));
        }
    }
}
