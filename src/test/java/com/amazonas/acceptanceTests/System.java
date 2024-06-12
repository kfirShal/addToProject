package com.amazonas.acceptanceTests;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.authentication.AuthenticationResponse;
import com.amazonas.business.payment.*;
import com.amazonas.business.shipping.ShippingService;
import com.amazonas.business.shipping.ShippingServiceController;
import com.amazonas.repository.UserCredentialsRepository;
import com.amazonas.business.market.MarketInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class System {

    private AuthenticationController authController;
    private PaymentServiceController paymentController;
    private ShippingServiceController shippingController;
    private MarketInitializer marketInitializer;
    private UserCredentialsRepository repository;
    private CreditCard creditCard;

    @BeforeEach
    public void setUp() {
        repository = mock(UserCredentialsRepository.class);
        authController = new AuthenticationController(repository); // Pass password encoder to controller
        paymentController = new PaymentServiceController();
        shippingController = new ShippingServiceController();
        marketInitializer = new MarketInitializer(shippingController, paymentController);
        creditCard = new CreditCard();
    }

    private String simpleHash(String input) {
        return Integer.toString(input.hashCode());
    }


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
        verify(paymentController, times(1)).enableAllPaymentServices();
        verify(shippingController, times(1)).enableAllShippingServices();
    }

    @Test
    public void testSystemStartup_WrongApiKeyOrCredentials() {
        // Arrange
        String adminUserId = "admin";
        String wrongAdminPassword = "wrongPassword";
        String correctAdminPassword = "correctPassword";
        String hashedPassword = simpleHash(correctAdminPassword);
        authController.addUserCredentials(adminUserId, hashedPassword);
        when(repository.getHashedPassword(adminUserId)).thenReturn(hashedPassword);

        // Act
        AuthenticationResponse initialResponse = authController.authenticateUser(adminUserId, wrongAdminPassword);
        AuthenticationResponse finalResponse = authController.authenticateUser(adminUserId, correctAdminPassword);

        // Assert
        assertFalse(initialResponse.success());
        assertTrue(finalResponse.success());

        marketInitializer.start();
    }
}
