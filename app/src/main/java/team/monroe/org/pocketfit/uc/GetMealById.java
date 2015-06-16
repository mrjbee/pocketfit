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
        /*for (String dayId : mealPersist.routineDaysIdList) {
            RoutineDay routineDay = using(Model.class).execute(GetRoutineDayById.class,dayId);
            if (routineDay == null) throw new NullPointerException("Day couldn`t be null");
            answer.trainingDays.add(routineDay);
        }*/
        return answer;
    }
}
