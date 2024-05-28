package com.amazonas.business.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlobalProductTrackerTest {

    private GlobalProductTracker tracker;

    @BeforeEach
    void setUp() {
        tracker = new GlobalProductTracker();
    }

    @Test
    void productExists() {
        assertFalse(tracker.productExists("1"));

        // Add a product and check if it exists
        tracker.addProduct("1", "StoreA");
        assertTrue(tracker.productExists("1"));

    }

    @Test
    void addProduct() {
        // Add a product and verify it was added
        tracker.addProduct("1", "StoreA");
        assertTrue(tracker.productExists("1"));

    }
}