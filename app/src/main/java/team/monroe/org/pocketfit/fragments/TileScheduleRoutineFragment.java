package team.monroe.org.pocketfit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import org.monroe.team.android.box.app.ui.GenericListViewAdapter;
import org.monroe.team.android.box.app.ui.GetViewImplementation;
import org.monroe.team.android.box.data.Data;
import org.monroe.team.corebox.utils.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.RoutineDay;
import team.monroe.org.pocketfit.presentations.RoutineSchedule;

public class TileScheduleRoutineFragment extends DashboardTileFragment {

    private Data.DataChangeObserver<RoutineSchedule> observer_activeRoutineObserver;
    private RoutineSchedule mSchedule;
    private GenericListViewAdapter<Day, GetViewImplementation.ViewHolder<Day>> mDayListViewAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDayListViewAdapter = new GenericListViewAdapter<Day, GetViewImplementation.ViewHolder<Day>>(getActivity(),new GetViewImplementation.ViewHolderFactory<GetViewImplementation.ViewHolder<Day>>() {


            @Override
            public GetViewImplementation.ViewHolder<Day> create(final View convertView) {
                return new GetViewImplementation.GenericViewHolder<Day>() {

                    TextView caption = (TextView) convertView.findViewById(R.id.item_caption);
                    TextView text = (TextView) convertView.findViewById(R.id.item_text);
                    TextView shortDay = (TextView) convertView.findViewById(R.id.item_day);
                    ImageView dayBackground = (ImageView) convertView.findViewById(R.id.item_day_background);

                    @Override
                    public void update(final Day day, int position) {

                        if (day.id != null){
                            shortDay.setTextColor(getResources().getColor(R.color.text_color_day_short_training));
                            text.setVisibility(View.VISIBLE);
                            dayBackground.setImageResource(R.drawable.day_training);
                            caption.setTextColor(getResources().getColor(R.color.text_color_date_training));
                            /*button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onRoutineDaySelect(day.id);
                                }
                            });*/
                        }else{
                            shortDay.setTextColor(getResources().getColor(R.color.text_color_day_short));
                            dayBackground.setImageResource(R.drawable.day_not_training);
                            text.setVisibility(View.INVISIBLE);
                            caption.setTextColor(getResources().getColor(R.color.text_color_date));
                        }

                        if (day.dayDateString.equals("Today")){
                            shortDay.setTextColor(getResources().getColor(R.color.text_color_day_short_training));
                            dayBackground.setImageResource(R.drawable.day_today);
                        }

                        caption.setText(day.dayDateString);
                        text.setText(day.dayDescription);
                        shortDay.setText(day.shortDay);
                        convertView.requestLayout();
                    }
                };
            }
        }, R.layout.item_schedule){
            @Override
            public int getCount() {
                return isScheduleAvailable() ? 0: 200;
            }

            @Override
            public Day getItem(int position) {
                return buildDayFor(DateUtils.mathDays(getStartDate(), position));
            }

            @Override
            public boolean isEmpty() {
                return !isScheduleAvailable();
            }

            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public boolean isEnabled(int position) {
                return getItem(position).id != null;
            }
        };

        view_list(R.id.list_items).setAdapter(mDayListViewAdapter);
        view_list(R.id.list_items).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Day day = mDayListViewAdapter.getItem(position);
                if (day.id != null){
                    onRoutineDaySelect(day.id);
                }
            }
        });
    }

    private void onRoutineDaySelect(String id) {
        owner().openDayEditor(mSchedule.mRoutine.id, id);
    }


    private DateFormat dateFormat= DateFormat.getDateInstance();
    private DateFormat dayOfWeekFormat = new SimpleDateFormat("EE");

    private Day buildDayFor(Date date) {
        String dateString = dateFormat.format(date);
        if (DateUtils.isToday(date)){
            dateString = "Today";
        }
        String shortDay = dayOfWeekFormat.format(date);

        RoutineDay routineDay = mSchedule.getTrainingDay(date);
        String description = routineDay != null ? routineDay.description:"";
        return new Day(dateString, description, shortDay, routineDay==null?null:routineDay.id);
    }

    private boolean isScheduleAvailable() {
        return mSchedule == null || mSchedule.isNull() || !mSchedule.isDefined();
    }

    @Override
    protected String getHeaderName() {
        return "Workout Schedule";
    }

    @Override
    protected int getTileLayoutId() {
        return R.layout.tile_content_active_schedule;
    }

    @Override
    public View build_HeaderActionsView(ViewGroup actionPanel, LayoutInflater layoutInflater) {
        View view = layoutInflater.inflate(R.layout.actions_routine_schedule,actionPanel, false);
        view.findViewById(R.id.action_edit_routine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                owner().openRoutineEditor(mSchedule.mRoutine.id);
            }
        });

        return view;
    }

    @Override
    public void onMainButton() {}


    @Override
    public void onStart() {
        super.onStart();
        observer_activeRoutineObserver = observe_data_change(State.STOP, new Data.DataChangeObserver<RoutineSchedule>() {

            @Override
            public void onDataInvalid() {
                fetch_ActiveRoutine();
            }

            @Override
            public void onData(RoutineSchedule routine) {

            }
        });
        application().data_activeRoutineSchedule().addDataChangeObserver(observer_activeRoutineObserver);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetch_ActiveRoutine();
    }

    @Override
    public void onStop() {
        super.onStop();
        application().data_activeRoutineSchedule().removeDataChangeObserver(observer_activeRoutineObserver);
    }

    private void fetch_ActiveRoutine() {
        application().data_activeRoutineSchedule().fetch(true, observe_data_fetch(State.STOP, new PocketFitApp.DataAction<RoutineSchedule>() {
            @Override
            public void data(RoutineSchedule schedule) {
                mSchedule = schedule;
                mDayListViewAdapter.notifyDataSetChanged();
            }
        }));

    }

    public Date getStartDate() {
        return DateUtils.today();
    }


    private class Day{

        private final String dayDateString;
        private final String shortDay;
        private final String dayDescription;
        private final String id;


        private Day(String dateString, String trainingDescription, String shortDay, String id) {
            this.dayDateString = dateString;
            this.shortDay = shortDay;
            this.dayDescription = trainingDescription;
            this.id = id;
        }
    }
}
