package team.monroe.org.pocketfit.uc;

import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.Meal;
import team.monroe.org.pocketfit.presentations.Routine;

public class GetMealList extends UserCaseSupport<Void, List<Meal>>{

    public GetMealList(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected  List<Meal> executeImpl(Void request) {
        Set<String> idSet = using(PersistManager.class).listMealIds();
        List<Meal> answer = new ArrayList<>(idSet.size());
        for (String mealId : idSet) {
            Meal meal = using(Model.class).execute(GetMealById.class, mealId);
            answer.add(meal);
        }
        Collections.sort(answer, new Comparator<Meal>() {
            @Override
            public int compare(Meal lhs, Meal rhs) {
                return lhs.title.compareTo(rhs.title);
            }
        });
        return answer;
    }
}
