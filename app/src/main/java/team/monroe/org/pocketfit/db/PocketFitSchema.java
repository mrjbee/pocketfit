package team.monroe.org.pocketfit.db;

import org.monroe.team.android.box.db.Schema;

import team.monroe.org.pocketfit.presentations.Meal;

public class PocketFitSchema extends Schema {

    public PocketFitSchema() {
        super(1, "PocketFit.db", MealEntry.class);
    }

    public static class MealEntry extends VersionTable{

        public final String TABLE_NAME = "meals";
        public final ColumnID<Long> _ID = column("_id", Long.class);
        public final ColumnID<String> _MEAL_ID = column("_meal_id", String.class);
        public final ColumnID<Long> _DATE = column("date", Long.class);

        public MealEntry() {
            define(1,TABLE_NAME)
                    .column(_ID, "INTEGER PRIMARY KEY AUTOINCREMENT")
                    .column(_MEAL_ID, "TEXT NOT NULL")
                    .column(_DATE, "INTEGER NOT NULL");
        }

    }
}
