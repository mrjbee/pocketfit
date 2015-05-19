package team.monroe.org.pocketfit.presentations;


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
            return !isDefined() ? "":distance+" m";
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
            return !isDefined() ? "":time+" min";
        }

        @Override
        public boolean isDefined() {
            return time != null;
        }
    }

    public static class TimesExerciseDetails implements ExerciseDetails{

        public Integer times;

        @Override
        public String detailsString() {
            return !isDefined() ? "":times+" times";
        }

        @Override
        public boolean isDefined() {
            return times != null;
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
}

