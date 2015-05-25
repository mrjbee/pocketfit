package team.monroe.org.pocketfit.view.presenter;

import android.view.View;
import android.widget.NumberPicker;

import team.monroe.org.pocketfit.R;

public class TimePickPresenter extends ViewPresenter<View>{


    private final NumberPicker mMinutesPicker;
    private final NumberPicker mSecondsPicker;

    public TimePickPresenter(View rootView) {
        super(rootView);
        mMinutesPicker = (NumberPicker) rootView.findViewById(R.id.picker_minutes);
        mSecondsPicker = (NumberPicker) rootView.findViewById(R.id.picker_seconds);

        mMinutesPicker.setMinValue(0);
        mMinutesPicker.setMaxValue(5 * 60);

        mSecondsPicker.setMinValue(0);
        mSecondsPicker.setMaxValue(59);
    }


    public void setMinutes(Float existingTime){
        if (existingTime == null) return;
        int minutes = (int)((float)existingTime);
        int seconds = Math.round((existingTime - minutes) * 60f);

        mMinutesPicker.setValue(minutes);
        mSecondsPicker.setValue(seconds);

    }

    public Float getMinutes(){
        float minutes = mMinutesPicker.getValue() + ((float) mSecondsPicker.getValue()/60f);
        return minutes;
    }
}
