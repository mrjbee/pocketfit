package team.monroe.org.pocketfit;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.monroe.team.corebox.utils.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import team.monroe.org.pocketfit.fragments.DefaultPageFragment;
import team.monroe.org.pocketfit.view.CalendarView;

public class PageHistoryFragment extends DefaultPageFragment {

    private Date mDate;
    private ViewPager mCalendarPager;
    private int mMonthMaxPosition;
    private Date mCurrentMonthDate;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        buildHeader();
        hideMainButton(null);
        mMonthMaxPosition = 50;
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.today());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        mCurrentMonthDate = calendar.getTime();

        //view(R.id.calendar, CalendarView.class).init();
        /*view(R.id.text_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mDate);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.MONTH,1);
                Date date = calendar.getTime();
                updateMonthCaption(date);
            }
        });*/

        updateMonthCaption(DateUtils.today());
        mCalendarPager = new ViewPager(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        view(R.id.panel_calendar, ViewGroup.class).addView(mCalendarPager,layoutParams);
        mCalendarPager.setAdapter(new PagerAdapter() {

            private HashMap<Integer, ViewGroup> calendarViewPerPositionHashMap = new HashMap<Integer, ViewGroup>();
            private List<ViewGroup> notUsedCalendarViewList = new ArrayList<ViewGroup>();

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ViewGroup calendarPanel = calendarViewPerPositionHashMap.get(position);
                if (calendarPanel == null) {
                    if (!notUsedCalendarViewList.isEmpty()){
                        calendarPanel = notUsedCalendarViewList.remove(0);
                    }else {
                        calendarPanel = (ViewGroup) activity().getLayoutInflater().inflate(R.layout.panel_calendar, container, false);;
                    }
                    Date monthDate = getMonthDateByPosition(position);
                    ((CalendarView)calendarPanel.getChildAt(0)).setMonth(monthDate);
                    calendarViewPerPositionHashMap.put(position, calendarPanel);
                }
                container.addView(calendarPanel);
                return calendarPanel;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
                calendarViewPerPositionHashMap.remove(position);
                if (notUsedCalendarViewList.size() < 5){
                    notUsedCalendarViewList.add((ViewGroup) object);
                }
            }


            @Override
            public int getCount() {
                return mMonthMaxPosition + 1;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
        mCalendarPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateMonthCaption(getMonthDateByPosition(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        mCalendarPager.setCurrentItem(mMonthMaxPosition);
    }

    private void buildHeader() {
        View headerActionsView = activity().getLayoutInflater().inflate(R.layout.actions_history, null);
        headerActionsView.findViewById(R.id.action_today).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarPager.setCurrentItem(mMonthMaxPosition);
            }
        });
        configureHeader("My Fit Diary", headerActionsView);
    }

    private Date getMonthDateByPosition(int position) {
        if (position == mMonthMaxPosition){
            return mCurrentMonthDate;
        }else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mCurrentMonthDate);
            calendar.add(Calendar.MONTH, position - mMonthMaxPosition);
            return calendar.getTime();
        }
    }

    DateFormat dateFormat = new SimpleDateFormat("MMMM, yyyy");
    private void updateMonthCaption(Date today) {
        mDate = today;
        view_text(R.id.text_month).setText(dateFormat.format(mDate));
        //view(R.id.calendar, CalendarView.class).setMonth(mDate);
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
        hideMainButton(null);
    }
}
