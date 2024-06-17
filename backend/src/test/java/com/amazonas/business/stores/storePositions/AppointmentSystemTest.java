package com.amazonas.business.stores.storePositions;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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


    /******************************************************************
     *
     * addManager
     *
     *****************************************************************/

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
        assertTrue(appointmentSystem.addOwner("25489","12345"));
        boolean result = appointmentSystem.addManager("12345", "39021");
        assertTrue(result);
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().size(), 1);
        assertEquals(appointmentSystem.getManagers().getFirst().role(), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().getFirst().userId(), "39021");
    }

    @org.junit.jupiter.api.Test
    void givenNoTwoParentsForManagerWhenAddManagerThenFail() {
        boolean result = appointmentSystem.addManager("25489", "39021");
        assertTrue(result);
        result = appointmentSystem.addManager("25489", "39021");
        assertFalse(result);
    }

    @org.junit.jupiter.api.Test
    void givenTwoOwnersTryAppointSameUserWhenAddManagerThenFail() {
        boolean result = appointmentSystem.addManager("25489", "39021");
        assertTrue(result);
        assertTrue(appointmentSystem.addOwner("25489","12345"));
        result = appointmentSystem.addManager("12345", "39021");
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
    void givenMoreThan1UsersToAppointWhenAddManagerThenSuccess() {
        assertTrue(appointmentSystem.addManager("25489", "39021"));
        assertTrue(appointmentSystem.addManager("25489", "39022"));
        assertTrue(appointmentSystem.addManager("25489", "39023"));
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getRoleOfUser("39022"), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getRoleOfUser("39023"), StoreRole.STORE_MANAGER);
        List<StorePosition> managersList = appointmentSystem.getManagers();
        assertEquals(managersList.size(), 3);
        for (StorePosition manager : managersList) {
            assertEquals(manager.role(), StoreRole.STORE_MANAGER);
            assertTrue(manager.userId().equals("39021") ||
                                manager.userId().equals("39022") ||
                                manager.userId().equals("39023"));
        }
    }

    /******************************************************************
     *
     * removeManager
     *
     *****************************************************************/

    @org.junit.jupiter.api.Test
    void givenLegalAppointeeWhenRemoveManagerThenSuccess() {
        assertTrue(appointmentSystem.addManager("25489","39021"));
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
    void givenAnotherAppointeeWhenRemoveManagerThenFail() {
        assertTrue(appointmentSystem.addManager("25489","39021"));
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().size(), 1);
        assertEquals(appointmentSystem.getManagers().getFirst().role(), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().getFirst().userId(), "39021");
        assertTrue(appointmentSystem.addOwner("25489","12345"));
        boolean result = appointmentSystem.removeManager("12345", "39021");
        assertFalse(result);
    }

    @org.junit.jupiter.api.Test
    void givenIllegalAppointeeWhenRemoveManagerThenFail() {
        assertTrue(appointmentSystem.addManager("25489","39021"));
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().size(), 1);
        assertEquals(appointmentSystem.getManagers().getFirst().role(), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().getFirst().userId(), "39021");
        boolean result = appointmentSystem.removeManager("00000", "39021");
        assertFalse(result);
    }

    @org.junit.jupiter.api.Test
    void givenIllegalAppointedWhenRemoveManagerThenFail() {
        assertTrue(appointmentSystem.addManager("25489","39021"));
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().size(), 1);
        assertEquals(appointmentSystem.getManagers().getFirst().role(), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().getFirst().userId(), "39021");
        boolean result = appointmentSystem.removeManager("25489", "00000");
        assertFalse(result);
    }

    @org.junit.jupiter.api.Test
    void givenMoreThan1UsersToRemoveWhenRemoveManagerThenSuccess() {
        // add all managers
        assertTrue(appointmentSystem.addManager("25489", "39021"));
        assertTrue(appointmentSystem.addManager("25489", "39022"));
        assertTrue(appointmentSystem.addManager("25489", "39023"));
        assertEquals(appointmentSystem.getManagers().size(), 3);

        //remove two af the managers
        assertTrue(appointmentSystem.removeManager("25489", "39022"));
        assertTrue(appointmentSystem.removeManager("25489", "39023"));
        assertEquals(appointmentSystem.getManagers().size(), 1);

        //check if only the manager who didn't remove stay
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().size(), 1);
        assertEquals(appointmentSystem.getManagers().getFirst().role(), StoreRole.STORE_MANAGER);
        assertEquals(appointmentSystem.getManagers().getFirst().userId(), "39021");

        //check if the removed managers were actually removed
        assertEquals(appointmentSystem.getRoleOfUser("39022"), StoreRole.NONE);
        assertEquals(appointmentSystem.getRoleOfUser("39023"), StoreRole.NONE);
    }

    /******************************************************************
     *
     * addOwner
     *
     *****************************************************************/

    @org.junit.jupiter.api.Test
    void givenLegalOwnerIdWhenAddOwnerThenSuccess() {
        assertTrue(appointmentSystem.addOwner("25489","12345"));
        assertEquals(appointmentSystem.getRoleOfUser("12345"), StoreRole.STORE_OWNER);
        assertEquals(appointmentSystem.getOwners().size(), 1);
        assertEquals(appointmentSystem.getOwners().getFirst().role(), StoreRole.STORE_OWNER);
        assertEquals(appointmentSystem.getOwners().getFirst().userId(), "12345");
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
    void givenTwoOwnersTryAppointSameUserWhenAddOwnerThenFail() {
        boolean result = appointmentSystem.addOwner("25489", "39021");
        assertTrue(result);
        assertTrue(appointmentSystem.addOwner("25489","12345"));
        result = appointmentSystem.addOwner("12345", "39021");
        assertFalse(result);
    }

    @org.junit.jupiter.api.Test
    void givenIllegalAppointeeWhenAddOwnerThenFail() {
        boolean result = appointmentSystem.addOwner("00000", "39021");
        assertFalse(result);
    }

    @org.junit.jupiter.api.Test
    void givenMoreThan1UsersToAppointWhenAddOwnerThenSuccess() {
        assertTrue(appointmentSystem.addOwner("25489", "39021"));
        assertTrue(appointmentSystem.addOwner("25489", "39022"));
        assertTrue(appointmentSystem.addOwner("25489", "39023"));
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_OWNER);
        assertEquals(appointmentSystem.getRoleOfUser("39022"), StoreRole.STORE_OWNER);
        assertEquals(appointmentSystem.getRoleOfUser("39023"), StoreRole.STORE_OWNER);
        List<StorePosition> ownersList = appointmentSystem.getOwners();
        assertEquals(ownersList.size(), 3);
        for (StorePosition owner : ownersList) {
            assertEquals(owner.role(), StoreRole.STORE_OWNER);
            assertTrue(owner.userId().equals("39021") ||
                    owner.userId().equals("39022") ||
                    owner.userId().equals("39023"));
        }
    }

    /******************************************************************
     *
     * removeOwner
     *
     *****************************************************************/

    @org.junit.jupiter.api.Test
    void givenLegalAppointeeWhenRemoveOwnerThenSuccess() {
        assertTrue(appointmentSystem.addOwner("25489","39021"));
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_OWNER);
        assertEquals(appointmentSystem.getOwners().size(), 1);
        assertEquals(appointmentSystem.getOwners().getFirst().role(), StoreRole.STORE_OWNER);
        assertEquals(appointmentSystem.getOwners().getFirst().userId(), "39021");
        boolean result = appointmentSystem.removeOwner("25489", "39021");
        assertTrue(result);
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.NONE);
        assertEquals(appointmentSystem.getOwners().size(), 0);
    }

    @org.junit.jupiter.api.Test
    void givenAnotherAppointeeWhenRemoveOwnerThenFail() {
        assertTrue(appointmentSystem.addOwner("25489","39021"));
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_OWNER);
        assertEquals(appointmentSystem.getOwners().size(), 1);
        assertEquals(appointmentSystem.getOwners().getFirst().role(), StoreRole.STORE_OWNER);
        assertEquals(appointmentSystem.getOwners().getFirst().userId(), "39021");
        assertTrue(appointmentSystem.addOwner("25489","12345"));
        boolean result = appointmentSystem.removeOwner("12345", "39021");
        assertFalse(result);
    }

    @org.junit.jupiter.api.Test
    void givenIllegalAppointeeWhenRemoveOwnerThenFail() {
        assertTrue(appointmentSystem.addOwner("25489","39021"));
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_OWNER);
        assertEquals(appointmentSystem.getOwners().size(), 1);
        assertEquals(appointmentSystem.getOwners().getFirst().role(), StoreRole.STORE_OWNER);
        assertEquals(appointmentSystem.getOwners().getFirst().userId(), "39021");
        boolean result = appointmentSystem.removeOwner("00000", "39021");
        assertFalse(result);
    }

    @org.junit.jupiter.api.Test
    void givenIllegalAppointedWhenRemoveOwnerThenFail() {
        assertTrue(appointmentSystem.addOwner("25489","39021"));
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_OWNER);
        assertEquals(appointmentSystem.getOwners().size(), 1);
        assertEquals(appointmentSystem.getOwners().getFirst().role(), StoreRole.STORE_OWNER);
        assertEquals(appointmentSystem.getOwners().getFirst().userId(), "39021");
        boolean result = appointmentSystem.removeOwner("25489", "00000");
        assertFalse(result);
    }

    @org.junit.jupiter.api.Test
    void givenMoreThan1UsersToRemoveWhenRemoveOwnerThenSuccess() {
        // add all owners
        assertTrue(appointmentSystem.addOwner("25489", "39021"));
        assertTrue(appointmentSystem.addOwner("25489", "39022"));
        assertTrue(appointmentSystem.addOwner("25489", "39023"));
        assertEquals(appointmentSystem.getOwners().size(), 3);

        //remove two af the owners
        assertTrue(appointmentSystem.removeOwner("25489", "39022"));
        assertTrue(appointmentSystem.removeOwner("25489", "39023"));
        assertEquals(appointmentSystem.getOwners().size(), 1);

        //check if only the owner who didn't remove stay
        assertEquals(appointmentSystem.getRoleOfUser("39021"), StoreRole.STORE_OWNER);
        assertEquals(appointmentSystem.getOwners().size(), 1);
        assertEquals(appointmentSystem.getOwners().getFirst().role(), StoreRole.STORE_OWNER);
        assertEquals(appointmentSystem.getOwners().getFirst().userId(), "39021");

        //check if the removed owners were actually removed
        assertEquals(appointmentSystem.getRoleOfUser("39022"), StoreRole.NONE);
        assertEquals(appointmentSystem.getRoleOfUser("39023"), StoreRole.NONE);
    }

    /******************************************************************
     *
     * TreeTests
     *
     *****************************************************************/

    @org.junit.jupiter.api.Test
    void removeOwnerAndItsAppointedOwners() {
        // add new owner - 12345
        assertTrue(appointmentSystem.addOwner("25489", "12345"));

        //the new owner appoints new owners
        assertTrue(appointmentSystem.addOwner("12345", "39021"));
        assertTrue(appointmentSystem.addOwner("12345", "39022"));
        assertTrue(appointmentSystem.addOwner("12345", "39023"));
        assertEquals(appointmentSystem.getOwners().size(), 4);

        //remove the appointee owner
        assertTrue(appointmentSystem.removeOwner("25489", "12345"));
        assertEquals(appointmentSystem.getOwners().size(), 0);
    }

    @org.junit.jupiter.api.Test
    void removeOwnerAndItsAppointedManagers() {
        // add new owner - 12345
        assertTrue(appointmentSystem.addOwner("25489", "12345"));

        //the new owner appoints new managers
        assertTrue(appointmentSystem.addManager("12345", "39021"));
        assertTrue(appointmentSystem.addManager("12345", "39022"));
        assertTrue(appointmentSystem.addManager("12345", "39023"));
        assertEquals(appointmentSystem.getManagers().size(), 3);

        //remove the appointee owner
        assertTrue(appointmentSystem.removeOwner("25489", "12345"));
        assertEquals(appointmentSystem.getManagers().size(), 0);
    }

    @org.junit.jupiter.api.Test
    void removeOwnerAndItsAllAppointedManagersAndOwners() {
        // add new owner - 12345
        assertTrue(appointmentSystem.addOwner("25489", "12345"));

        //the new owner appoints new owners and managers
        assertTrue(appointmentSystem.addManager("12345", "39021"));
        assertTrue(appointmentSystem.addManager("12345", "39022"));
        assertTrue(appointmentSystem.addManager("12345", "39023"));
        assertTrue(appointmentSystem.addOwner("12345", "49021"));
        assertTrue(appointmentSystem.addOwner("12345", "49022"));
        assertTrue(appointmentSystem.addOwner("12345", "49023"));
        assertEquals(appointmentSystem.getManagers().size(), 3);
        assertEquals(appointmentSystem.getOwners().size(), 4);

        //remove the appointee owner
        assertTrue(appointmentSystem.removeOwner("25489", "12345"));
        assertEquals(appointmentSystem.getManagers().size(), 0);
        assertEquals(appointmentSystem.getOwners().size(), 0);
    }

    @org.junit.jupiter.api.Test
    void removeOwnerAndItsAllAppointedForSomeLevelsInTheTree() {
        assertTrue(appointmentSystem.addOwner("25489", "1"));

        //the new owner appoints new owners and managers
        assertTrue(appointmentSystem.addManager("1", "11"));
        assertTrue(appointmentSystem.addManager("1", "12"));
        assertTrue(appointmentSystem.addManager("1", "13"));
        assertTrue(appointmentSystem.addOwner("1", "101"));
        assertTrue(appointmentSystem.addOwner("1", "102"));
        assertTrue(appointmentSystem.addManager("102", "1021"));
        assertTrue(appointmentSystem.addOwner("102", "10201"));
        assertTrue(appointmentSystem.addManager("10201", "102011"));
        assertEquals(appointmentSystem.getManagers().size(), 5);
        assertEquals(appointmentSystem.getOwners().size(), 4);

        assertTrue(appointmentSystem.removeOwner("1", "102"));
        assertEquals(appointmentSystem.getManagers().size(), 3);
        assertEquals(appointmentSystem.getOwners().size(), 2);

        assertTrue(appointmentSystem.removeOwner("25489", "1"));
        assertEquals(appointmentSystem.getManagers().size(), 0);
        assertEquals(appointmentSystem.getOwners().size(), 0);
    }

    @org.junit.jupiter.api.Test
    void addOwnerWhoAppointedByAnotherOwnerOnAnotherLevel() {
        assertTrue(appointmentSystem.addOwner("25489", "1"));

        //the new owner appoints new owners and managers
        assertTrue(appointmentSystem.addManager("1", "11"));
        assertTrue(appointmentSystem.addManager("1", "12"));
        assertTrue(appointmentSystem.addManager("1", "13"));
        assertTrue(appointmentSystem.addOwner("1", "101"));
        assertTrue(appointmentSystem.addOwner("1", "102"));
        assertTrue(appointmentSystem.addManager("102", "1021"));
        assertTrue(appointmentSystem.addOwner("102", "10201"));
        assertTrue(appointmentSystem.addManager("10201", "102011"));

        //trying to appoint exist owner
        assertFalse(appointmentSystem.addOwner("1", "10201"));
        assertFalse(appointmentSystem.addOwner("101", "10201"));
        assertFalse(appointmentSystem.addOwner("102", "10201"));
        assertFalse(appointmentSystem.addOwner("10201", "10201"));

        //trying to appoint exist manager
        assertFalse(appointmentSystem.addOwner("1", "10201"));
        assertFalse(appointmentSystem.addOwner("101", "10201"));
        assertFalse(appointmentSystem.addOwner("102", "10201"));
        assertFalse(appointmentSystem.addOwner("10201", "10201"));
    }

    @org.junit.jupiter.api.Test
    void addManagerWhoAppointedByAnotherOwnerOnAnotherLevel() {
        assertTrue(appointmentSystem.addOwner("25489", "1"));

        //the new owner appoints new owners and managers
        assertTrue(appointmentSystem.addManager("1", "11"));
        assertTrue(appointmentSystem.addManager("1", "12"));
        assertTrue(appointmentSystem.addManager("1", "13"));
        assertTrue(appointmentSystem.addOwner("1", "101"));
        assertTrue(appointmentSystem.addOwner("1", "102"));
        assertTrue(appointmentSystem.addManager("102", "1021"));
        assertTrue(appointmentSystem.addOwner("102", "10201"));
        assertTrue(appointmentSystem.addManager("10201", "102011"));

        //trying to appoint exist owner
        assertFalse(appointmentSystem.addManager("1", "10201"));
        assertFalse(appointmentSystem.addManager("101", "10201"));
        assertFalse(appointmentSystem.addManager("102", "10201"));
        assertFalse(appointmentSystem.addManager("10201", "10201"));

        //trying to appoint exist manager
        assertFalse(appointmentSystem.addManager("1", "10201"));
        assertFalse(appointmentSystem.addManager("101", "10201"));
        assertFalse(appointmentSystem.addManager("102", "10201"));
        assertFalse(appointmentSystem.addManager("10201", "10201"));
    }

    @org.junit.jupiter.api.Test
    void twoOwnersAddSameManagerAtTheSameTime() {
        assertTrue(appointmentSystem.addOwner("25489", "1"));
        assertTrue(appointmentSystem.addOwner("25489", "2"));
        final boolean[] result1 = new boolean[1];
        final boolean[] result2 = new boolean[1];
        AtomicInteger count = new AtomicInteger(0);

        Thread thread1 = new Thread() {
            public void run() {
                result1[0] = appointmentSystem.addManager("1", "11");
                count.incrementAndGet();
                System.out.println(result1[0]);
            }
        };

        Thread thread2 = new Thread() {
            public void run() {
                result2[0] = appointmentSystem.addManager("2", "11");
                count.incrementAndGet();
            }
        };

        thread1.start();
        thread2.start();

        while (count.get() != 2);
        boolean result = (result1[0] && !result2[0]) || (!result1[0] && result2[0]); // xor implementation
        assertTrue(result);
    }

    @org.junit.jupiter.api.Test
    void tenOwnersAddSameManagerAtTheSameTime() {
        final boolean[] results = new boolean[10];
        final Thread[] threads = new Thread[10];
        AtomicInteger count = new AtomicInteger(0);

        for (int i = 0; i < 10; i++) {
            assertTrue(appointmentSystem.addOwner("25489", String.valueOf(i)));
            int finalI = i;
            threads[i] = new Thread() {
                public void run() {
                    results[finalI] = appointmentSystem.addManager(String.valueOf(finalI), "11");
                    count.incrementAndGet();
                }
            };
        }

        for (int i = 0; i < 10; i++) {
            threads[i].start();
        }

        while (count.get() != 10);
        // bitwise xor implementation
        boolean result = false;
        for (int i = 0; i < 10; i++) {
            result = (result && !results[i]) || (!result && results[i]);
        }
        assertTrue(result);
    }

    @org.junit.jupiter.api.Test
    void twoOwnersAddSameOwnerAtTheSameTime() {
        assertTrue(appointmentSystem.addOwner("25489", "1"));
        assertTrue(appointmentSystem.addOwner("25489", "2"));
        final boolean[] result1 = new boolean[1];
        final boolean[] result2 = new boolean[1];
        AtomicInteger count = new AtomicInteger(0);

        Thread thread1 = new Thread() {
            public void run() {
                result1[0] = appointmentSystem.addOwner("1", "11");
                count.incrementAndGet();
                System.out.println(result1[0]);
            }
        };

        Thread thread2 = new Thread() {
            public void run() {
                result2[0] = appointmentSystem.addOwner("2", "11");
                count.incrementAndGet();
            }
        };

        thread1.start();
        thread2.start();

        while (count.get() != 2);
        boolean result = (result1[0] && !result2[0]) || (!result1[0] && result2[0]); // xor implementation
        assertTrue(result);
    }

    @org.junit.jupiter.api.Test
    void tenOwnersAddSameOwnerAtTheSameTime() {
        final boolean[] results = new boolean[10];
        final Thread[] threads = new Thread[10];
        AtomicInteger count = new AtomicInteger(0);

        for (int i = 0; i < 10; i++) {
            assertTrue(appointmentSystem.addOwner("25489", String.valueOf(i)));
            int finalI = i;
            threads[i] = new Thread() {
                public void run() {
                    results[finalI] = appointmentSystem.addOwner(String.valueOf(finalI), "11");
                    count.incrementAndGet();
                }
            };
        }

        for (int i = 0; i < 10; i++) {
            threads[i].start();
        }

        while (count.get() != 10);
        // bitwise xor implementation
        boolean result = false;
        for (int i = 0; i < 10; i++) {
            result = (result && !results[i]) || (!result && results[i]);
        }
        assertTrue(result);
    }

    @org.junit.jupiter.api.Test
    void addManagerByRemovedOwnerAtTheSameTime() {
        assertTrue(appointmentSystem.addOwner("25489", "1"));
        final boolean[] result1 = new boolean[1];
        final boolean[] result2 = new boolean[1];
        AtomicInteger count = new AtomicInteger(0);

        Thread thread1 = new Thread() {
            public void run() {
                result1[0] = appointmentSystem.addManager("1", "11");
                count.incrementAndGet();
            }
        };

        Thread thread2 = new Thread() {
            public void run() {
                result2[0] = appointmentSystem.removeOwner("25489", "1");
                count.incrementAndGet();
            }
        };

        thread1.start();
        thread2.start();

        while (count.get() != 2);
        boolean firstAddedThenRemoved = result1[0] && result2[0];
        boolean firstRemovedThenDidntAdded = !result1[0] && result2[0];
        boolean result = appointmentSystem.getOwners().isEmpty() && appointmentSystem.getManagers().isEmpty()
                        && (firstAddedThenRemoved || firstRemovedThenDidntAdded);
        assertTrue(result);
    }

    @org.junit.jupiter.api.Test
    void addManagerByRemovedOwnerAppointeeAtTheSameTime() {
        assertTrue(appointmentSystem.addOwner("25489", "25490"));
        assertTrue(appointmentSystem.addOwner("25490", "1"));
        final boolean[] result1 = new boolean[1];
        final boolean[] result2 = new boolean[1];
        AtomicInteger count = new AtomicInteger(0);

        Thread thread1 = new Thread() {
            public void run() {
                result1[0] = appointmentSystem.addManager("1", "11");
                count.incrementAndGet();
            }
        };

        Thread thread2 = new Thread() {
            public void run() {
                result2[0] = appointmentSystem.removeOwner("25489", "25490");
                count.incrementAndGet();
            }
        };

        thread1.start();
        thread2.start();

        while (count.get() != 2);
        boolean firstAddedThenRemoved = result1[0] && result2[0];
        boolean firstRemovedThenDidntAdded = !result1[0] && result2[0];
        boolean result = appointmentSystem.getOwners().isEmpty() && appointmentSystem.getManagers().isEmpty()
                && (firstAddedThenRemoved || firstRemovedThenDidntAdded);
        assertTrue(result);
    }



}
