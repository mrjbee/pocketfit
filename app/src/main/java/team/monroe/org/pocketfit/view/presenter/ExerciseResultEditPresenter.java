package team.monroe.org.pocketfit.view.presenter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import team.monroe.org.pocketfit.R;
import team.monroe.org.pocketfit.presentations.RoutineExercise;

public class ExerciseResultEditPresenter extends ViewPresenter<ViewGroup>{

    private RoutineExercise.ExerciseDetails mExerciseDetails;
    private ExerciseDetailsViewPresenter mDetailsViewPresenter;
    private TimePickPresenter mTimePickPresenter;

    public ExerciseResultEditPresenter(ViewGroup rootView) {
        super(rootView);
        mTimePickPresenter = new TimePickPresenter(rootView.findViewById(R.id.panel_edit_time));
    }

    public void setup(RoutineExercise.ExerciseDetails exerciseDetails){
        mExerciseDetails = exerciseDetails;
        if (exerciseDetails instanceof RoutineExercise.PowerExerciseDetails){
            mDetailsViewPresenter = new ExerciseDetailsViewPresenter<RoutineExercise.PowerExerciseDetails>(getEditPanelById(R.id.panel_edit_power)) {
                @Override
                public void fillDetails(RoutineExercise.PowerExerciseDetails details) {
                    details.times  = readPositiveInteger(R.id.edit_power_times);
                    details.weight  = readPositiveFloat(R.id.edit_power_weight);
                }

                @Override
                protected void fillUI(RoutineExercise.PowerExerciseDetails details) {
                    updateTextView(R.id.edit_power_weight, details.weight);
                    updateTextView(R.id.edit_power_times, details.times);
                }

            };
        }else if (exerciseDetails instanceof RoutineExercise.DistanceExerciseDetails){
            mDetailsViewPresenter = new ExerciseDetailsViewPresenter<RoutineExercise.DistanceExerciseDetails>(getEditPanelById(R.id.panel_edit_distance)) {
                @Override
                public void fillDetails(RoutineExercise.DistanceExerciseDetails details) {
                    details.distance  = readPositiveFloat(R.id.edit_distance);
                }

                @Override
                protected void fillUI(RoutineExercise.DistanceExerciseDetails details) {
                    updateTextView(R.id.edit_distance, details.distance);
                }
            };
        }else if (exerciseDetails instanceof RoutineExercise.TimeExerciseDetails){
            mDetailsViewPresenter = new ExerciseDetailsViewPresenter<RoutineExercise.TimeExerciseDetails>(getEditPanelById(R.id.panel_edit_time)) {
                @Override
                public void fillDetails(RoutineExercise.TimeExerciseDetails details) {
                    details.time  = mTimePickPresenter.getMinutes();
                }

                @Override
                protected void fillUI(RoutineExercise.TimeExerciseDetails details) {
                    Float existingTime =  details.time;
                    mTimePickPresenter.setMinutes(existingTime);
                }
            };
        }else{
            throw new IllegalStateException();
        }
        mDetailsViewPresenter.fillUI(mExerciseDetails);
        mDetailsViewPresenter.show();
    }

    private View getEditPanelById(int r) {
        return getRootView().findViewById(r);
    }

    public RoutineExercise.ExerciseDetails result(){
        mDetailsViewPresenter.fillDetails(mExerciseDetails);
        return mExerciseDetails;
    }


    public static abstract class ExerciseDetailsViewPresenter<DetailsType extends RoutineExercise.ExerciseDetails> extends ViewPresenter<View>{

        public ExerciseDetailsViewPresenter(View rootView) {
            super(rootView);
        }

        protected Integer readPositiveInteger(int r_text) {
            Integer value;
            String text = ((TextView)getRootView().findViewById(r_text)).getText().toString();
            try {
                value = Math.abs(Integer.parseInt(text));
            }catch (Exception e){
                value = null;
            }
            return value;
        }

        protected Float readPositiveFloat(int r_text) {
            Float value;
            String text = ((TextView)getRootView().findViewById(r_text)).getText().toString();
            try {
                value = Math.abs(Float.parseFloat(text));
            }catch (Exception e){
                value = null;
            }
            return value;
        }

        protected void updateTextView(int r, Object value) {
            ((TextView)getRootView().findViewById(r)).setText(value == null?"":value.toString());
        }


        public void show(DetailsType details) {
            fillUI(details);
            show();
        }

        protected abstract void fillUI(DetailsType details);
        public abstract void fillDetails(DetailsType details);

    }
}
