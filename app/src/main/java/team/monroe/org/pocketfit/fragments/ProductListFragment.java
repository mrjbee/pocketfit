package team.monroe.org.pocketfit.fragments;

import org.monroe.team.android.box.data.Data;

import java.util.List;

import team.monroe.org.pocketfit.FoodActivity;
import team.monroe.org.pocketfit.FragmentActivity;
import team.monroe.org.pocketfit.PocketFitApp;
import team.monroe.org.pocketfit.fragments.contract.ExerciseOwnerContract;
import team.monroe.org.pocketfit.presentations.Exercise;
import team.monroe.org.pocketfit.presentations.Product;

public class ProductListFragment extends GenericListFragment<Product, FoodActivity> {

    private Data.DataChangeObserver<List> dataChangeObserver;

    @Override
    protected void onNewItemClick() {
        application().function_createId("product",observe_function(State.STOP, new PocketFitApp.DataAction<String>() {
            @Override
            public void data(String id) {
               owner().editProduct(id);
            }
        }));
    }

    @Override
    protected void onItemClick(Product product) {
        if (getBoolArgument("chooserMode")){
          owner().onProductSelected(product.id);
        }else {
          onItemEdit(product);
        }
    }

    @Override
    protected void onItemEdit(Product product) {
        owner().editProduct(product.id);
    }

    @Override
    protected boolean isInlineEditAllowed() {
        return getBoolArgument("chooserMode");
    }

    @Override
    protected String item_text(Product product) {
        return product.carbsFatsProteinString();
    }

    @Override
    protected String item_subCaption(Product product) {
        return product.calories + " calories";
    }

    @Override
    protected String item_caption(Product product) {
        return product.title;
    }

    @Override
    protected String getNewItemCaption() {
        return "Do you want to create new product?";
    }

    @Override
    protected boolean isHeaderSecondary() {
        return getBoolArgument("chooserMode");
    }

    @Override
    protected String getHeaderName() {
        return !getBoolArgument("chooserMode")?"Products":"Choose Product";
    }

    @Override
    public void onStart() {
        super.onStart();
        dataChangeObserver = new Data.DataChangeObserver<List>() {
            @Override
            public void onDataInvalid() {
                updateProductsList();
            }

            @Override
            public void onData(List list) {

            }
        };
        application().data_products().addDataChangeObserver(
                dataChangeObserver
        );
        updateProductsList();
    }

    private void updateProductsList() {
        application().data_products().fetch(true, observe_data_fetch(State.STOP, new PocketFitApp.DataAction<List>() {
            @Override
            public void data(List data) {
                updateItems(data);
            }
        }));
    }

    @Override
    public void onStop() {
        super.onStop();
        application().data_products().removeDataChangeObserver(dataChangeObserver);
    }

}
