package team.monroe.org.pocketfit.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.Collections;

import team.monroe.org.pocketfit.manage.Persist;
import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.MealProduct;
import team.monroe.org.pocketfit.presentations.RoutineExercise;

public class UpdateMealProduct extends UserCaseSupport<UpdateMealProduct.Update, Void>{

    public UpdateMealProduct(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Void executeImpl(Update request) {

        Persist.Meal meal = using(PersistManager.class).getMeal(request.mealId);
        Persist.MealProduct updateMealPersists = new Persist.MealProduct(request.update.id, request.mealId, request.update.product.id);
        updateMealPersists.gram = request.update.gram;

        boolean not_filled = updateMealPersists.gram == null;

        if (not_filled){
            return null;
        }

        if (request.delete){
            meal.mealProductIdList.remove(updateMealPersists.id);
            using(PersistManager.class).updateOrCreateMeal(meal);
            using(PersistManager.class).removeMealProduct(updateMealPersists.id);
            return null;
        }


        using(PersistManager.class).updateOrCreateMealProduct(updateMealPersists);
        int indexExists = meal.mealProductIdList.indexOf(request.update.id);
        if (indexExists == -1){
            meal.mealProductIdList.add(updateMealPersists.id);
            using(PersistManager.class).updateOrCreateMeal(meal);
        }
        return null;
    }

    private boolean isDefined(Persist.RoutineDay routineDay) {
        return isDefined(routineDay.description);
    }

    private boolean isDefined(String string) {
        return string != null && !string.trim().isEmpty();
    }

    private <Type> Type diff(Type origin, Type update) {
        return update != null? update:origin;
    }


    public static class Update {

        public final MealProduct update;
        public final String mealId;
        public final boolean delete;


        public Update(MealProduct product, String mealId, boolean delete) {
            this.update = product;
            this.mealId = mealId;
            this.delete = delete;
        }
    }
}
