package team.monroe.org.pocketfit.presentations;

import org.monroe.team.corebox.utils.Closure;

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

    public int calories() {
        return Math.round(calculateNutrition(new Closure<MealProduct, Float>() {
            @Override
            public Float execute(MealProduct arg) {
                return Float.valueOf(arg.calories());
            }
        }));
    }

    public float fats() {
        return calculateNutrition(new Closure<MealProduct, Float>() {
            @Override
            public Float execute(MealProduct arg) {
                return arg.fats();
            }
        });
    }

    public float carbs() {
        return calculateNutrition(new Closure<MealProduct, Float>() {
            @Override
            public Float execute(MealProduct arg) {
                return arg.carbs();
            }
        });
    }

    public float protein() {
        return calculateNutrition(new Closure<MealProduct, Float>() {
            @Override
            public Float execute(MealProduct arg) {
                return arg.protein();
            }
        });
    }

    private float calculateNutrition(Closure<MealProduct,Float> nutritionValueProvider){
        float answer = 0;
        for (MealProduct mealProduct : mealProducts) {
            answer+=nutritionValueProvider.execute(mealProduct);
        }
        return answer;
    }

    public String products() {
        if (mealProducts.isEmpty()){
            return "";
        }
        String string = "";
        for (MealProduct mealProduct : mealProducts) {
            string+= ", "+mealProduct.product.title;
        }

        return string.substring(1);
    }
}
