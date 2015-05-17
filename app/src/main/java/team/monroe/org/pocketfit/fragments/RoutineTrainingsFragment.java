package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.View;

import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.RoutineDay;

public class RoutineTrainingsFragment extends GenericListFragment<RoutineDay> {

    private String mRoutineId;

    @Override
    protected String item_text(RoutineDay item) {
        return null;
    }

    @Override
    protected String item_subCaption(RoutineDay item) {
        return null;
    }

    @Override
    protected String item_caption(RoutineDay item) {
        return null;
    }

    @Override
    protected String getNewItemCaption() {
        return "Do you want to setup a training?";
    }

    @Override
    protected String getHeaderName() {
        return "Manage Trainings";
    }

}
