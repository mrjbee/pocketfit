package team.monroe.org.pocketfit.presentations;


import android.content.res.Resources;

import java.io.Serializable;

public class RoutineExercise {

    public final String id;
    public Exercise exercise;
    public ExerciseDetails exerciseDetails;

    public RoutineExercise(String id) {
        this.id = id;
    }

    public String getExerciseId() {
        return exercise == null? null:exercise.id;
    }


    public static interface ExerciseDetails extends Serializable {
        public String detailsString();
        public boolean isDefined();
    }

    public static class DistanceExerciseDetails implements ExerciseDetails{

        public Float distance;

        @Override
        public String detailsString() {
            return !isDefined() ? "":distance+" meters";
        }

        @Override
        public boolean isDefined() {
            return distance != null;
        }
    }

    public static class TimeExerciseDetails implements ExerciseDetails{

        public Float time;

        @Override
        public String detailsString() {
            if (!isDefined())return "";
            int minutes = (int)((float)time);
            int seconds = Math.round((time - minutes) * 60f);
            if (seconds < 10){
                return minutes+" min 0"+seconds+" sec";
            }else{
                return minutes+" min "+seconds+" sec";
            }
        }

        @Override
        public boolean isDefined() {
            return time != null;
        }
    }

    public static class PowerExerciseDetails implements ExerciseDetails{

        public Float weight;
        public Integer times;
        public Integer sets;

        @Override
        public String detailsString() {
            return !isDefined() ? "":sets+" sets ("+weight+"x"+times+" kg/times)";
        }

        @Override
        public boolean isDefined() {
            return times != null && weight!=null && sets!=null;
        }
    }


    public static String shortDescription(ExerciseDetails details, Resources resources) {
        return detailsValue(details, resources)+" "+detailsMeasure(details, resources);
    }

    public static String detailsCharacteristic(ExerciseDetails details, Resources resources){
            if (details instanceof  DistanceExerciseDetails){
                return "Distance";
            }else if (details instanceof TimeExerciseDetails){
                return "Time";
            }else if (details instanceof  PowerExerciseDetails){
                if (((PowerExerciseDetails) details).sets > 0) {
                    return "Sets";
                }else {
                    return "Set";
                }
            }else {
              throw new IllegalStateException();
            }
    }

    public static String detailsMeasure(ExerciseDetails details, Resources resources){
        if (details instanceof  DistanceExerciseDetails){
            return "meters";
        }else if (details instanceof TimeExerciseDetails){
            return "min";
        }else if (details instanceof  PowerExerciseDetails){
            if (((PowerExerciseDetails) details).sets > 0) {
                return "reps/kg";
            }else {
                return "reps/kg";
            }
        }else {
            throw new IllegalStateException();
        }
    }

    public static String detailsValue(ExerciseDetails details, Resources resources){
        if (details instanceof  DistanceExerciseDetails){
            return ""+((DistanceExerciseDetails) details).distance;
        }else if (details instanceof TimeExerciseDetails){
            int minutes = (int)((float)((TimeExerciseDetails) details).time);
            int seconds = Math.round((((TimeExerciseDetails) details).time - minutes) * 60f);
            if (seconds < 10){
                return minutes+".0"+seconds;
            }else{
                return minutes+"."+seconds;
            }
        }else if (details instanceof  PowerExerciseDetails){
            if (((PowerExerciseDetails) details).sets > 0){
                return ((PowerExerciseDetails) details).sets + " x ("+((PowerExerciseDetails) details).times + "x" + ((PowerExerciseDetails) details).weight+")";
            }else {
                return ((PowerExerciseDetails) details).times + "x" + ((PowerExerciseDetails) details).weight;
            }
        }else {
            throw new IllegalStateException();
        }
    }

}

