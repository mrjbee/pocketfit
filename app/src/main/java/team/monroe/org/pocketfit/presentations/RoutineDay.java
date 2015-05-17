package team.monroe.org.pocketfit.presentations;


import java.io.Serializable;

public class RoutineDay implements Serializable {

    public final String id;
    public final String routineId;
    public Integer restDays;
    public String description;

    public RoutineDay(String id, String routineId) {
        this.id = id;
        this.routineId = routineId;
    }


}
