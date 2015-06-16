package team.monroe.org.pocketfit.uc;

import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import team.monroe.org.pocketfit.manage.Persist;
import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.MealProduct;
import team.monroe.org.pocketfit.presentations.RoutineExercise;

public class GetMealProductById extends UserCaseSupport<String, MealProduct>{

    public GetMealProductById(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected MealProduct executeImpl(String id) {
        Persist.MealProduct productPersist = using(PersistManager.class).getMealProduct(id);
        if (productPersist == null) return null;
        MealProduct answer = new MealProduct(id);
        answer.product = using(Model.class).execute(GetProductById.class, productPersist.productId);
        answer.gram = productPersist.gram;
        return answer;
    }
}
