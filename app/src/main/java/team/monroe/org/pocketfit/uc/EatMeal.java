package team.monroe.org.pocketfit.uc;

import org.monroe.team.android.box.db.DAOSupport;
import org.monroe.team.android.box.db.TransactionUserCase;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.utils.DateUtils;

import java.util.Date;

import team.monroe.org.pocketfit.db.Dao;
import team.monroe.org.pocketfit.presentations.AteMeal;
import team.monroe.org.pocketfit.presentations.Meal;

public class EatMeal extends TransactionUserCase<Meal, AteMeal, Dao>{


    public EatMeal(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected AteMeal transactionalExecute(Meal request, Dao dao) {
        Date eatDate = DateUtils.now();
        long id = dao.insertEatMeal(request.id, eatDate);
        return new AteMeal(id, request, eatDate);
    }
}
