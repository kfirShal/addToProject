package com.amazonas.business.stores.storePositions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentSystemTest {
    AppointmentSystem appointmentSystem;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        appointmentSystem = new AppointmentSystem("25489");
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void givenLegalAppointmentSystemWhenGetFounderThenSuccess() {
        assertEquals("25489", appointmentSystem.getFounder().userId());
        assertEquals(appointmentSystem.getFounder().role(), StoreRole.STORE_FOUNDER);
    }

    @org.junit.jupiter.api.Test
    void givenLegalFounderIdWhenAddManagerThenSuccess() {
        boolean result = appointmentSystem.addManager("25489", "39021");
        assertTrue(result);
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().size(), 1);
        assertEquals(appointmentSystem.getManagers().getFirst().role(), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().getFirst().userId(), "39021");
    }

    @org.junit.jupiter.api.Test
    void givenLegalOwnerIdWhenAddManagerThenSuccess() {
        assertTrue(appointmentSystem.addOwner("25489", "12345"));
        boolean result = appointmentSystem.addManager("12345", "39021");
        assertTrue(result);
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().size(), 1);
        assertEquals(appointmentSystem.getManagers().getFirst().role(), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().getFirst().userId(), "39021");
    }

    @org.junit.jupiter.api.Test
    void givenExistManagerIdWhenAddManagerThenFail() {
        boolean result = appointmentSystem.addManager("25489", "39021");
        assertTrue(result);
        result = appointmentSystem.addManager("25489", "39021");
        assertFalse(result);
    }

    @org.junit.jupiter.api.Test
    void givenExistOwnerIdWhenAddManagerThenFail() {
        boolean result = appointmentSystem.addOwner("25489", "39021");
        assertTrue(result);
        result = appointmentSystem.addManager("25489", "39021");
        assertFalse(result);
    }

    @org.junit.jupiter.api.Test
    void givenIllegalAppointeeWhenAddManagerThenFail() {
        boolean result = appointmentSystem.addManager("00000", "39021");
        assertFalse(result);
    }

    @org.junit.jupiter.api.Test
    void givenLegalAppointeeWhenRemoveManagerThenSuccess() {
        assertTrue(appointmentSystem.addManager("25489", "39021"));
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().size(), 1);
        assertEquals(appointmentSystem.getManagers().getFirst().role(), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().getFirst().userId(), "39021");
        boolean result = appointmentSystem.removeManager("25489", "39021");
        assertTrue(result);
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.NONE);
        assertEquals(appointmentSystem.getManagers().size(), 0);
    }

    @org.junit.jupiter.api.Test
    void givenIllegalAppointeeWhenRemoveManagerThenSuccess() {
        assertTrue(appointmentSystem.addManager("25489", "39021"));
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().size(), 1);
        assertEquals(appointmentSystem.getManagers().getFirst().role(), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().getFirst().userId(), "39021");
        boolean result = appointmentSystem.removeManager("00000", "39021");
        assertFalse(result);
    }

    @org.junit.jupiter.api.Test
    void givenAnotherAppointeeWhenRemoveManagerThenSuccess() {
        assertTrue(appointmentSystem.addManager("25489", "39021"));
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().size(), 1);
        assertEquals(appointmentSystem.getManagers().getFirst().role(), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().getFirst().userId(), "39021");
        assertTrue(appointmentSystem.addOwner("25489", "12345"));
        boolean result = appointmentSystem.removeManager("12345", "39021");
        assertFalse(result);
    }

    @org.junit.jupiter.api.Test
    void givenIllegalAppointeeWhenRemoveManagerThenFail() {
        assertTrue(appointmentSystem.addManager("25489", "39021"));
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().size(), 1);
        assertEquals(appointmentSystem.getManagers().getFirst().role(), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().getFirst().userId(), "39021");
        boolean result = appointmentSystem.removeManager("00000", "39021");
        assertFalse(result);
    }

    @org.junit.jupiter.api.Test
    void givenLegalFounderIdWhenAddOwnerThenFail() {
        boolean result = appointmentSystem.addOwner("25489", "39021");
        assertTrue(result);
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_OWNER);
        assertEquals(appointmentSystem.getOwners().size(), 1);
        assertEquals(appointmentSystem.getOwners().getFirst().role(), StoreRole.STORE_OWNER);
        assertEquals(appointmentSystem.getOwners().getFirst().userId(), "39021");
    }

    @org.junit.jupiter.api.Test
    void givenIllegalAppointedWhenRemoveManagerThenFail() {
        assertTrue(appointmentSystem.addManager("25489", "39021"));
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().size(), 1);
        assertEquals(appointmentSystem.getManagers().getFirst().role(), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().getFirst().userId(), "39021");
        boolean result = appointmentSystem.removeManager("25489", "00000");
        assertFalse(result);
    }

    @org.junit.jupiter.api.Test
    void givenExistManagerIdWhenAddOwnerThenFail() {
        boolean result = appointmentSystem.addManager("25489", "39021");
        assertTrue(result);
        result = appointmentSystem.addOwner("25489", "39021");
        assertFalse(result);
    }

    @org.junit.jupiter.api.Test
    void givenExistOwnerIdWhenAddOwnerThenFail() {
        boolean result = appointmentSystem.addOwner("25489", "39021");
        assertTrue(result);
        result = appointmentSystem.addOwner("25489", "39021");
        assertFalse(result);
    }

    @org.junit.jupiter.api.Test
    void givenIllegalAppointeeWhenAddOwnerThenFail() {
        boolean result = appointmentSystem.addOwner("00000", "39021");
        assertFalse(result);
    }

    @org.junit.jupiter.api.Test
    void givenLegalAppointeeWhenRemoveOwnerThenSuccess() {
        assertTrue(appointmentSystem.addOwner("25489", "39021"));
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_OWNER);
        assertEquals(appointmentSystem.getOwners().size(), 2);
        assertEquals(appointmentSystem.getOwners().getFirst().role(), StoreRole.STORE_OWNER);
        assertEquals(appointmentSystem.getOwners().getFirst().userId(), "39021");
        boolean result = appointmentSystem.removeManager("25489", "39021");
        assertTrue(result);
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.NONE);
        assertEquals(appointmentSystem.getManagers().size(), 1);
    }

}
