package com.amazonas.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationValuesTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    void test1() {
        assertEquals("admin", ConfigurationValues.getProperty("ADMIN_ID"));
    }

    @Test
    void test2() {
        assertEquals("admin@amazonas.com", ConfigurationValues.getProperty("ADMIN_EMAIL"));
    }

    @Test
    void test3() {
        assertEquals("GoodPass!123", ConfigurationValues.getProperty("ADMIN_PASSWORD"));
    }
}