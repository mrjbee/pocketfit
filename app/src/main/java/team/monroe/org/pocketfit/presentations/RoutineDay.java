package team.monroe.org.pocketfit.presentations;

import java.util.List;

public class RoutineDay{

    public final String id;
    public Integer restDays  = null;
    public String description = null;
    public List<DayExercise> exerciseList = null;

    public RoutineDay(String id) {
        this.id = id;
    }


    public boolean hasDescription() {
        return description != null && !description.trim().isEmpty();
    }
}
