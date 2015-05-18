package team.monroe.org.pocketfit.presentations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Routine{

    public final String id;
    public String title = null;
    public String description = null;
    public String imageId = null;
    public Boolean active = null;
    public List<RoutineDay> trainingDays = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Routine routine = (Routine) o;

        if (!id.equals(routine.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Routine(String id) {
        this.id = id;
    }
}
