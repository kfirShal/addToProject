package com.amazonas.backend.business;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BusinessSearchTest {

    @Test
    public void testValidSearch() {
        List<String> results = search("validCategory", "validLocation");
        assertFalse(results.isEmpty());
    }

    @Test
    public void testNoBusinessesFound() {
        List<String> results = search("validCategory", "noBusinessesLocation");
        assertTrue(results.isEmpty());
    }

    @Test
    public void testInvalidCategory() {
        List<String> results = search("invalidCategory", "validLocation");
        assertNull(results);
    }

    @Test
    public void testInvalidLocation() {
        List<String> results = search("validCategory", "invalidLocation");
        assertNull(results);
    }

    // Dummy search method for illustration purposes
    private List<String> search(String category, String location) {
        // Implement actual search logic
        if ("validCategory".equals(category) && "validLocation".equals(location)) {
            return Arrays.asList("Business1", "Business2");
        } else if ("validCategory".equals(category) && "noBusinessesLocation".equals(location)) {
            return Collections.emptyList();
        } else {
            return null;
        }
    }
}
