package team.monroe.org.pocketfit.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import org.monroe.team.android.box.utils.DisplayUtils;
import org.monroe.team.corebox.log.L;
import org.monroe.team.corebox.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import team.monroe.org.pocketfit.R;

public class CalendarView extends GridLayout {

    private int mCellSize;
    private Calendar mCalendar;
    private Date mStartDate;

    public CalendarView(Context context) {
        super(context);
        init();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        if (mCalendar != null){
            release();
        }

        mCellSize = (int) DisplayUtils.dpToPx(35, getResources());
        mCalendar = Calendar.getInstance();
        for (int i = 0; i < 7;i++){
            mCalendar.set(Calendar.DAY_OF_WEEK, mCalendar.getFirstDayOfWeek()+i);
            String name = mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
            createDayCaptionCell(name, i);
        }
        setMonth(DateUtils.today());
        requestLayout();
    }

    public void setMonth(Date month) {

        if (getChildCount() > 6) {
            removeViews(7, getChildCount() - 7);
        }

        mStartDate = month;
        mCalendar.setTime(mStartDate);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int offset = mCalendar.get(Calendar.DAY_OF_WEEK);
        int days = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i=offset; i< (offset+days);i++){
            mCalendar.set(Calendar.DAY_OF_MONTH, i);
            Date date = mCalendar.getTime();
            createDateCell(date, i - offset + 1 ,i + 6);
        }
    }

    public void release() {
        removeAllViews();
    }

    private void createDateCell(Date date, int dayOfMonth, int position) {
        View view =  LayoutInflater.from(getContext()).inflate(R.layout.calendar_day_caption, null);
        ((TextView)view.findViewById(R.id.caption)).setText(" "+dayOfMonth);
        LayoutParams params = buildLayoutParams(position);
        view.setLayoutParams(params);
        addView(view, getChildCount(), params);
    }

    private void createDayCaptionCell(String name, int position) {
        View view =  LayoutInflater.from(getContext()).inflate(R.layout.calendar_day_caption, null);
        ((TextView)view.findViewById(R.id.caption)).setText(name);
        LayoutParams params = buildLayoutParams(position);
        view.setLayoutParams(params);
        addView(view, position, params);
    }

    private LayoutParams buildLayoutParams(int position) {
        int row = getRowByPosition(position);
        int column = getColumnByPosition(position);
        L.DEBUG.d("Position to row x column:"+position +"="+row+" x "+column);
        LayoutParams params = new LayoutParams(GridLayout.spec(row), GridLayout.spec(column));
        params.width = mCellSize;
        params.height = mCellSize;
        return params;
    }

    private int getColumnByPosition(int position) {
        return position % 7;
    }

    private int getRowByPosition(int position) {
        return position / 7;
    }
}
