package team.monroe.org.pocketfit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.monroe.team.android.box.app.AndroidModel;
import org.monroe.team.android.box.db.DAOFactory;
import org.monroe.team.android.box.db.DAOSupport;
import org.monroe.team.android.box.db.DBHelper;
import org.monroe.team.android.box.db.TransactionManager;
import org.monroe.team.corebox.services.ServiceRegistry;

import team.monroe.org.pocketfit.db.Dao;
import team.monroe.org.pocketfit.db.PocketFitSchema;
import team.monroe.org.pocketfit.manage.PersistManager;


public class PocketFitModel extends AndroidModel {

    public PocketFitModel(Context context) {
        super("pocketfit", context);
    }

    @Override
    protected void constructor(String appName, Context context, ServiceRegistry serviceRegistry) {
        super.constructor(appName, context, serviceRegistry);
        final PocketFitSchema schema = new PocketFitSchema();
        DBHelper helper = new DBHelper(context, schema);
        TransactionManager transactionManager = new TransactionManager(helper, new DAOFactory() {
            @Override
            public DAOSupport createInstanceFor(SQLiteDatabase database) {
                return new Dao(database, schema);
            }
        });
        serviceRegistry.registrate(TransactionManager.class, transactionManager);
        serviceRegistry.registrate(PersistManager.class, new PersistManager(context));
    }
}
