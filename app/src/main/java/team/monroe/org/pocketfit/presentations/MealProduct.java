package team.monroe.org.pocketfit.presentations;

import org.monroe.team.corebox.utils.Closure;

public class MealProduct {

    public final String id;

    public Product product;
    public Float gram;

    public MealProduct(String id) {
        this.id = id;
    }

    public int calories() {
        return Math.round(calculateNutrition(new Closure<Void, Float>() {
            @Override
            public Float execute(Void arg) {
                return Float.valueOf(product.calories);
            }
        }));
    }

    private float calculateNutrition(Closure<Void,Float> nutritionValueProvider){
        return nutritionValueProvider.execute(null)/100f * gram;
    }

    public String getProductId() {
        return product!= null?product.id:null;
    }

    public float fats() {
        return calculateNutrition(new Closure<Void, Float>() {
            @Override
            public Float execute(Void arg) {
                return product.fats;
            }
        });
    }

    public float protein() {
        return calculateNutrition(new Closure<Void, Float>() {
            @Override
            public Float execute(Void arg) {
                return product.protein;
            }
        });
    }

    public float carbs() {
        return calculateNutrition(new Closure<Void, Float>() {
            @Override
            public Float execute(Void arg) {
                return product.carbs;
            }
        });
    }
}
