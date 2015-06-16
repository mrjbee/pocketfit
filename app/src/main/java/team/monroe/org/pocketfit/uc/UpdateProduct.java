package team.monroe.org.pocketfit.uc;

import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.Exercise;
import team.monroe.org.pocketfit.presentations.Product;

public class UpdateProduct extends UserCaseSupport<Product, Void>{

    public UpdateProduct(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected Void executeImpl(Product updatePersist) {

        Product originPersist = using(PersistManager.class).getProduct(updatePersist.id);

        if (originPersist == null) {
            if (isDefined(updatePersist)){
                using(PersistManager.class).updateOrCreateProduct(updatePersist);
            }
            return null;
        }

        originPersist.title = diff(originPersist.title, updatePersist.title);
        originPersist.calories = diff(originPersist.calories, updatePersist.calories);
        originPersist.protein = diff(originPersist.protein, updatePersist.protein);
        originPersist.carbs = diff(originPersist.carbs, updatePersist.carbs);
        originPersist.fats = diff(originPersist.fats, updatePersist.fats);

        if (isDefined(originPersist)) {
            using(PersistManager.class).updateOrCreateProduct(originPersist);
        }
        return null;
    }


    private boolean isDefined(Product product) {
        return isDefined(product.title)
                && isDefined(product.calories)
                && isDefined(product.carbs)
                && isDefined(product.fats)
                && isDefined(product.protein);
    }


    private boolean isDefined(Object object) {
        return object != null;
    }


    private boolean isDefined(String string) {
        return string != null && !string.trim().isEmpty();
    }

    private <Type> Type diff(Type origin, Type update) {
        return update != null? update:origin;
    }

}
