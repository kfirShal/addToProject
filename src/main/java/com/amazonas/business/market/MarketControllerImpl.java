package com.amazonas.business.market;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.payment.PaymentMethod;
import com.amazonas.business.stores.Store;
import com.amazonas.business.stores.StoresControllerImpl;
import com.amazonas.business.userProfiles.ShoppingCart;
import com.amazonas.business.userProfiles.StoreBasket;
import com.amazonas.business.userProfiles.User;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component("marketController")
public class MarketControllerImpl implements MarketController {

    @Override
    public List<Product> searchProducts(GlobalSearchRequest request) {
        StoresControllerImpl storesController = new StoresControllerImpl();
        List<Store> stores = storesController.getAllStores();
        List<Product> ret = new LinkedList<>();
        for (Store store : stores) {
            if (store.storeRate == request.getStoreRating())
            ret.addAll(store.searchProduct(request));
        }
        return ret;
    }


    @Override
    public ShoppingCart getShoppingCartDetails(User user, String token){
        return user.getShoppingCart();
    }

    @Override
    public void makePurchase(User user, String token, PaymentMethod paymentMethod){
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

}
