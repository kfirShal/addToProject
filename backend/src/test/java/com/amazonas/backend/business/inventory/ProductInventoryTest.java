package com.amazonas.backend.business.inventory;

import com.amazonas.backend.exceptions.StoreException;
import com.amazonas.backend.repository.ProductRepository;
import com.amazonas.common.dtos.Product;
import com.amazonas.common.utils.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


public class ProductInventoryTest {

    private ProductInventory inventory;

    @BeforeEach
    public void setUp() {
        inventory = new ProductInventory(mock(ProductRepository.class));
    }

    @Test
    public void testAddProduct() throws StoreException {
        Product product = new Product(null, "Product1", 100.0, "Category1", "Description1", Rating.FIVE_STARS, "store1");
        inventory.addProduct(product);

        assertTrue(inventory.nameExists("Product1"));
    }

    @Test
    public void testUpdateProduct() throws StoreException {
        Product product = new Product(null, "Product1", 100.0, "Category1", "Description1", Rating.FIVE_STARS, "store1");
        inventory.addProduct(product);

        String productId = product.getProductId();
        Product updatedProduct = new Product(productId, "UpdatedProduct", 200.0, "UpdatedCategory", "UpdatedDescription", Rating.FOUR_STARS, "store1");

        assertTrue(inventory.updateProduct(updatedProduct));
        assertEquals("UpdatedProduct", inventory.idToProduct().get(productId).getProductName());
    }

    @Test
    public void testRemoveProduct() throws StoreException {
        Product product = new Product(null, "Product1", 100.0, "Category1", "Description1", Rating.FIVE_STARS, "store1");
        String productId = inventory.addProduct(product);
        inventory.disableProduct(productId);

        assertTrue(inventory.removeProduct(productId));
        assertFalse(inventory.idToProduct().containsKey(productId));
    }

    @Test
    public void testSetAndGetQuantity() throws StoreException {
        Product product = new Product(null, "Product1", 100.0, "Category1", "Description1", Rating.FIVE_STARS, "store1");
        inventory.addProduct(product);

        String productId = product.getProductId();
        inventory.setQuantity(productId, 10);

        assertEquals(10, inventory.getQuantity(productId));
    }

    @Test
    public void testEnableAndDisableProduct() throws StoreException {
        Product product = new Product(null, "Product1", 100.0, "Category1", "Description1", Rating.FIVE_STARS, "store1");
        inventory.addProduct(product);

        String productId = product.getProductId();
        assertTrue(inventory.disableProduct(productId));
        assertTrue(inventory.isProductDisabled(productId));

        assertTrue(inventory.enableProduct(productId));
        assertFalse(inventory.isProductDisabled(productId));
    }

    @Test
    public void testGetAllAvailableProducts() throws StoreException {
        Product product1 = new Product(null, "Product1", 100.0, "Category1", "Description1", Rating.FIVE_STARS, "store1");
        Product product2 = new Product(null, "Product2", 150.0, "Category2", "Description2", Rating.FOUR_STARS, "store1");
        inventory.addProduct(product1);
        inventory.addProduct(product2);

        String productId1 = product1.getProductId();
        String productId2 = product2.getProductId();

        inventory.setQuantity(productId1, 10);
        inventory.setQuantity(productId2, 0);

        inventory.disableProduct(productId2);

        List<Product> availableProducts = inventory.getAllAvailableProducts();
        assertEquals(1, availableProducts.size());
        assertTrue(availableProducts.contains(product1));
        assertFalse(availableProducts.contains(product2));
    }
}
