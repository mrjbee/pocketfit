package team.monroe.org.pocketfit.uc;

import org.monroe.team.corebox.app.Model;
import org.monroe.team.corebox.services.ServiceRegistry;
import org.monroe.team.corebox.uc.UserCaseSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import team.monroe.org.pocketfit.manage.PersistManager;
import team.monroe.org.pocketfit.presentations.Exercise;
import team.monroe.org.pocketfit.presentations.Meal;
import team.monroe.org.pocketfit.presentations.Product;

public class GetProductList extends UserCaseSupport<Void, List<Product>>{

    public GetProductList(ServiceRegistry serviceRegistry) {
        super(serviceRegistry);
    }

    @Override
    protected  List<Product> executeImpl(Void request) {
        Set<String> idSet = using(PersistManager.class).listProductIds();
        List<Product> answer = new ArrayList<>(idSet.size());
        for (String exId : idSet) {
            Product product = using(Model.class).execute(GetProductById.class, exId);
            answer.add(product);
        }
        Collections.sort(answer, new Comparator<Product>() {
            @Override
            public int compare(Product lhs, Product rhs) {
                return lhs.title.compareTo(rhs.title);
            }
        });
        return answer;
    }
}
