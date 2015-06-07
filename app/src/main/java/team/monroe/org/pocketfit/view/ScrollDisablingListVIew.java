package team.monroe.org.pocketfit.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by mrjbee on 6/7/15.
 */
public class ScrollDisablingListVIew extends ListView {
    public ScrollDisablingListVIew(Context context) {
        super(context);
    }

    public ScrollDisablingListVIew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollDisablingListVIew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScrollDisablingListVIew(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            ev.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.dispatchTouchEvent(ev);
    }
}
