package team.monroe.org.pocketfit.uc;

import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.Exercise;
import team.monroe.org.pocketfit.presentations.Product;

public class GetProductById extends UserCaseSupport<String, Product>{

    public GetProductById(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Product executeImpl(String id) {
        return using(PersistManager.class).getProduct(id);
    }
}
