package com.amazonas.business.inventory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static com.amazonas.utils.Rating.FOUR_STARS;
import static org.junit.jupiter.api.Assertions.*;

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