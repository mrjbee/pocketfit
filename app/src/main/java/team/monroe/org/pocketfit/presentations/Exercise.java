package team.monroe.org.pocketfit.presentations;


import org.monroe.team.corebox.utils.Lists;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Exercise implements Serializable{

    public final String id;
    public String title;
    public String description;
    public Type type;
    public final Set<String> muscleSet = new HashSet<>();

    public Exercise(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Exercise exercise = (Exercise) o;

        if (!id.equals(exercise.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static enum Type implements Serializable{
        weight_times("Weight & Times"), distance("Distance"), time("Time");
        public final String human;
        Type(String text) {
            this.human = text;
        }
    }

}
