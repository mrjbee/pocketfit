package team.monroe.org.pocketfit.uc;

import org.monroe.team.android.box.db.DAOSupport;
import org.monroe.team.android.box.db.TransactionUserCase;
import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import team.monroe.org.pocketfit.db.Dao;
import team.monroe.org.pocketfit.presentations.AteMeal;

public class GetAteMealByDate extends TransactionUserCase<Date, List<AteMeal>, Dao> {

    public GetAteMealByDate(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected List<AteMeal> transactionalExecute(Date request, Dao dao) {
        Date fromDate = DateUtils.dateOnly(request);
        Date toDate = DateUtils.mathDays(fromDate, 1);
        List<DAOSupport.Result> resultList = dao.getEatMealForDates(fromDate, toDate);
        List<AteMeal> ateMeals = new ArrayList<>();
        for (DAOSupport.Result result : resultList) {
            AteMeal ateMeal = new AteMeal(
                    result.get(0, Long.class),
                    using(Model.class).execute(GetMealById.class, result.get(1, String.class)),
                    result.get(2, Date.class));
            ateMeals.add(ateMeal);
        }
        return ateMeals;
    }
}
