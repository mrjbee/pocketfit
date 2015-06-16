package team.monroe.org.pocketfit.presentations;

import java.util.ArrayList;
import java.util.List;

public class Meal {

    public final String id;

    public String title = null;
    public List<MealProduct> mealProducts = new ArrayList<>();
    public String imageId = null;

    public Meal(String id) {
        this.id = id;
    }
}
