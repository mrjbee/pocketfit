package team.monroe.org.pocketfit.presentations;


import java.io.Serializable;

public class Exercise implements Serializable{

    public final String id;
    public String title;
    public String description;
    public Type type;

    public Exercise(String id) {
        this.id = id;
    }


    public static enum Type implements Serializable{
        weight_times("Weight & Times"), times("Times"), distance("Distance"), time("Time");

        public final String human;

        Type(String text) {
            this.human = text;
        }
    }
}
