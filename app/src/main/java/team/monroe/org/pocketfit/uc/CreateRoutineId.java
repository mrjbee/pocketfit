package team.monroe.org.pocketfit.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.UUID;

public class CreateRoutineId extends UserCaseSupport<String, String>{

    public CreateRoutineId(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected String executeImpl(String prefix) {
        String id = UUID.randomUUID().toString();
        return prefix+"_"+id;
    }
}
