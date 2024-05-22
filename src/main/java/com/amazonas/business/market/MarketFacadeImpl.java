package com.amazonas.business.market;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.inventory.ProductWithQuantity;
import com.amazonas.business.payment.PaymentMethod;
import com.amazonas.business.payment.PaymentService;
import com.amazonas.business.shipping.ShippingService;
import com.amazonas.business.stores.Reservation;
import com.amazonas.business.stores.Store;
import com.amazonas.business.stores.StoresController;
import com.amazonas.business.stores.StoresControllerImpl;
import com.amazonas.business.transactions.FinalProduct;
import com.amazonas.business.transactions.Transaction;
import com.amazonas.business.transactions.TransactionsController;
import com.amazonas.business.userProfiles.ShoppingCart;
import com.amazonas.business.userProfiles.StoreBasket;
import com.amazonas.business.userProfiles.User;
import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;
import com.amazonas.utils.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Component("marketFacade")
public class MarketFacadeImpl implements MarketFacade {

    private PaymentService currenPaymentService;
    private Map<PaymentMethod, Boolean> paymentMethods;
    private Map<PaymentService, Boolean> paymentServices;
    private Map<ShippingService, Boolean> shippingServices;
    private final ReentrantLock paymentMethodsLock;
    private final ReentrantLock paymentServicesLock;
    private final ReentrantLock shippingServicesLock;
    public MarketFacadeImpl() {
        paymentMethods = new HashMap<>();
        paymentServices = new HashMap<>();
        shippingServices = new HashMap<>();
        paymentMethodsLock = new ReentrantLock(true);
        paymentServicesLock = new ReentrantLock(true);
        shippingServicesLock = new ReentrantLock(true);
        currenPaymentService = new PaymentService();
    }

    @Override
    public List<Product> searchProducts(GlobalSearchRequest request) {
        StoresControllerImpl storesController = new StoresControllerImpl();
        List<Store> stores = storesController.getAllStores();
        List<Product> ret = new LinkedList<>();
        for (Store store : stores) {
            if (store.getStoreRating().ordinal() >= request.getStoreRating().ordinal()) {
                ret.addAll(store.searchProduct(request));
            }
        }
        return ret;
    }

    @Override
    public void makePurchase(User user, String token){
        ShoppingCart shoppingCart = user.getCart();
        List<Transaction> transactions = new LinkedList<>();
        List<Reservation> reservations = new LinkedList<>();
        LocalDateTime transactionTime = LocalDateTime.now();
        double totalPrice = 0;
        for (String storeID : shoppingCart.getBaskets().keySet()) {
            StoresController storesController = new StoresControllerImpl();
            Store store = storesController.getStore(Integer.getInteger(storeID));
            StoreBasket storeBaket = shoppingCart.getBaskets().get(storeID);
            Reservation reservation = null;
            try {
                reservation = store.reserveProducts(user.getUserId(), (List)storeBaket.getProducts().values());
                if (reservation == null) {
                    throw new NullPointerException();
                }
                reservations.add(reservation);
                double price = store.calculateTotalPrice((List)storeBaket.getProducts().values());
                totalPrice += price;
                Transaction transaction = new Transaction(storeID, user.getUserId(), user.getPaymentMethod(), transactionTime, productToFinalProduct(storeBaket.getProducts().values()), price);
                transactions.add(transaction);
            }
            catch (Exception e) {
                return;
            }
        };
        try {
            currenPaymentService.charge(user.getPaymentMethod(), totalPrice);
        }
        catch (Exception e) {
            return;
        }
        for (Reservation reservation : reservations) {
            reservation.isPaid();
        }
        TransactionsController transactionsController = new TransactionsController();
        for (Transaction transaction : transactions) {
            transactionsController.documentTransaction(transaction);
        }
    }

    private Collection<Pair<FinalProduct, Integer>> productToFinalProduct(Collection<Pair<Product, Integer>> products) {
        Collection<Pair<FinalProduct, Integer>> ret = new LinkedList<>();
        for (Pair<Product, Integer> product : products) {
            FinalProduct finalProduct = new FinalProduct(product.first(), product.first().productID());
            ret.add(new Pair(finalProduct, product.second()));
        }
        return ret;
    }

    @Override
    public void addPaymentService(PaymentService newPaymentService) {
        paymentServicesLock.lock();  // block until condition holds
        try {
            paymentServices.put(newPaymentService, true);
        } finally {
            paymentServicesLock.unlock();
        }
    }

    public void removePaymentService(PaymentService oldPaymentService) {
        paymentServicesLock.lock();  // block until condition holds
        try {
            paymentServices.remove(oldPaymentService);
        } finally {
            paymentServicesLock.unlock();
        }
    }

    @Override
    public void updatePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {

    }

    public void addPaymentMethod(PaymentMethod newPaymentMethod) {
        paymentMethodsLock.lock();  // block until condition holds
        try {
            paymentMethods.put(newPaymentMethod, true);
        } finally {
            paymentMethodsLock.unlock();
        }
    }

    public void removePaymentMethod(PaymentMethod oldPaymentMethod) {
        paymentMethodsLock.lock();  // block until condition holds
        try {
            paymentMethods.remove(oldPaymentMethod);
        } finally {
            paymentMethodsLock.unlock();
        }
    }

    @Override
    public void updatePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {

    }

    public void addShippingService(ShippingService newShoppingService) {
        shippingServicesLock.lock();  // block until condition holds
        try {
            shippingServices.put(newShoppingService, true);
        } finally {
            shippingServicesLock.unlock();
        }
    }

    public void removeShippingService(ShippingService oldShoppingService) {
        shippingServicesLock.lock();  // block until condition holds
        try {
            shippingServices.remove(oldShoppingService);
        } finally {
            shippingServicesLock.unlock();
        }
    }

    @Override
    public void updateShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {

    }

    public void enablePaymentService(PaymentService paymentService) {
        paymentServicesLock.lock();  // block until condition holds
        try {
            if(paymentServices.containsKey(paymentService)) {
                paymentServices.put(paymentService, true);
            }
        } finally {
            paymentServicesLock.unlock();
        }
    }

    public void enableAllPaymentServices() {
        paymentServicesLock.lock();  // block until condition holds
        try {
            Set<PaymentService> paymentServiceSet = paymentServices.keySet();
            for (PaymentService PS: paymentServiceSet) {
                paymentServices.put(PS, true);
            }
        } finally {
            paymentServicesLock.unlock();
        }
    }

    public void disablePaymentService(PaymentService paymentService) {
        paymentServicesLock.lock();  // block until condition holds
        try {
            if(paymentServices.containsKey(paymentService)) {
                paymentServices.put(paymentService, false);
            }
        } finally {
            paymentServicesLock.unlock();
        }
    }

    public void disableAllPaymentServices() {
        paymentServicesLock.lock();  // block until condition holds
        try {
            Set<PaymentService> paymentServiceSet = paymentServices.keySet();
            for (PaymentService PS: paymentServiceSet) {
                paymentServices.put(PS, false);
            }
        } finally {
            paymentServicesLock.unlock();
        }
    }

    public void enablePaymentMethod(PaymentMethod paymentMethod) {
        paymentMethodsLock.lock();  // block until condition holds
        try {
            if(paymentMethods.containsKey(paymentMethod)) {
                paymentMethods.put(paymentMethod, true);
            }
        } finally {
            paymentMethodsLock.unlock();
        }
    }

    public void enableAllPaymentMethods() {
        paymentMethodsLock.lock();  // block until condition holds
        try {
            Set<PaymentMethod> paymentMethodSet = paymentMethods.keySet();
            for (PaymentMethod PM: paymentMethodSet) {
                paymentMethods.put(PM, true);
            }
        } finally {
            paymentMethodsLock.unlock();
        }
    }

    public void disablePaymentMethod(PaymentMethod paymentMethod) {
        paymentMethodsLock.lock();  // block until condition holds
        try {
            if(paymentMethods.containsKey(paymentMethod)) {
                paymentMethods.put(paymentMethod, false);
            }
        } finally {
            paymentMethodsLock.unlock();
        }
    }

    public void disableAllPaymentMethods() {
        paymentMethodsLock.lock();  // block until condition holds
        try {
            Set<PaymentMethod> paymentMethodSet = paymentMethods.keySet();
            for (PaymentMethod PM: paymentMethodSet) {
                paymentMethods.put(PM, false);
            }
        } finally {
            paymentMethodsLock.unlock();
        }
    }

    public void enableShippingService(ShippingService shippingService) {
        shippingServicesLock.lock();  // block until condition holds
        try {
            if(shippingServices.containsKey(shippingService)) {
                shippingServices.put(shippingService, true);
            }
        } finally {
            shippingServicesLock.unlock();
        }
    }

    public void enableAllShippingServices() {
        shippingServicesLock.lock();  // block until condition holds
        try {
            Set<ShippingService> shippingServiceSet = shippingServices.keySet();
            for (ShippingService SS: shippingServiceSet) {
                shippingServices.put(SS, true);
            }
        } finally {
            shippingServicesLock.unlock();
        }
    }

    public void disableShippingService(ShippingService shippingService) {
        shippingServicesLock.lock();  // block until condition holds
        try {
            if(shippingServices.containsKey(shippingService)) {
                shippingServices.put(shippingService, false);
            }
        } finally {
            shippingServicesLock.unlock();
        }
    }

    public void disableAllShippingServices() {
        shippingServicesLock.lock();  // block until condition holds
        try {
            Set<ShippingService> shippingServiceSet = shippingServices.keySet();
            for (ShippingService SS: shippingServiceSet) {
                shippingServices.put(SS, false);
            }
        } finally {
            shippingServicesLock.unlock();
        }
    }

    @Override
    public void start() throws NoPermissionException, AuthenticationFailedException {
        enableAllShippingServices();
        enableAllPaymentServices();
        enableAllPaymentMethods();
    }

    @Override
    public void shutdown() throws NoPermissionException, AuthenticationFailedException {
        disableAllShippingServices();
        disableAllPaymentServices();
        disableAllPaymentMethods();
    }

    @Override
    public void restart() throws NoPermissionException, AuthenticationFailedException {
        enableAllShippingServices();
        enableAllPaymentServices();
        enableAllPaymentMethods();
    }

}
