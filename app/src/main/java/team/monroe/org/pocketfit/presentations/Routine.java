package team.monroe.org.pocketfit.presentations;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class Routine implements Serializable {

    public final String id;
    public String title;
    public String description;
    public String imageId;
    transient public Boolean active = null;
    public List<RoutineDay> trainingDays = Collections.emptyList();

    public Routine(String id) {
        this.id = id;
    }
}
