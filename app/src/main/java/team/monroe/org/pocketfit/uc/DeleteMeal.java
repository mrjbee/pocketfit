package team.monroe.org.pocketfit.uc;

import org.monroe.team.android.box.db.TransactionUserCase;
import org.monroe.team.corebox.services.ServiceRegistry;

import team.monroe.org.pocketfit.db.Dao;
import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.AteMeal;
import team.monroe.org.pocketfit.presentations.Meal;

public class DeleteMeal extends TransactionUserCase<String, Boolean, Dao>{


    public DeleteMeal(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Boolean transactionalExecute(String mealId, Dao dao) {
        dao.deleteEatMealByMealId(mealId);
        using(PersistManager.class).removeMeal(mealId);
        return true;
    }
}
