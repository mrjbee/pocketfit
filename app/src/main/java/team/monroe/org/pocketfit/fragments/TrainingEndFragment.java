package team.monroe.org.pocketfit.fragments;


import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceController;
import org.monroe.team.android.box.data.Data;
import org.monroe.team.corebox.utils.Closure;
import org.monroe.team.corebox.utils.DateUtils;

import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.TrainingActivity;
import team.monroe.org.pocketfit.TrainingExecutionService;
import team.monroe.org.pocketfit.presentations.RoutineExercise;

import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.animateAppearance;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.duration_constant;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.interpreter_accelerate;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.interpreter_overshot;
import static org.monroe.team.android.box.app.ui.animation.apperrance.AppearanceControllerBuilder.scale;

public class TrainingEndFragment extends BodyFragment<TrainingActivity>{


    private Data.DataChangeObserver<TrainingExecutionService.TrainingPlan.Agenda> mAgendaChangeObserver;
    private AppearanceController ac_stopButton;

    @Override
    final protected String getHeaderName() {
        return "Training Ends";
    }

    @Override
    final protected boolean isHeaderSecondary() {
        return true;
    }

    @Override
    final protected int getLayoutId() {
        return R.layout.fragment_training_end;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        view_text(R.id.text_day).setText(application().getTrainingPlan().getRoutineDay().description);

        updateWorkoutDetails();

        ac_stopButton = animateAppearance(view(R.id.panel_main_button), scale(1, 0))
                .showAnimation(duration_constant(400), interpreter_overshot())
                .hideAnimation(duration_constant(300), interpreter_accelerate(0.6f))
                .hideAndGone()
                .build();


        view(R.id.main_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               owner().stopTraining(true);
            }
        });

        ac_stopButton.showWithoutAnimation();

        view(R.id.scroll, ScrollView.class).getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                int scrollY =  view(R.id.scroll, ScrollView.class).getScrollY();
                if (owner() !=null) {
                    owner().onBodyScroll(scrollY);
                }

                if (scrollY < 10){
                    ac_stopButton.show();
                }else{
                    ac_stopButton.hide();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAgendaChangeObserver = new Data.DataChangeObserver<TrainingExecutionService.TrainingPlan.Agenda>() {
            @Override
            public void onDataInvalid() {
                updateWorkoutDetails();
            }

            @Override public void onData(TrainingExecutionService.TrainingPlan.Agenda agenda) {}
        };
        application().getTrainingPlan().getAgenda().addDataChangeObserver(mAgendaChangeObserver);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (application().getTrainingPlan() != null) {
            application().getTrainingPlan().getAgenda().removeDataChangeObserver(mAgendaChangeObserver);
        }
    }

    private void updateWorkoutDetails() {
        view(R.id.panel_results, ViewGroup.class).removeAllViews();
        addDetails("Exercises", application().getTrainingPlan().getExerciseCount(), " ");
        //days,hours,minutes,seconds, periodMs
        long[] duration = DateUtils.splitperiod(application().getTrainingPlan().getDurationMs());
        String durationString = string(duration[1],2)+":"+string(duration[2],2)+":"+string(duration[3],2);
        addDetails("Duration", durationString, "");
        float weight = application().getTrainingPlan().getLiftedUpWeight();
        addDetails("Lifted Up",weight, "kg");
        float distance = application().getTrainingPlan().getDistance();
        addDetails("Total distance", distance, "meters");
        updateWorkoutAgenda();
    }

    private void updateWorkoutAgenda() {

        application().getTrainingPlan().getAgenda().fetch(true, observe_data_fetch(State.STOP, new PocketFitApp.DataAction<TrainingExecutionService.TrainingPlan.Agenda>() {
            @Override
            public void data(TrainingExecutionService.TrainingPlan.Agenda data) {
                ViewGroup agendaPanel = view(R.id.panel_agenda, ViewGroup.class);
                agendaPanel.removeAllViews();
                for (TrainingExecutionService.TrainingPlan.AgendaExercise agendaExercise : data.exerciseList) {
                    int index = data.exerciseList.indexOf(agendaExercise);
                    View convertedView = activity().getLayoutInflater().inflate(R.layout.item_agenda_exercise, agendaPanel, false);
                    fillAgendaUI(convertedView, agendaExercise, index, data.exerciseList.size());
                    agendaPanel.addView(convertedView);
                }

            }
        }));
    }

    private void fillAgendaUI(View convertView, TrainingExecutionService.TrainingPlan.AgendaExercise exercise, int position, int size) {

        TextView caption = (TextView) convertView.findViewById(R.id.item_caption);
        TextView index = (TextView) convertView.findViewById(R.id.item_index);
        ImageView circleImage = (ImageView) convertView.findViewById(R.id.image_circle);

        ImageView line = (ImageView) convertView.findViewById(R.id.image_line);
        ImageView lineOver = (ImageView) convertView.findViewById(R.id.image_line_over);

        ViewGroup setsPanel = (ViewGroup) convertView.findViewById(R.id.panel_exercise_sets);
            setsPanel.removeAllViews();
            caption.setText(exercise.exercise.title);
            index.setText((position+1)+"");
            circleImage.setImageResource(!exercise.isExecuted()? R.drawable.circle_gray:R.drawable.circle_pink);
            if (position == 0){
                line.setImageResource(R.drawable.gray_line_bottom);
                lineOver.setVisibility(View.VISIBLE);
            }else if (position == size - 1){
                line.setImageResource(R.drawable.gray_line_top);
                lineOver.setVisibility(View.INVISIBLE);
            }else {
                line.setImageResource(R.drawable.gray_line_both);
                lineOver.setVisibility(View.VISIBLE);
            }

            for (final TrainingExecutionService.TrainingPlan.ExerciseResult result : exercise.results) {
                final RoutineExercise.ExerciseDetails details = result.asExerciseDetails();
                View view = activity().getLayoutInflater().inflate(R.layout.panel_3_column_details_edit, setsPanel, false);
                ((TextView)view.findViewById(R.id.item_caption)).setVisibility(View.GONE);
                ((TextView)view.findViewById(R.id.item_value)).setText(RoutineExercise.detailsValue(details, getResources()));
                ((TextView)view.findViewById(R.id.item_measure)).setText(RoutineExercise.detailsMeasure(details, getResources()));
                view.findViewById(R.id.action_edit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        owner().editDetails(details, new Closure<RoutineExercise.ExerciseDetails, Void>() {

                            @Override
                            public Void execute(RoutineExercise.ExerciseDetails arg) {
                                if (arg != null) {
                                    result.updateDetails(arg);
                                }
                                return null;
                            }
                        });
                    }
                });
                setsPanel.addView(view);
            }
    }


    private String string(long value, int digits) {
        String answer = Long.toString(value);
        for (int i = answer.length(); i < digits; i++){
            answer = "0"+answer;
        }
        return answer;
    }

    private void addDetails(String caption, Object value, String measure) {
        ViewGroup viewPanel = view(R.id.panel_results, ViewGroup.class);
        View view = activity().getLayoutInflater().inflate(R.layout.panel_3_column_details,viewPanel,false);
        ((TextView)view.findViewById(R.id.item_caption)).setText(caption);
        ((TextView)view.findViewById(R.id.item_value)).setText(value.toString());
        ((TextView)view.findViewById(R.id.item_measure)).setText(measure);
        viewPanel.addView(view);
    }
}
