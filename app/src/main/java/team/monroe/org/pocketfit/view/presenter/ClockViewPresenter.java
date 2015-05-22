package team.monroe.org.pocketfit.view.presenter;

import android.os.Looper;
import android.widget.TextView;

import org.monroe.team.corebox.utils.DateUtils;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ClockViewPresenter extends ViewPresenter<TextView>{

    private long mStartTime;
    private Timer mTimer;
    private final android.os.Handler uiHandler = new android.os.Handler(Looper.getMainLooper());

    public ClockViewPresenter(TextView rootView) {
        super(rootView);
    }

    public void startClock(Date fromTime){
        resetClock();
        mStartTime = fromTime.getTime();
        updateView();
        mTimer = new Timer("clock", true);
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateView();
                    }
                });
            }
        }, 0, 300);
    }

    private void updateView() {
        long delta = System.currentTimeMillis() - mStartTime;
        //days,hours,minutes,seconds, periodMs
        long[] values = DateUtils.splitperiod(delta);
        String timeString = string(values[1], 2)+":"+ string(values[2], 2)+":"+ string(values[3], 2);
        getRootView().setText(timeString);
    }

    private String string(long value, int digits) {
        String answer = Long.toString(value);
        for (int i = answer.length(); i < digits; i++){
            answer = "0"+answer;
        }
        return answer;
    }


    public void resetClock() {
        if (mTimer!= null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        getRootView().setText("00:00:00");
    }
}
