package team.monroe.org.pocketfit.db;

import android.database.sqlite.SQLiteDatabase;

import org.monroe.team.android.box.db.DAOSupport;
import org.monroe.team.android.box.db.Schema;
import org.monroe.team.corebox.utils.DateUtils;

import java.util.Date;

public class Dao extends DAOSupport{

    public Dao(SQLiteDatabase db, Schema schema) {
        super(db, schema);
    }

    public long insertEatMeal(String mealId, Date now) {

        long id = db.insertOrThrow(
                mealTable().TABLE_NAME,
                null,
                content()
                        .value(mealTable()._MEAL_ID, mealId)
                        .value(mealTable()._DATE, DateUtils.now().getTime())
                        .get());
        return id;
    }

    private PocketFitSchema.MealEntry mealTable() {
        return table(PocketFitSchema.MealEntry.class);
    }
}
