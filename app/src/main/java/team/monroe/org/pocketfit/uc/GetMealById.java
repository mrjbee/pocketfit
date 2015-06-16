package team.monroe.org.pocketfit.uc;

import org.monroe.team.android.box.services.SettingManager;
import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.ArrayList;

import team.monroe.org.pocketfit.Settings;
import team.monroe.org.pocketfit.manage.Persist;
import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.Meal;
import team.monroe.org.pocketfit.presentations.MealProduct;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;

public class GetMealById extends UserCaseSupport<String, Meal>{

    public GetMealById(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Meal executeImpl(String id) {
        Persist.Meal mealPersist = using(PersistManager.class).getMeal(id);
        if (mealPersist == null) return null;

        Meal answer = new Meal(mealPersist.id);
        answer.title = mealPersist.title;
        answer.imageId = mealPersist.imageId;

        answer.mealProducts = new ArrayList<>();
        for (String mealProductId : mealPersist.mealProductIdList) {
            MealProduct product = using(Model.class).execute(GetMealProductById.class, mealProductId);
            if (product == null) throw new NullPointerException("Meal Product couldn`t be null");
            answer.mealProducts.add(product);
        }
        return answer;
    }
}
