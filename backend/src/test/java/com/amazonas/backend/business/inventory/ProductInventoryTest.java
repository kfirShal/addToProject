package com.amazonas.backend.business.inventory;

import com.amazonas.backend.exceptions.StoreException;
import com.amazonas.common.dtos.Product;
import com.amazonas.common.utils.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class ProductInventoryTest {

    private ProductInventory inventory;

    @BeforeEach
    public void setUp() {
        inventory = new ProductInventory();
    }

    @Test
    public void testAddProduct() throws StoreException {
        Product product = new Product(null, "Product1", 100.0, "Category1", "Description1", Rating.FIVE_STARS);
        inventory.addProduct(product);

        assertTrue(inventory.nameExists("Product1"));
    }

    @Test
    public void testUpdateProduct() throws StoreException {
        Product product = new Product(null, "Product1", 100.0, "Category1", "Description1", Rating.FIVE_STARS);
        inventory.addProduct(product);

        String productId = product.productId();
        Product updatedProduct = new Product(productId, "UpdatedProduct", 200.0, "UpdatedCategory", "UpdatedDescription", Rating.FOUR_STARS);

        assertTrue(inventory.updateProduct(updatedProduct));
        assertEquals("UpdatedProduct", inventory.idToProduct.get(productId).productName());
    }

    @Test
    public void testRemoveProduct() throws StoreException {
        Product product = new Product(null, "Product1", 100.0, "Category1", "Description1", Rating.FIVE_STARS);
        String productId = inventory.addProduct(product);
        inventory.disableProduct(productId);

        assertTrue(inventory.removeProduct(productId));
        assertFalse(inventory.idToProduct.containsKey(productId));
    }

    @Test
    public void testSetAndGetQuantity() throws StoreException {
        Product product = new Product(null, "Product1", 100.0, "Category1", "Description1", Rating.FIVE_STARS);
        inventory.addProduct(product);

        String productId = product.productId();
        inventory.setQuantity(productId, 10);

        assertEquals(10, inventory.getQuantity(productId));
    }

    @Test
    public void testEnableAndDisableProduct() throws StoreException {
        Product product = new Product(null, "Product1", 100.0, "Category1", "Description1", Rating.FIVE_STARS);
        inventory.addProduct(product);

        String productId = product.productId();
        assertTrue(inventory.disableProduct(productId));
        assertTrue(inventory.isProductDisabled(productId));

        assertTrue(inventory.enableProduct(productId));
        assertFalse(inventory.isProductDisabled(productId));
    }

    @Test
    public void testGetAllAvailableProducts() throws StoreException {
        Product product1 = new Product(null, "Product1", 100.0, "Category1", "Description1", Rating.FIVE_STARS);
        Product product2 = new Product(null, "Product2", 150.0, "Category2", "Description2", Rating.FOUR_STARS);
        inventory.addProduct(product1);
        inventory.addProduct(product2);

        String productId1 = product1.productId();
        String productId2 = product2.productId();

        inventory.setQuantity(productId1, 10);
        inventory.setQuantity(productId2, 0);

        inventory.disableProduct(productId2);

        Set<Product> availableProducts = inventory.getAllAvailableProducts();
        assertEquals(1, availableProducts.size());
        assertTrue(availableProducts.contains(product1));
        assertFalse(availableProducts.contains(product2));
    }
}
