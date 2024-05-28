package com.amazonas.business.market;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.payment.PaymentMethod;
import com.amazonas.business.payment.PaymentService;
import com.amazonas.business.shipping.ShippingService;
import com.amazonas.business.stores.Reservation;
import com.amazonas.business.stores.Store;
import com.amazonas.business.stores.StoresController;
import com.amazonas.business.transactions.Transaction;
import com.amazonas.business.transactions.TransactionsController;
import com.amazonas.business.userProfiles.*;
import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;
import com.amazonas.utils.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Component("marketFacade")
public class MarketFacadeImpl implements MarketFacade {

    private final PaymentService currentPaymentService;
    private final ShippingService currentShippingService;
    private final StoresController storesController;
    private final TransactionsController transactionsController;
    private final UsersController usersController;


    private final Map<PaymentMethod, Boolean> paymentMethods;
    private final Map<PaymentService, Boolean> paymentServices;
    private final Map<ShippingService, Boolean> shippingServices;
    private final ReentrantLock paymentMethodsLock;
    private final ReentrantLock paymentServicesLock;
    private final ReentrantLock shippingServicesLock;

    public MarketFacadeImpl(StoresController storesController, TransactionsController transactionsController, UsersController usersController) {
        this.storesController = storesController;
        this.transactionsController = transactionsController;
        this.usersController = usersController;
        paymentMethods = new HashMap<>();
        paymentServices = new HashMap<>();
        shippingServices = new HashMap<>();
        paymentMethodsLock = new ReentrantLock(true);
        paymentServicesLock = new ReentrantLock(true);
        shippingServicesLock = new ReentrantLock(true);
        currentPaymentService = new PaymentService();
        currentShippingService = new ShippingService();
    }

    @Override
    public void makePurchase(String userId, String token){

        //TODO: fix this

        ShoppingCart shoppingCart = usersController.getCart(userId);
        User user = usersController.getUser(userId);
        List<Transaction> transactions = new LinkedList<>();
        List<Reservation> reservations = new LinkedList<>();
        LocalDateTime transactionTime = LocalDateTime.now();

        double totalPrice = 0;

        for (String storeId : shoppingCart.getBaskets().keySet()) {

            // Get the store and the basket
            Store store = storesController.getStore(storeId);
            StoreBasket storeBasket = shoppingCart.getBaskets().get(storeId);

            // Reserve the products
//            Reservation reservation = store.reserveProducts(userId, productsToReserve);
//            reservations.add(reservation);

            // add the price of the products to the total price
//            double price = store.calculatePrice(productsToReserve);
//            totalPrice += price;

            // Create the transaction and add it to the list
            Transaction transaction = new Transaction(storeId,
                    userId,
                    user.getPaymentMethod(),
                    transactionTime,
                    storeBasket.getProductsMap());
            transactions.add(transaction);
        };

        // Charge the user and set the reservations as paid

        if (! currentPaymentService.charge(user.getPaymentMethod(), totalPrice)) {
            for (Reservation reservation : reservations) {
                //TODO: cancel the reservation from the store object, it's better
                reservation.cancelReservation();
            }
            return;
        }

        //TODO: handle the case where the shipping service fails after the payment was successful
        if (! currentShippingService.ship(transactions)) {
            for (Reservation reservation : reservations) {
                //TODO: cancel the reservation from the store object, it's better
                reservation.cancelReservation();
            }
            return;
        }

        for (Reservation reservation : reservations) {
            reservation.setPaid();
        }

        for (Transaction transaction : transactions) {
            transactionsController.documentTransaction(transaction);
        }

        //TODO: return a response to the service layer
    }

    @Override
    public List<Product> searchProducts(GlobalSearchRequest request) {
        Collection<Store> stores = storesController.getAllStores();
        List<Product> ret = new LinkedList<>();
        for (Store store : stores) {
            if (store.getStoreRating().ordinal() >= request.getStoreRating().ordinal()) {
                ret.addAll(store.searchProduct(request));
            }
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
