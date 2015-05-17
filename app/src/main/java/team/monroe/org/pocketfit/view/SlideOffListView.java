package team.monroe.org.pocketfit.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class SlideOffListView extends ListView {
    public SlideOffListView(Context context) {
        super(context);
    }

    public SlideOffListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlideOffListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SlideOffListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public boolean disabled = false;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return disabled?false:super.onInterceptTouchEvent(ev);
    }
}
