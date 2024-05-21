package com.amazonas.business.market;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.inventory.ProductWithQuantity;
import com.amazonas.business.payment.PaymentMethod;
import com.amazonas.business.payment.PaymentService;
import com.amazonas.business.shipping.ShippingService;
import com.amazonas.business.stores.Store;
import com.amazonas.business.stores.StoresControllerImpl;
import com.amazonas.business.userProfiles.ShoppingCart;
import com.amazonas.business.userProfiles.StoreBasket;
import com.amazonas.business.userProfiles.User;
import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component("marketFacade")
public class MarketFacadeImpl implements MarketFacade {

    ExternalServices externalServicesController;
    public MarketFacadeImpl() {
        externalServicesController = ExternalServices.getInstance();
    }

    @Override
    public List<Product> searchProducts(GlobalSearchRequest request) {
        StoresControllerImpl storesController = new StoresControllerImpl();
        List<Store> stores = storesController.getAllStores();
        List<Product> ret = new LinkedList<>();
        for (Store store : stores) {
            if (store.storeRating.ordinal() >= request.getStoreRating().ordinal()) {
                ret.addAll(store.searchProduct(request));
            }
        }
        return ret;
    }

    @Override
    public void makePurchase(User user, String token){
        ShoppingCart shoppingCart = user.getCart();
        for (String storeID : shoppingCart.getBaskets().keySet()) {
            Store store = getStore(storeID);
            StoreBasket storeBaket = shoppingCart.getBaskets().get(storeID);
            for (ProductWithQuantity prodctAmount : storeBaket.getProducts().values()) {
                try {
                    //TODO after store will be implemented
                    //store.decreaseProduct(prodctAmount);
                }
                catch (Exception e) {
                    for (ProductWithQuantity prodctAmount_ : storeBaket.getProducts().values()) {
                        if(prodctAmount_ != prodctAmount) {
                            //store.increaseProduct(prodctAmount_);
                        }
                        else {
                            return;
                        }
                    }
                }
            }
            /*
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
             */
        }
    }

    private Store getStore(String storeID) {
        return null;
    }

    @Override
    public void addShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {
        externalServicesController.addShippingService(shippingService);
    }

    @Override
    public void removeShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {
        externalServicesController.removeShippingService(shippingService);
    }

    @Override
    public void updateShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void enableShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {
        externalServicesController.enableShippingService(shippingService);
    }

    @Override
    public void disableShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {
        externalServicesController.disableShippingService(shippingService);
    }

    @Override
    public void addPaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {
        externalServicesController.addPaymentService(paymentService);
    }

    @Override
    public void removePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {
        externalServicesController.removePaymentService(paymentService);
    }

    @Override
    public void updatePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void enablePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {
        externalServicesController.enablePaymentService(paymentService);
    }

    @Override
    public void disablePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {
        externalServicesController.disablePaymentService(paymentService);
    }

    @Override
    public void addPaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {
        externalServicesController.addPaymentMethod(paymentMethod);
    }

    @Override
    public void removePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {
        externalServicesController.removePaymentMethod(paymentMethod);
    }

    @Override
    public void updatePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void enablePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {
        externalServicesController.enablePaymentMethod(paymentMethod);
    }

    @Override
    public void disablePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {
        externalServicesController.disablePaymentMethod(paymentMethod);
    }

    @Override
    public void start() throws NoPermissionException, AuthenticationFailedException {
        externalServicesController.enableAllShippingServices();
        externalServicesController.enableAllPaymentServices();
        externalServicesController.enableAllPaymentMethods();
    }

    @Override
    public void shutdown() throws NoPermissionException, AuthenticationFailedException {
        externalServicesController.disableAllShippingServices();
        externalServicesController.disableAllPaymentServices();
        externalServicesController.disableAllPaymentMethods();
    }

    @Override
    public void restart() throws NoPermissionException, AuthenticationFailedException {
        externalServicesController.enableAllShippingServices();
        externalServicesController.enableAllPaymentServices();
        externalServicesController.enableAllPaymentMethods();
    }

}
