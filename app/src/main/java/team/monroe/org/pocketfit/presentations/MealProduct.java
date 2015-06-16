package team.monroe.org.pocketfit.presentations;

public class MealProduct {

    public final String id;

    public Product product;
    public Float gram;


    public MealProduct(String id) {
        this.id = id;
    }

    public int calories() {
        return Math.round(product.calories/100f * gram);
    }

    public String getProductId() {
        return product!= null?product.id:null;
    }
}
