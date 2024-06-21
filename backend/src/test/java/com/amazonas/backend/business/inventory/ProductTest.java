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

        pTest = new Product("1", "Shirt", 50, "Shirts", "black" ,FOUR_STARS);
    }



    @Test
    void addKeyWords() {
        pTest.addKeyWords("shi");
        assertTrue(pTest.keyWords().contains("shi"));
    }

    @Test
    void removeKeyWords() {
        pTest.addKeyWords("shi");
        pTest.removeKeyWords("shi");
        assertFalse(pTest.keyWords().contains("shi"));
    }


}