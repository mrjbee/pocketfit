package team.monroe.org.pocketfit.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.UUID;

public class CreateId extends UserCaseSupport<String, String>{

    public CreateId(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected String executeImpl(String prefix) {
        String id = UUID.randomUUID().toString();
        return prefix+"_"+id;
    }
}
