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
import team.monroe.org.pocketfit.view.DayFoodSummaryPresenter;

public class PageFoodFragment extends DefaultPageFragment {

    private Date mDate;
    private ViewPager mDayFoodPager;
    private int mMaxDayPosition;
    private Date mTodayDate;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        buildHeader();
        hideMainButton(null);
        mMaxDayPosition = 360;
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.today());
        mTodayDate = calendar.getTime();
        updateDateCaption(mTodayDate);

        mDayFoodPager = new ViewPager(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        view(R.id.panel_day, ViewGroup.class).addView(mDayFoodPager,layoutParams);
        mDayFoodPager.setAdapter(new PagerAdapter() {

            private HashMap<Integer, ViewGroup> dayFoodViewPerPositionHashMap = new HashMap<Integer, ViewGroup>();
            private List<ViewGroup> notUsedViewList = new ArrayList<ViewGroup>();

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ViewGroup dayFoodView = dayFoodViewPerPositionHashMap.get(position);
                DayFoodSummaryPresenter dayFoodPresenter = null;
                if (dayFoodView == null) {
                    if (!notUsedViewList.isEmpty()) {
                        dayFoodView = notUsedViewList.remove(0);
                        dayFoodPresenter = (DayFoodSummaryPresenter) dayFoodView.getTag();
                    } else {
                        dayFoodView = (ViewGroup) activity().getLayoutInflater().inflate(R.layout.panel_food_summaries, container, false);
                        dayFoodPresenter = new DayFoodSummaryPresenter(dayFoodView, application());
                        dayFoodView.setTag(dayFoodPresenter);
                    }
                    Date date = getDateByPosition(position);
                    dayFoodPresenter.init(date);
                    dayFoodViewPerPositionHashMap.put(position, dayFoodView);
                }else {
                    dayFoodPresenter = (DayFoodSummaryPresenter) dayFoodView.getTag();
                }
                container.addView(dayFoodView);
                return dayFoodView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                View view = (View) object;
                DayFoodSummaryPresenter presenter = (DayFoodSummaryPresenter) view.getTag();
                presenter.deinit();
                container.removeView(view);
                dayFoodViewPerPositionHashMap.remove(position);
                if (notUsedViewList.size() < 5) {
                    notUsedViewList.add((ViewGroup) view);
                }else {
                    presenter.destroy();
                }
            }


            @Override
            public int getCount() {
                return mMaxDayPosition + 1;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
        mDayFoodPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateDateCaption(getDateByPosition(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mDayFoodPager.setCurrentItem(mMaxDayPosition);
    }

    private void buildHeader() {
        View headerActionsView = activity().getLayoutInflater().inflate(R.layout.actions_history, null);
        headerActionsView.findViewById(R.id.action_today).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDayFoodPager.setCurrentItem(mMaxDayPosition);
            }
        });
        configureHeader("My Calories Diary", headerActionsView);
    }

    private Date getDateByPosition(int position) {
        if (position == mMaxDayPosition){
            return mTodayDate;
        }else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mTodayDate);
            calendar.add(Calendar.DAY_OF_YEAR, position - mMaxDayPosition);
            return calendar.getTime();
        }
    }

    DateFormat dateFormat = new SimpleDateFormat("dd, MMMM yyyy");
    private void updateDateCaption(Date today) {
        mDate = today;
        view_text(R.id.text_day).setText(dateFormat.format(mDate));
    }

    @Override
    protected int getTileLayoutId() {
        return R.layout.page_content_food;
    }

    @Override
    public void onMainButton() {
        hideMainButton(new Runnable() {
            @Override
            public void run() {
                owner().openMealsSelect();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMainButton();
    }

    @Override
    public void updateMainButton() {
        showMainButton(R.drawable.round_btn_plus, null);
    }
}
