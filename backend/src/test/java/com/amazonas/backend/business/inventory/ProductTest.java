package com.amazonas.backend.business.inventory;

import com.amazonas.common.dtos.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.amazonas.common.utils.Rating.FOUR_STARS;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductTest {
    private Product pTest;

    @BeforeEach
    void setUp() {
        pTest = new Product("1", "Shirt", 50.0, "Shirts", "black" ,FOUR_STARS, "store1");
    }

    @Test
    void addGetKeyWords() {
        pTest.addKeyWords("shi");
        assertTrue(pTest.getKeyWords().contains("shi"));
    }

    @Test
    void removeGetKeyWords() {
        pTest.addKeyWords("shi");
        pTest.removeKeyWords("shi");
        assertFalse(pTest.getKeyWords().contains("shi"));
    }
}