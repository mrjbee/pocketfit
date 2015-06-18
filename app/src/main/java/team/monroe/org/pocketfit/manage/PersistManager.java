package team.monroe.org.pocketfit.manage;

import android.content.Context;

import org.monroe.team.android.box.utils.SerializationMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import team.monroe.org.pocketfit.presentations.Exercise;
import team.monroe.org.pocketfit.presentations.Product;
import team.monroe.org.pocketfit.presentations.Routine;
import team.monroe.org.pocketfit.presentations.RoutineDay;

public class PersistManager {

    private final SerializationMap<String, Persist.Routine> routineSerializationMap;
    private final SerializationMap<String, Persist.RoutineDay> routineDaySerializationMap;
    private final SerializationMap<String, Persist.RoutineExercise> routineExerciseSerializationMap;
    private final SerializationMap<String, Exercise> exerciseSerializationMap;
    private final SerializationMap<String, Persist.Meal> mealSerializationMap;
    private final SerializationMap<String, Product> productSerializationMap;
    private final SerializationMap<String, Persist.MealProduct> mealProductSerializationMap;


    public PersistManager(Context context) {
        this.routineSerializationMap = new SerializationMap<>("persist_routine.map", context);
        this.routineDaySerializationMap = new SerializationMap<>("persist_routinedays.map", context);
        this.routineExerciseSerializationMap = new SerializationMap<>("persist_routineexercise.map", context);
        this.exerciseSerializationMap = new SerializationMap<>("persist_exercises.map", context);
        this.mealSerializationMap = new SerializationMap<>("persist_meal.map", context);
        this.productSerializationMap = new SerializationMap<>("persist_product.map", context);
        mealProductSerializationMap= new SerializationMap<>("persist_mp.map", context);
    }

    public void updateOrCreate(Persist.Routine routine) {
        routineSerializationMap.put(routine.id, routine);
    }

    public Persist.Routine get(String routineId) {
        return routineSerializationMap.get(routineId);
    }

    public Set<String> listRoutineIds() {
        return new HashSet<>(routineSerializationMap.keys());
    }

    public void remove(String routineId) {
        routineSerializationMap.remove(routineId);
    }

    public Persist.RoutineDay getDay(String dayId) {
        return routineDaySerializationMap.get(dayId);
    }

    public void updateOrCreateDay(Persist.RoutineDay update) {
        routineDaySerializationMap.put(update.id, update);
    }

    public void removeDay(String dayId) {
        routineDaySerializationMap.remove(dayId);
    }

    public Exercise getExercise(String exerciseId) {
        return exerciseSerializationMap.get(exerciseId);
    }

    public void updateOrCreateExercise(Exercise update) {
        exerciseSerializationMap.put(update.id,update);
    }

    public Set<String> listExerciseIds() {
        return exerciseSerializationMap.keys();
    }

    public Persist.RoutineExercise getRoutineExercise(String id) {
        return routineExerciseSerializationMap.get(id);
    }

    public void removeRoutineExercise(String id) {
        routineExerciseSerializationMap.remove(id);
    }

    public void updateOrCreateRoutineExercise(Persist.RoutineExercise updateExercisePersists) {
        routineExerciseSerializationMap.put(updateExercisePersists.id, updateExercisePersists);
    }

    public Persist.Meal getMeal(String mealId) {
        return mealSerializationMap.get(mealId);
    }

    public void updateOrCreateMeal(Persist.Meal updatePersist) {
        mealSerializationMap.put(updatePersist.id, updatePersist);
    }

    public Product getProduct(String id) {
        return productSerializationMap.get(id);
    }

    public void updateOrCreateProduct(Product product) {
        productSerializationMap.put(product.id, product);
    }

    public Set<String> listProductIds()  {
        return productSerializationMap.keys();
    }

    public Persist.MealProduct getMealProduct(String id) {
        return mealProductSerializationMap.get(id);
    }

    public void removeMealProduct(String id) {
        mealProductSerializationMap.remove(id);
    }

    public void updateOrCreateMealProduct(Persist.MealProduct mealPersists) {
        mealProductSerializationMap.put(mealPersists.id, mealPersists);
    }

    public Set<String> listMealIds() {
        return mealSerializationMap.keys();
    }

    public void removeMeal(String id) {
        mealSerializationMap.remove(id);
    }

    public void removeProduct(String productId) {
        productSerializationMap.remove(productId);
    }
}
