package team.monroe.org.pocketfit.uc;

import org.monroe.team.android.box.db.TransactionUserCase;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.utils.DateUtils;

import java.util.Date;

import team.monroe.org.pocketfit.db.Dao;
import team.monroe.org.pocketfit.presentations.AteMeal;
import team.monroe.org.pocketfit.presentations.Meal;

public class DeleteMeal extends TransactionUserCase<AteMeal, Void, Dao>{


    public DeleteMeal(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Void transactionalExecute(AteMeal request, Dao dao) {
        dao.deleteEatMeal(request.id);
        return null;
    }
}
