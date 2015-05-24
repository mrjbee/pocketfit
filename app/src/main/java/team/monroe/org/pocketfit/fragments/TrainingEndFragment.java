package team.monroe.org.pocketfit.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.monroe.team.corebox.utils.DateUtils;

import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.TrainingActivity;

public class TrainingEndFragment extends BodyFragment<TrainingActivity>{


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

        addDetails("Exercises",application().getTrainingPlan().getExerciseCount()," ");

        //days,hours,minutes,seconds, periodMs
        long[] duration = DateUtils.splitperiod(application().getTrainingPlan().getDurationMs());
        String durationString = string(duration[1],2)+":"+string(duration[2],2)+":"+string(duration[3],2);
        addDetails("Duration", durationString, "");
        float weight = application().getTrainingPlan().getLiftedUpWeight();
        addDetails("Lifted Up",weight, "kg");

        view(R.id.main_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               owner().stopTraining(true);
            }
        });
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
        View view = activity().getLayoutInflater().inflate(R.layout.panel_result_detail,viewPanel,false);
        ((TextView)view.findViewById(R.id.item_caption)).setText(caption);
        ((TextView)view.findViewById(R.id.item_value)).setText(value.toString());
        ((TextView)view.findViewById(R.id.item_measure)).setText(measure);
        viewPanel.addView(view);
    }
}
