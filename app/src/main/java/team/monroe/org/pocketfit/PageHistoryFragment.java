package team.monroe.org.pocketfit;

import android.os.Bundle;
import android.view.View;

import org.monroe.team.corebox.utils.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import team.monroe.org.pocketfit.fragments.DefaultPageFragment;
import team.monroe.org.pocketfit.view.CalendarView;

public class PageHistoryFragment extends DefaultPageFragment {

    private Date mDate;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        configureHeader("My Fit Diary", null);
        showMainButton(R.drawable.round_btn_pen, null);
        view(R.id.calendar, CalendarView.class).init();
        view(R.id.text_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mDate);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.MONTH,1);
                Date date = calendar.getTime();
                updateCalendar(date);
            }
        });
        updateCalendar(DateUtils.today());
    }

    DateFormat dateFormat = new SimpleDateFormat("MMMM, yyyy");
    private void updateCalendar(Date today) {
        mDate = today;
        view_text(R.id.text_month).setText(dateFormat.format(mDate));
        view(R.id.calendar, CalendarView.class).setMonth(mDate);
    }

    @Override
    protected int getTileLayoutId() {
        return R.layout.page_content_history;
    }

    @Override
    public void onMainButton() {

    }

    @Override
    public void updateMainButton() {
        showMainButton(R.drawable.round_btn_pen, null);
    }
}
