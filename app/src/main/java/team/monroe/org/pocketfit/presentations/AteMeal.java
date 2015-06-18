package team.monroe.org.pocketfit.presentations;

import java.util.Date;

public class AteMeal {
    public final long id;
    public final Meal meal;
    public final Date date;

    public AteMeal(long id, Meal meal, Date date) {
        this.id = id;
        this.meal = meal;
        this.date = date;
    }
}
