package team.monroe.org.pocketfit.uc;

import org.monroe.team.android.box.db.TransactionUserCase;
import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.List;

import team.monroe.org.pocketfit.db.Dao;
import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.Meal;
import team.monroe.org.pocketfit.presentations.MealProduct;
import team.monroe.org.pocketfit.presentations.Product;

public class DeleteProduct extends UserCaseSupport<String, Boolean>{


    public DeleteProduct(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Boolean executeImpl(String productId) {
        List<Meal> mealList = using(Model.class).execute(GetMealList.class, null);
        for (Meal meal : mealList) {
            for (MealProduct mealProduct : meal.mealProducts) {
                if (mealProduct.product.id.equals(productId)){
                    return false;
                }
            }
        }
        using(PersistManager.class).removeProduct(productId);
        return true;
    }
}
