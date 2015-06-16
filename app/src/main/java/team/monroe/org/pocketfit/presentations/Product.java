package team.monroe.org.pocketfit.presentations;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Product implements Serializable{

    public final String id;
    public String title;
    public Integer calories;
    public Float carbs;
    public Float fats;
    public Float protein;


    public Product(String id) {
        this.id = id;
    }

    public String carbsFatsProteinString() {
        return "C:"+carbs+" F:"+fats+" P:"+protein;
    }
}
