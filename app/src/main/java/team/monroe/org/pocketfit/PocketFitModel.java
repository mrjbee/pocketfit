package team.monroe.org.pocketfit;

import android.content.Context;

import org.monroe.team.android.box.app.AndroidModel;
import org.monroe.team.corebox.services.ServiceRegistry;

import team.monroe.org.pocketfit.manage.RoutineManager;


public class PocketFitModel extends AndroidModel {
    public PocketFitModel(Context context) {
        super("pocketfit", context);
    }

    @Override
    protected void constructor(String appName, Context context, ServiceRegistry serviceRegistry) {
        super.constructor(appName, context, serviceRegistry);
        serviceRegistry.registrate(RoutineManager.class, new RoutineManager(context));
    }
}
