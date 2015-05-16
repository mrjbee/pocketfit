package team.monroe.org.pocketfit.presentations;

import java.io.Serializable;

public class Routine implements Serializable {

    public final String id;
    public String title;
    public String description;

    public Routine(String id) {
        this.id = id;
    }
}
