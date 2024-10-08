package com.amazonas.backend;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.notifications.NotificationController;
import com.amazonas.backend.business.payment.PaymentService;
import com.amazonas.backend.business.payment.PaymentServiceController;
import com.amazonas.backend.business.permissions.PermissionsController;
import com.amazonas.backend.business.shipping.ShippingService;
import com.amazonas.backend.business.shipping.ShippingServiceController;
import com.amazonas.backend.business.stores.Store;
import com.amazonas.backend.business.stores.StoresController;
import com.amazonas.backend.business.userProfiles.UsersController;
import com.amazonas.backend.service.InitialRunFileExecutor;
import com.amazonas.common.DiscountDTOs.*;
import com.amazonas.common.PurchaseRuleDTO.*;
import com.amazonas.common.dtos.Product;
import com.amazonas.common.dtos.Transaction;
import com.amazonas.common.permissions.actions.StoreActions;
import com.amazonas.common.utils.Rating;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;



import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;

@Component
public class DataGenerator {

    private final UsersController usersController;
    private final AuthenticationController authenticationController;
    private final NotificationController notificationController;
    private final PermissionsController permissionsController;
    private final StoresController storesController;
    private final ShippingServiceController shippingServiceController;
    private final PaymentServiceController paymentServiceController;

    public DataGenerator(UsersController usersController, AuthenticationController authenticationController, NotificationController notificationController, PermissionsController permissionsController, StoresController storesController, ShippingServiceController shippingServiceController, PaymentServiceController paymentServiceController, InitialRunFileExecutor initialRunFileExecutor) {
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
        usersController.register("user2@email.com","user2","Password12#", LocalDate.now().minusYears(22));
        usersController.register("user3@email.com","user3","Password12#", LocalDate.now().minusYears(22));
        usersController.register("user4@email.com","user4","Password12#", LocalDate.now().minusYears(22));
        usersController.register("user5@email.com","user5","Password12#", LocalDate.now().minusYears(22));
        usersController.register("user6@email.com","user6","Password12#", LocalDate.now().minusYears(22));
        usersController.register("user7@email.com","user7","Password12#", LocalDate.now().minusYears(22));
        usersController.register("user8@email.com","user8","Password12#", LocalDate.now().minusYears(22));
        usersController.register("user9@email.com","user9","Password12#", LocalDate.now().minusYears(22));

        // create stores
        String store1Id = storesController.addStore("user1", "user1Store", "this is user1 store");
        Store store1 = storesController.getStore(store1Id);
        store1.setStoreRating(Rating.FIVE_STARS);
        String store2Id = storesController.addStore("user2", "user2Store", "this is user2 store");
        String store3Id = storesController.addStore("user3", "user3Store", "this is user3 store");
        String store4Id = storesController.addStore("user4", "user4Store", "this is user4 store");
        String store5Id = storesController.addStore("user5", "user5Store", "this is user5 store");

        // add products
        Product product1 = new Product("product1", "Product 1", 10.0, "category1", "This is product 1", Rating.FIVE_STARS);
        product1.addKeyWords("key1");
        storesController.addProduct(store1Id, product1);
        storesController.setProductQuantity(store1Id, product1.getProductId(), 10);
        Product product2 = new Product("product2", "Product 2", 20.0, "category1", "This is product 2", Rating.FOUR_STARS);
        storesController.addProduct(store1Id, product2);
        product2.addKeyWords("key2");
        storesController.setProductQuantity(store1Id, product2.getProductId(), 20);
        Product product3 = new Product("product3", "Product 3", 30.0, "category2", "This is product 3", Rating.THREE_STARS);
        storesController.addProduct(store2Id, product3);
        storesController.setProductQuantity(store2Id, product3.getProductId(), 30);
        Product product4 = new Product("product4", "Product 4", 40.0, "category2", "This is product 4", Rating.TWO_STARS);
        storesController.addProduct(store2Id, product4);
        storesController.setProductQuantity(store2Id, product4.getProductId(), 40);
        Product product5 = new Product("product5", "Product 5", 50.0, "category3", "This is product 5", Rating.ONE_STAR);
        storesController.addProduct(store3Id, product5);
        storesController.setProductQuantity(store3Id, product5.getProductId(), 50);
        Product product6 = new Product("product6", "Product 6", 60.0, "category3", "This is product 6", Rating.FIVE_STARS);
        storesController.addProduct(store3Id, product6);
        storesController.setProductQuantity(store3Id, product6.getProductId(), 60);
        Product product7 = new Product("product7", "Product 7", 70.0, "category4", "This is product 7", Rating.FOUR_STARS);
        storesController.addProduct(store4Id, product7);
        storesController.setProductQuantity(store4Id, product7.getProductId(), 70);
        Product product8 = new Product("product8", "Product 8", 80.0, "category4", "This is product 8", Rating.THREE_STARS);
        storesController.addProduct(store4Id, product8);
        storesController.setProductQuantity(store4Id, product8.getProductId(), 80);
        Product product9 = new Product("product9", "Product 9", 90.0, "category5", "This is product 9", Rating.TWO_STARS);
        storesController.addProduct(store5Id, product9);
        storesController.setProductQuantity(store5Id, product9.getProductId(), 90);
        Product product10 = new Product("product10", "Product 10", 100.0, "category5", "This is product 10", Rating.ONE_STAR);
        storesController.addProduct(store5Id, product10);
        storesController.setProductQuantity(store5Id, product10.getProductId(), 100);


        // add key words to products
        List<Product> products = List.of(product1, product2, product3, product4, product5, product6, product7, product8, product9, product10);
        for(Product product : products){
            product.addKeyWords(product.getProductName());
        }
        products.getFirst().addKeyWords("Laptop");
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
        usersController.addProductToCart("user1",store1Id, product1.getProductId(),5);
        usersController.addProductToCart("user1",store1Id, product2.getProductId(),5);
        usersController.startPurchase("user1");
        usersController.payForPurchase("user1");
        // set order shipped
        Transaction transaction1 = usersController.getUserTransactionHistory("user1").getFirst();
        storesController.getStore(store1Id).setOrderShipped(transaction1.getTransactionId());

        usersController.addProductToCart("user2",store2Id, product3.getProductId(),5);
        usersController.addProductToCart("user2",store2Id, product4.getProductId(),5);
        usersController.startPurchase("user2");
        usersController.payForPurchase("user2"); // leave order not shipped

        // assign owners
        storesController.getStore(store1Id).addOwner("user1", "user8");
        storesController.getStore(store2Id).addOwner("user2", "user9");

        // assign managers
        storesController.getStore(store1Id).addManager("user1", "user6");
        storesController.getStore(store1Id).addPermissionToManager("user6", StoreActions.ADD_PRODUCT);
        storesController.getStore(store2Id).addManager("user2", "user7");

        // add some products to some carts
        usersController.addProductToCart("user1",store3Id, product5.getProductId(),5);
        usersController.addProductToCart("user3",store3Id, product6.getProductId(),5);
        usersController.addProductToCart("user4",store4Id, product7.getProductId(),5);
        usersController.addProductToCart("user4",store4Id, product8.getProductId(),5);
        usersController.addProductToCart("user5",store5Id, product9.getProductId(),5);
        usersController.addProductToCart("user5",store5Id, product10.getProductId(),5);


        // add discount policy
        // Example discount data creation
        SimpleDiscountDTO simpleDiscount = new SimpleDiscountDTO(HierarchyLevel.ProductLevel, "Product123", 10);
        UnaryConditionDTO condition1 = new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_SOME_PRODUCT, 3, "Product123");
        UnaryConditionDTO condition2 = new UnaryConditionDTO(UnaryConditionType.AT_LEAST_SOME_PRICE, 50, "Product123");
        MultipleConditionDTO orConditions = new MultipleConditionDTO( MultipleConditionType.OR, List.of(condition1, condition2));
        ComplexDiscountDTO complexDiscount = new ComplexDiscountDTO(orConditions, simpleDiscount);
        SimpleDiscountDTO simpleDiscount2 = new SimpleDiscountDTO(HierarchyLevel.CategoryLevel, "Category123", 20);
        List<DiscountComponentDTO> multipleDiscounts = new ArrayList<>();
        multipleDiscounts.add(complexDiscount);
        multipleDiscounts.add(simpleDiscount2);
        MultipleDiscountDTO andDiscount = new MultipleDiscountDTO(MultipleDiscountType.MAXIMUM_PRICE,multipleDiscounts);
        //storesController.addDiscountRuleByDTO(store1Id,andDiscount);


        // add purchase policy
        // Example rule data creation
        NumericalPurchaseRuleDTO ageRestrictionRule = new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.AGE_RESTRICTION, 18);
        DatePurchaseRuleDTO dayRestrictionRule = new DatePurchaseRuleDTO(DatePurchaseRuleType.DAY_RESTRICTION, LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 7));
        ConditionLevelDTO conditionLevelDTO = new ConditionLevelDTO(ConditionLevelType.PRODUCT_LEVEL, "Product123", 5);
        ConditionalPurchaseRuleDTO conditionalPurchaseRule = new ConditionalPurchaseRuleDTO(conditionLevelDTO, ageRestrictionRule);

        List<PurchaseRuleDTO> multipleRules = new ArrayList<>();
        multipleRules.add(dayRestrictionRule);
        multipleRules.add(conditionalPurchaseRule);
        MultiplePurchaseRuleDTO andRule = new MultiplePurchaseRuleDTO(MultiplePurchaseRuleType.AND, multipleRules);

        //storesController.changePurchasePolicy(store1Id,andRule);

        System.out.println("Store1 id: "+store1Id);
    }


    @EventListener
    public void handleApplicationStartedEvent(ApplicationStartedEvent event) {
        try {
//            generateData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
