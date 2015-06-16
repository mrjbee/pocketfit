package team.monroe.org.pocketfit.uc;

import org.monroe.team.android.box.services.SettingManager;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import team.monroe.org.pocketfit.Settings;
import team.monroe.org.pocketfit.manage.Persist;
import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.Meal;
import team.monroe.org.pocketfit.presentations.MealProduct;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;

public class UpdateMeal extends UserCaseSupport<Meal, Void>{

    public UpdateMeal(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Void executeImpl(Meal update) {

        Persist.Meal updatePersist = new Persist.Meal(update.id);
        updatePersist.imageId = update.imageId;
        updatePersist.title = update.title;
        if (update.mealProducts != null){
            for (MealProduct product: update.mealProducts) {
                updatePersist.mealProductIdList.add(product.id);
            }
        }
        Persist.Meal originPersist = using(PersistManager.class).getMeal(updatePersist.id);

        if (originPersist == null){
            using(PersistManager.class).updateOrCreateMeal(updatePersist);
            checkForRemove(updatePersist);
            return null;
        }

        originPersist.title = diff(originPersist.title, updatePersist.title);
        originPersist.imageId = diff(originPersist.imageId, updatePersist.imageId);
        originPersist.mealProductIdList = diff(originPersist.mealProductIdList, updatePersist.mealProductIdList);

        using(PersistManager.class).updateOrCreateMeal(originPersist);
        checkForRemove(originPersist);
        return null;
    }

    private void checkForRemove(Persist.Meal origin) {
        if (!isDefined(origin)){
            using(PersistManager.class).removeMeal(origin.id);
        }
    }

    private boolean isDefined(Persist.Meal routine) {
        return isDefined(routine.title) ||
               !routine.mealProductIdList.isEmpty();
    }

    private boolean isDefined(String string) {
        return string != null && !string.trim().isEmpty();
    }

    private <Type> Type diff(Type origin, Type update) {
        return update != null? update:origin;
    }

}
