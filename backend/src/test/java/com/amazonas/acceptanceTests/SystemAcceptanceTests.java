package com.amazonas.acceptanceTests;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.authentication.AuthenticationResponse;
import com.amazonas.backend.business.authentication.UserCredentials;
import com.amazonas.backend.business.payment.*;
import com.amazonas.backend.business.permissions.proxies.NotificationProxy;
import com.amazonas.backend.business.shipping.ShippingService;
import com.amazonas.backend.business.shipping.ShippingServiceController;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import com.amazonas.backend.exceptions.NoPermissionException;
import com.amazonas.backend.exceptions.NotificationException;
import com.amazonas.backend.service.NotificationsService;
import com.amazonas.common.dtos.Transaction;
import com.amazonas.backend.repository.StoreRepository;
import com.amazonas.backend.repository.TransactionRepository;
import com.amazonas.backend.repository.UserCredentialsRepository;
import com.amazonas.backend.business.market.MarketInitializer;
import com.amazonas.common.requests.Request;
import com.amazonas.common.requests.notifications.NotificationRequest;
import com.amazonas.common.requests.shipping.ShipmentRequest;
import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SystemAcceptanceTests {

    private AuthenticationController authController;
    private PaymentServiceController paymentController;
    private ShippingServiceController shippingController;
    private MarketInitializer marketInitializer;
    private UserCredentialsRepository repository;
    private CreditCard creditCard;
    private StoreRepository storeRepository;
    private TransactionRepository transactionRepository;

    @Mock
    private NotificationProxy notificationProxy;
    @InjectMocks
    private NotificationsService notificationService;

    @BeforeEach
    public void setUp() {
        //Mocks
        storeRepository = mock(StoreRepository.class);
        repository = mock(UserCredentialsRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        notificationProxy = mock(NotificationProxy.class);
        authController = new AuthenticationController(repository); // Pass password encoder to controller
        paymentController = new PaymentServiceController();
        shippingController = new ShippingServiceController(storeRepository,transactionRepository);
        marketInitializer = new MarketInitializer(shippingController, paymentController);
        notificationService = new NotificationsService(notificationProxy);
        creditCard = new CreditCard();

    }

    private String simpleHash(String input) {
        return Integer.toString(input.hashCode());
    }


    //-------------------------System Startup-------------------------

    @Test
    public void testSystemStartup_Success() {
        // Arrange
        PaymentService pService = new PaymentService();
        paymentController.addPaymentService(String.valueOf(0), pService);
        paymentController.addPaymentMethod(creditCard);
        ShippingService sService = new ShippingService();
        shippingController.addShippingService(String.valueOf(0), sService);

        // Act
        marketInitializer.start();

        // Assert
        assertTrue(paymentController.areAllPaymentServicesEnabled());
        assertTrue(paymentController.areAllPaymentMethodsEnabled());
        assertTrue(shippingController.areAllShippingServicesEnabled());

    }

    @Test
    public void testSystemStartup_FailureToConnectServices() {
        // Arrange
        paymentController = mock(PaymentServiceController.class);
        shippingController = mock(ShippingServiceController.class);
        marketInitializer = new MarketInitializer(shippingController, paymentController);

        PaymentService pService = mock(PaymentService.class);
        paymentController.addPaymentService("0", pService);
        paymentController.addPaymentMethod(creditCard);

        ShippingService sService = mock(ShippingService.class);
        shippingController.addShippingService("0", sService);

        doThrow(new RuntimeException("Failed to connect to payment services"))
                .when(paymentController).enableAllPaymentServices();
        doThrow(new RuntimeException("Failed to connect to shipping services"))
                .when(shippingController).enableAllShippingServices();

        // Act
        try {
            marketInitializer.start();
        } catch (RuntimeException e) {
            // Expected exception
        }

        // Assert
        assertFalse(paymentController.areAllPaymentServicesEnabled());
        assertFalse(paymentController.areAllPaymentMethodsEnabled());
        assertFalse(shippingController.areAllShippingServicesEnabled());
    }

    @Test
    public void testSystemStartup_WrongApiKeyOrCredentials() {
        // Arrange
        String adminUserId = "admin";
        String wrongAdminPassword = "wrongPassword";
        String correctAdminPassword = "correctPassword";
        String hashedPassword = simpleHash(correctAdminPassword);
        UserCredentials adminUser = new UserCredentials(adminUserId, hashedPassword);
        authController.createUser(adminUser);
        when(repository.getHashedPassword(adminUserId)).thenReturn(hashedPassword);

        // Act
        AuthenticationResponse initialResponse = authController.authenticateUser(adminUserId, wrongAdminPassword);
        AuthenticationResponse finalResponse = authController.authenticateUser(adminUserId, correctAdminPassword);

        // Assert
        assertFalse(initialResponse.success());
        assertTrue(finalResponse.success());

        marketInitializer.start();
    }

    //-------------------------Adding / removing / changing an external service-------------------------

    @Test
    public void testAddExternalService_Success() {
        // Arrange
        PaymentService newService = mock(PaymentService.class);
        when(newService.charge(any(PaymentMethod.class), anyDouble())).thenReturn(true);

        // Act
        paymentController.addPaymentService("service1", newService);
        boolean paymentResult = paymentController.processPayment(new PaymentRequest("service1", creditCard, 100.0));

        // Assert
        assertTrue(paymentResult);
        assertTrue(paymentController.areAllPaymentServicesEnabled());
    }

    @Test
    public void testRemoveOnlyPaymentService_Failure() {
        // Arrange
        PaymentService onlyService = mock(PaymentService.class);
        paymentController.addPaymentService("service1", onlyService);

        // Act
        paymentController.removePaymentService("service1");
        boolean paymentServiceExists = paymentController.areAllPaymentServicesEnabled();

        // Assert
        assertFalse(paymentServiceExists);
        assertFalse(paymentController.areAllPaymentServicesEnabled());
    }

    @Test
    public void testAddExternalService_WrongDetailsThenFix_Success() {
        // Arrange
        PaymentService faultyService = mock(PaymentService.class);
        when(faultyService.charge(any(PaymentMethod.class), anyDouble())).thenThrow(new RuntimeException("Connection failed"));

        // Act
        paymentController.addPaymentService("service1", faultyService);
        boolean initialPaymentResult = false;
        try {
            initialPaymentResult = paymentController.processPayment(new PaymentRequest("service1", creditCard, 100.0));
        } catch (RuntimeException e) {
            // Expected exception
        }

        // Assert initial failure
        assertFalse(initialPaymentResult);
        assertFalse(paymentController.areAllPaymentServicesEnabled());

        // Fix the service details
        PaymentService fixedService = mock(PaymentService.class);
        when(fixedService.charge(any(PaymentMethod.class), anyDouble())).thenReturn(true);
        paymentController.updatePaymentService("service1", fixedService);

        // Act again with correct service details
        boolean finalPaymentResult = paymentController.processPayment(new PaymentRequest("service1", creditCard, 100.0));

        // Assert final success
        assertTrue(finalPaymentResult);
        assertTrue(paymentController.areAllPaymentServicesEnabled());
    }

    //-------------------------Payment-------------------------

    @Test
    public void testPayment_Success() {
        // Arrange
        PaymentService pService = new PaymentService();

        // Act
        boolean paymentResult = pService.charge(creditCard, 100.0);

        // Assert
        assertTrue(paymentResult);
    }

    @Test
    public void testPayment_AmountZeroFailure() {
        // Arrange
        PaymentService pService = new PaymentService();

        // Act
        boolean paymentResult = pService.charge(creditCard, 0.0);

        // Assert
        assertFalse(paymentResult);
    }

    //-------------------------Supply-------------------------

    @Test
    public void testShippingOrder_Success() {
        // Arrange
        Transaction transaction = new Transaction("tx123", "store1", "user1", LocalDateTime.now(), new HashMap<>());
        ShipmentRequest request = new ShipmentRequest(transaction.getTransactionId(), "shippingService1", transaction.getStoreId());
        ShippingService shippingService = mock(ShippingService.class);
        shippingController.addShippingService("shippingService1", shippingService);

        // Act
        boolean shippingResult =  shippingController.sendShipment(request.transactionId(),request.serviceId());

        // Assert
        assertTrue(shippingResult);
    }

    @Test
    public void testShippingOrder_InvalidAddress_Failure() {
        // Arrange
        Transaction transaction = new Transaction("tx123", "store1", "user1", LocalDateTime.now(), new HashMap<>());
        ShipmentRequest request = new ShipmentRequest(transaction.getTransactionId(), "shippingService1", transaction.getStoreId());
        ShippingService shippingService = mock(ShippingService.class);
        ShippingServiceController shippingController = new ShippingServiceController(storeRepository,transactionRepository);

        when(shippingService.ship(transaction)).thenThrow(new RuntimeException("Invalid address"));

        // Act
        shippingController.addShippingService("shippingService1", shippingService);
        boolean result = false;
        try {
            result = shippingController.sendShipment(request.transactionId(),request.serviceId());
        } catch (RuntimeException e) {
            // Expected exception
        }

        // Assert
        assertFalse(result);
    }

    @Test
    public void testShippingOrder_CorrectAddressAfterFailure_Success() {
        // Arrange
        Transaction transaction = new Transaction("tx123", "store1", "user1", LocalDateTime.now(), new HashMap<>());
        ShipmentRequest request = new ShipmentRequest(transaction.getTransactionId(), "shippingService1", transaction.getStoreId());
        ShippingService shippingService = mock(ShippingService.class);

        ShippingServiceController shippingController = new ShippingServiceController(storeRepository,transactionRepository);

        when(shippingService.ship(transaction)).thenThrow(new RuntimeException("Invalid address"));

        // Act
        shippingController.addShippingService("shippingService1", shippingService);
        boolean initialResult = false;
        try {
            initialResult = shippingController.sendShipment(request.transactionId(),request.serviceId());
        } catch (RuntimeException e) {
            // Expected exception
        }

        // Assert initial failure
        assertFalse(initialResult);

        // User corrects the address
        when(shippingService.ship(transaction)).thenReturn(true);

        // Act again with the corrected address
        boolean finalResult = shippingController.sendShipment(request.transactionId(),request.serviceId());

        // Assert final success
        assertTrue(finalResult);
    }

    //-------------------------------Notifications-------------------------------

    @Test
    void testSendNotification_Success() throws AuthenticationFailedException, NotificationException, NoPermissionException {

        String title = "title";
        String message = "message";
        String senderId = "senderId";
        String receiverId = "receiverId";
        String userId = "userId";
        String token = "token";
        NotificationRequest notificationRequest = new NotificationRequest(title, message, senderId, receiverId);
        Request request = new Request(userId, token, JsonUtils.serialize(notificationRequest));
        doNothing().when(notificationProxy).sendNotification(title, message, senderId, receiverId, userId, request.token());
        String result = notificationService.sendNotification(request.toJson());
        assertEquals(Response.getOk(), result);
    }

    @Test
    void testSendNotification_FailureReceiverIdNotExist() throws AuthenticationFailedException, NotificationException, NoPermissionException {

        String title = "title";
        String message = "message";
        String senderId = "senderId";
        String receiverId = "wrongReceiverId";
        String userId = "userId";
        String token = "token";
        NotificationRequest notificationRequest = new NotificationRequest(title, message, senderId, receiverId);
        Request request = new Request(userId, token, JsonUtils.serialize(notificationRequest));
        doThrow(new NotificationException("Failed to send notification - receiver user does not exists")).when(notificationProxy).sendNotification(title, message, senderId, receiverId, userId, request.token());
        String result = notificationService.sendNotification(request.toJson());
        assertEquals(Response.getError(new NotificationException("Failed to send notification - receiver user does not exists")), result);
    }

    @Test
    void testSendNotification_FailureSenderIdNotExist() throws AuthenticationFailedException, NotificationException, NoPermissionException {

        String title = "title";
        String message = "message";
        String senderId = "wrongSenderId";
        String receiverId = "ReceiverId";
        String userId = "userId";
        String token = "token";
        NotificationRequest notificationRequest = new NotificationRequest(title, message, senderId, receiverId);
        Request request = new Request(userId, token, JsonUtils.serialize(notificationRequest));
        doThrow(new NotificationException("Failed to send notification - sender user does not exists")).when(notificationProxy).sendNotification(title, message, senderId, receiverId, userId, request.token());
        String result = notificationService.sendNotification(request.toJson());
        assertEquals(Response.getError(new NotificationException("Failed to send notification - sender user does not exists")), result);
    }


}
