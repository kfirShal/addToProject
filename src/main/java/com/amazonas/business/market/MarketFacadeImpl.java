package com.amazonas.business.market;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.payment.PaymentMethod;
import com.amazonas.business.payment.PaymentService;
import com.amazonas.business.shipping.ShippingService;
import com.amazonas.business.stores.Store;
import com.amazonas.business.stores.StoresController;
import com.amazonas.business.stores.StoresControllerImpl;
import com.amazonas.business.userProfiles.User;
import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component("marketFacade")
public class MarketFacadeImpl implements MarketFacade {

    private final StoresController controller;

    public MarketFacadeImpl(StoresController storesController) {
        this.controller = storesController;
    }

    @Override
    public List<Product> searchProducts(GlobalSearchRequest request) {
        List<Store> stores = controller.getAllStores();
        List<Product> ret = new LinkedList<>();
        for (Store store : stores) {
            if (store.getStoreRating() == request.getStoreRating())
            ret.addAll(store.searchProduct(request));
        }
        return ret;
    }

    @Override
    public void makePurchase(User user, String token){
        /*
        ShoppingCart shoppingCart = user.getShoppingCart();
        for (StoreBasket storeBaket : shoppingCart.getBaskets()) {
            Store store = storeBaket.store;
            for (ProdctAmount prodctAmount : storeBaket.PoductAmountsList()) {
                try {
                    store.decreaseProduct(prodctAmount);
                }
                catch (Exception e) {
                    for (ProdctAmount prodctAmount_ : storeBaket.PoductAmountsList()) {
                        if(prodctAmount_ != prodctAmount) {
                            store.increaseProduct(prodctAmount_);
                        }
                        else {
                            return;
                        }
                    }
                }
            }
            try {
                paymentMethod.pay(shoppingCart.getPrice());
            }
            catch (Exception e) {
                for (ProdctAmount prodctAmount : storeBaket.PoductAmountsList()) {
                    store.increaseProduct(prodctAmount);
                }
                return;
            }
            TransactionsController transactionsController = new TransactionsController();
            transactionsController.addTransaction(user);
        }
         */
    }

    @Override
    public void addShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void removeShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void updateShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void enableShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void disableShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void addPaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void removePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void updatePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void enablePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void disablePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void addPaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void removePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void updatePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void enablePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void disablePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void start() throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void shutdown() throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void restart() throws NoPermissionException, AuthenticationFailedException {

    }

}
