package com.amazonas.backend;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.notifications.NotificationController;
import com.amazonas.backend.business.payment.PaymentService;
import com.amazonas.backend.business.payment.PaymentServiceController;
import com.amazonas.backend.business.permissions.PermissionsController;
import com.amazonas.backend.business.shipping.ShippingService;
import com.amazonas.backend.business.shipping.ShippingServiceController;
import com.amazonas.backend.business.stores.StoresController;
import com.amazonas.backend.business.userProfiles.UsersController;
import com.amazonas.common.dtos.Product;
import com.amazonas.common.dtos.Transaction;
import com.amazonas.common.utils.Rating;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataGenerator {

    private final UsersController usersController;
    private final AuthenticationController authenticationController;
    private final NotificationController notificationController;
    private final PermissionsController permissionsController;
    private final StoresController storesController;
    private final ShippingServiceController shippingServiceController;
    private final PaymentServiceController paymentServiceController;

    public DataGenerator(UsersController usersController, AuthenticationController authenticationController, NotificationController notificationController, PermissionsController permissionsController, StoresController storesController, ShippingServiceController shippingServiceController, PaymentServiceController paymentServiceController) {
        this.usersController = usersController;
        this.authenticationController = authenticationController;
        this.notificationController = notificationController;
        this.permissionsController = permissionsController;
        this.storesController = storesController;
        this.shippingServiceController = shippingServiceController;
        this.paymentServiceController = paymentServiceController;
    }

    public void generateData() throws Exception {
        // create users
        usersController.register("user1@email.com","user1","Password12#", LocalDate.now().minusYears(22));
        usersController.register("user2@email.com","user2","Password22#", LocalDate.now().minusYears(22));
        usersController.register("user3@email.com","user3","Password32#", LocalDate.now().minusYears(22));
        usersController.register("user4@email.com","user4","Password42#", LocalDate.now().minusYears(22));
        usersController.register("user5@email.com","user5","Password52#", LocalDate.now().minusYears(22));
        usersController.register("user6@email.com","user6","Password62#", LocalDate.now().minusYears(22));
        usersController.register("user7@email.com","user7","Password72#", LocalDate.now().minusYears(22));
        usersController.register("user8@email.com","user8","Password82#", LocalDate.now().minusYears(22));
        usersController.register("user9@email.com","user9","Password92#", LocalDate.now().minusYears(22));

        // create stores
        String store1Id = storesController.addStore("user1", "user1Store", "this is user1 store");
        String store2Id = storesController.addStore("user2", "user2Store", "this is user2 store");
        String store3Id = storesController.addStore("user3", "user3Store", "this is user3 store");
        String store4Id = storesController.addStore("user4", "user4Store", "this is user4 store");
        String store5Id = storesController.addStore("user5", "user5Store", "this is user5 store");

        // add products
        Product product1 = new Product("product1", "Product 1", 10.0, "category1", "This is product 1", Rating.FIVE_STARS);
        storesController.addProduct(store1Id, product1);
        storesController.setProductQuantity(store1Id, product1.productId(), 10);
        Product product2 = new Product("product2", "Product 2", 20.0, "category1", "This is product 2", Rating.FOUR_STARS);
        storesController.addProduct(store1Id, product2);
        storesController.setProductQuantity(store1Id, product2.productId(), 20);
        Product product3 = new Product("product3", "Product 3", 30.0, "category2", "This is product 3", Rating.THREE_STARS);
        storesController.addProduct(store2Id, product3);
        storesController.setProductQuantity(store2Id, product3.productId(), 30);
        Product product4 = new Product("product4", "Product 4", 40.0, "category2", "This is product 4", Rating.TWO_STARS);
        storesController.addProduct(store2Id, product4);
        storesController.setProductQuantity(store2Id, product4.productId(), 40);
        Product product5 = new Product("product5", "Product 5", 50.0, "category3", "This is product 5", Rating.ONE_STAR);
        storesController.addProduct(store3Id, product5);
        storesController.setProductQuantity(store3Id, product5.productId(), 50);
        Product product6 = new Product("product6", "Product 6", 60.0, "category3", "This is product 6", Rating.FIVE_STARS);
        storesController.addProduct(store3Id, product6);
        storesController.setProductQuantity(store3Id, product6.productId(), 60);
        Product product7 = new Product("product7", "Product 7", 70.0, "category4", "This is product 7", Rating.FOUR_STARS);
        storesController.addProduct(store4Id, product7);
        storesController.setProductQuantity(store4Id, product7.productId(), 70);
        Product product8 = new Product("product8", "Product 8", 80.0, "category4", "This is product 8", Rating.THREE_STARS);
        storesController.addProduct(store4Id, product8);
        storesController.setProductQuantity(store4Id, product8.productId(), 80);
        Product product9 = new Product("product9", "Product 9", 90.0, "category5", "This is product 9", Rating.TWO_STARS);
        storesController.addProduct(store5Id, product9);
        storesController.setProductQuantity(store5Id, product9.productId(), 90);
        Product product10 = new Product("product10", "Product 10", 100.0, "category5", "This is product 10", Rating.ONE_STAR);
        storesController.addProduct(store5Id, product10);
        storesController.setProductQuantity(store5Id, product10.productId(), 100);

        // add shipping services
        shippingServiceController.addShippingService("service1", new ShippingService());

        // add payment services
        paymentServiceController.addPaymentService("service1", new PaymentService());

        //send notifications
        for (int i = 1; i <= 9; i++) {
            for(int j = 1; j <= 20; j++){
                notificationController.sendNotification("title" + j, "message" + j, "Amazonas", "user" + i);
            }
        }

        // make some transaction
        usersController.addProductToCart("user1",store1Id, product1.productId(),5);
        usersController.addProductToCart("user1",store1Id, product2.productId(),5);
        usersController.startPurchase("user1");
        usersController.payForPurchase("user1");
        // set order shipped
        Transaction transaction1 = usersController.getUserTransactionHistory("user1").getFirst();
        storesController.getStore(store1Id).setOrderShipped(transaction1.transactionId());

        usersController.addProductToCart("user2",store2Id, product3.productId(),5);
        usersController.addProductToCart("user2",store2Id, product4.productId(),5);
        usersController.startPurchase("user2");
        usersController.payForPurchase("user2"); // leave order not shipped

        // assign owners
        storesController.getStore(store1Id).addOwner("user1", "user8");
        storesController.getStore(store2Id).addOwner("user2", "user9");

        // assign managers
        storesController.getStore(store1Id).addManager("user1", "user6");
        storesController.getStore(store2Id).addManager("user2", "user7");

        // add some products to some carts
        usersController.addProductToCart("user3",store3Id, product5.productId(),5);
        usersController.addProductToCart("user3",store3Id, product6.productId(),5);
        usersController.addProductToCart("user4",store4Id, product7.productId(),5);
        usersController.addProductToCart("user4",store4Id, product8.productId(),5);
        usersController.addProductToCart("user5",store5Id, product9.productId(),5);
        usersController.addProductToCart("user5",store5Id, product10.productId(),5);
    }


    @EventListener
    public void handleApplicationStartedEvent(ApplicationStartedEvent event) {
        try {
            generateData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
