package com.amazonas.business.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.amazonas.business.stores.Rating.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductInventoryTest {

    private GlobalProductTracker tracker;
    private ProductInventory inventory;
    private Product product;

    @BeforeEach
    void setUp() {
        tracker = mock(GlobalProductTracker.class);
        inventory = new ProductInventory(tracker, "StoreA");
        product = new Product("1", "Shirt", 50, "Shirts", "black", FOUR_STARS);
    }

    @Test
    void addProduct() {
        when(tracker.productExists(anyString())).thenReturn(false);

        inventory.addProduct(product);

        assertTrue(inventory.getAllAvailableProducts().contains(product));
        verify(tracker).addProduct(anyString(), eq("StoreA"));
    }

    @Test
    void updateProduct() {
        when(tracker.productExists(anyString())).thenReturn(false);

        inventory.addProduct(product);

        Product updatedProduct = new Product(product.productId(), "New Shirt", 60, "Shirts", "blue", FIVE_STARS);
        boolean result = inventory.updateProduct(updatedProduct);

        assertTrue(result);
        assertEquals("New Shirt", product.productName());
        assertEquals(60, product.price());
        assertEquals("blue", product.description());
    }

    @Test
    void removeProduct() {
        when(tracker.productExists(anyString())).thenReturn(false);

        inventory.addProduct(product);
        inventory.disableProduct(product);

        boolean result = inventory.removeProduct(product);

        assertTrue(result);
        assertFalse(inventory.getAllAvailableProducts().contains(product));
    }

    @Test
    void setQuantity() {
        when(tracker.productExists(anyString())).thenReturn(false);

        inventory.addProduct(product);
        inventory.setQuantity(product, 10);

        assertEquals(10, inventory.getQuantity(product));
    }

    @Test
    void getQuantity() {
        when(tracker.productExists(anyString())).thenReturn(false);

        inventory.addProduct(product);
        inventory.setQuantity(product, 10);

        int quantity = inventory.getQuantity(product);

        assertEquals(10, quantity);
    }


    @Test
    void enableProduct() {
        when(tracker.productExists(anyString())).thenReturn(false);

        inventory.addProduct(product);
        inventory.disableProduct(product);

        boolean result = inventory.enableProduct(product);

        assertTrue(result);
        assertFalse(inventory.isProductDisabled(product));
    }

    @Test
    void disableProduct() {
        when(tracker.productExists(anyString())).thenReturn(false);

        inventory.addProduct(product);

        boolean result = inventory.disableProduct(product);

        assertTrue(result);
        assertTrue(inventory.isProductDisabled(product));
    }

    @Test
    void isProductDisabled() {
        when(tracker.productExists(anyString())).thenReturn(false);

        inventory.addProduct(product);
        inventory.disableProduct(product);

        boolean result = inventory.isProductDisabled(product);

        assertTrue(result);
    }

    @Test
    void getAllAvailableProducts() {
        when(tracker.productExists(anyString())).thenReturn(false);

        Product product2 = new Product("2", "Pants", 70, "Pants", "blue", THREE_STARS);
        Product product3 = new Product("3", "Shoes", 120, "Shoes", "red", FIVE_STARS);

        inventory.addProduct(product);
        inventory.addProduct(product2);
        inventory.addProduct(product3);

        inventory.setQuantity(product, 10);
        inventory.setQuantity(product2, 0);
        inventory.setQuantity(product3, 5);

        inventory.disableProduct(product3);

        Set<Product> availableProducts = inventory.getAllAvailableProducts();

        assertEquals(1, availableProducts.size());
        assertTrue(availableProducts.contains(product));
        assertFalse(availableProducts.contains(product2));
        assertFalse(availableProducts.contains(product3));
    }
}
