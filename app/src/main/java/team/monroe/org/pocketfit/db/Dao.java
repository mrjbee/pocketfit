package team.monroe.org.pocketfit.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.monroe.team.android.box.db.DAOSupport;
import org.monroe.team.android.box.db.Schema;
import org.monroe.team.corebox.utils.Closure;
import org.monroe.team.corebox.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public List<Result> getEatMealForDates(Date startDate, Date endDate) {
        String whereStatement = null;
        String[] whereArgs = null;

        if (startDate != null || endDate != null){
            if (startDate != null && endDate != null){
                whereStatement = "? <= " + mealTable()._DATE.name() + " AND ? > " + mealTable()._DATE.name();
                whereArgs = strs(Long.toString(startDate.getTime()), Long.toString(endDate.getTime()));
            } else if (endDate != null){
                whereStatement = "? > " + mealTable()._DATE.name();
                whereArgs = strs(Long.toString(endDate.getTime()));
            } else {
                whereStatement = "? <= " + mealTable()._DATE.name();
                whereArgs = strs(Long.toString(startDate.getTime()));
            }
        }

        Cursor cursor = db.query(mealTable().TABLE_NAME,
                strs(mealTable()._ID.name(),mealTable()._MEAL_ID.name(),mealTable()._DATE.name()),
                whereStatement,
                whereArgs,
                null,
                null,
                null);
        return collect(cursor, new Closure<Cursor, Result>() {
            @Override
            public Result execute(Cursor arg) {
                return result().with(arg.getLong(0),arg.getString(1),arg.getLong(2));
            }
        });
    }

    private List<Result> collect(Cursor cursor, Closure<Cursor,Result> closure) {
        List<Result> answer = new ArrayList<Result>(cursor.getCount());
        Result itResult;
        while (cursor.moveToNext()) {
            itResult = closure.execute(cursor);
            if (itResult != null) answer.add(itResult);
        }
        cursor.close();
        return answer;
    }



    private PocketFitSchema.MealEntry mealTable() {
        return table(PocketFitSchema.MealEntry.class);
    }

    public void deleteEatMeal(long id) {
        int i = db.delete(mealTable().TABLE_NAME,
                "? == "+mealTable()._ID.name(),
                strs(id));
        if (i != 1){
            throw new IllegalStateException("Supposed to remove single instance but was "+i);
        }
    }

    public void deleteEatMealByMealId(String mealId) {
        int i = db.delete(mealTable().TABLE_NAME,
                "? == "+mealTable()._MEAL_ID.name(),
                strs(mealId));
    }
}
