package team.monroe.org.pocketfit.uc;


import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import team.monroe.org.pocketfit.presentations.Exercise;

public class IsExerciseSafeToChange extends UserCaseSupport<String, Boolean> {

    public IsExerciseSafeToChange(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Boolean executeImpl(String request) {
        //TODO: Implement
        return true;
    }
}
