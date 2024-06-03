package com.amazonas.business.stores.storePositions;

import com.amazonas.utils.ReadWriteLock;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AppointmentSystem {
    private final Map<String, OwnerNode> managersList;
    private final OwnerNode ownershipTree;
    private final Map<String, OwnerNode> ownershipList;
    private final ReadWriteLock appointmentLock;

    public AppointmentSystem(String storeFounderId) {
        this.managersList = new HashMap<>();
        this.ownershipTree = new OwnerNode(storeFounderId, null);
        this.ownershipList = new HashMap<>();
        ownershipList.put(ownershipTree.getUserID(), ownershipTree);
        this.appointmentLock = new ReadWriteLock();
    }

    public void addManager(String appointeeOwnerUserId, String appointedUserId) {
        try {
            appointmentLock.acquireWrite();
            OwnerNode appointeeNode = ownershipList.get(appointeeOwnerUserId);
            if (appointeeNode != null) {
                if (!managersList.containsKey(appointedUserId) && !ownershipList.containsKey(appointedUserId)) {
                    appointeeNode.addManager(appointedUserId);
                    managersList.put(appointedUserId, null);
                }
            }
        }
        finally {
            appointmentLock.releaseWrite();
        }
    }

    public void removeManager(String appointeeOwnerUserId, String appointedUserId) {
        try {
            appointmentLock.acquireWrite();
            OwnerNode appointeeNode = ownershipList.get(appointeeOwnerUserId);
            if (appointeeNode != null) {
                if (appointeeNode.deleteManager(appointedUserId)) {
                    managersList.remove(appointedUserId);
                }
            }
        }
        finally {
            appointmentLock.releaseWrite();
        }
    }

    public void addOwner(String appointeeOwnerUserId, String appointedUserId) {
        try {
            appointmentLock.acquireWrite();
            OwnerNode appointeeNode = ownershipList.get(appointeeOwnerUserId);
            if (appointeeNode != null) {
                if (!ownershipList.containsKey(appointedUserId) && !managersList.containsKey(appointedUserId)) {
                    OwnerNode appointedNode = appointeeNode.addOwner(appointedUserId);
                    ownershipList.put(appointeeOwnerUserId, appointedNode);
                }
            }
        }
        finally {
            appointmentLock.releaseWrite();
        }
    }

    public void removeOwner(String appointeeOwnerUserId, String appointedUserId) {
        try {
            appointmentLock.acquireWrite();
            OwnerNode appointeeNode = ownershipList.get(appointeeOwnerUserId);
            if (appointeeNode != null) {
                OwnerNode deletedOwner = appointeeNode.deleteOwner(appointedUserId);
                if (deletedOwner != null) {
                    List<String> appointerChildren = deletedOwner.getAllChildren();
                    for (String appointerToRemove : appointerChildren) {
                        ownershipList.remove(appointerToRemove);
                    }
                }
            }
        }
        finally {
            appointmentLock.releaseWrite();
        }
    }

    public StorePosition getFounder() {
        try {
            appointmentLock.acquireRead();
            return new StorePosition(ownershipTree.getUserID(), StoreRole.STORE_FOUNDER);
        }
        finally {
            appointmentLock.releaseRead();
        }
    }

    public List<StorePosition> getOwners() {
        try {
            appointmentLock.acquireRead();
            LinkedList<StorePosition> ret = new LinkedList<>();
            for (String userId : ownershipList.keySet()) {
                if (!userId.equals(ownershipTree.getUserID())) { //except the founder
                    ret.add(new StorePosition(userId, StoreRole.STORE_OWNER));
                }
            }
            return ret;
        }
        finally {
            appointmentLock.releaseRead();
        }
    }

    public List<StorePosition> getManagers() {
        try {
            appointmentLock.acquireRead();
            LinkedList<StorePosition> ret = new LinkedList<>();
            for (String userId : managersList.keySet()) {
                ret.add(new StorePosition(userId, StoreRole.STORE_MANAGER));
            }
            return ret;
        }
        finally {
            appointmentLock.releaseRead();
        }
    }

    public List<StorePosition> getAllRoles() {
        try {
            appointmentLock.acquireRead();
            LinkedList<StorePosition> ret = new LinkedList<>();
            // founder
            ret.add(new StorePosition(ownershipTree.getUserID(), StoreRole.STORE_FOUNDER));
            // owners
            for (String userId : ownershipList.keySet()) {
                if (!userId.equals(ownershipTree.getUserID())) { //except the founder
                    ret.add(new StorePosition(userId, StoreRole.STORE_OWNER));
                }
            }
            // managers
            for (String userId : managersList.keySet()) {
                ret.add(new StorePosition(userId, StoreRole.STORE_MANAGER));
            }

            return ret;
        }
        finally {
            appointmentLock.releaseRead();
        }
    }

    public StoreRole getRoleOfUser(String userID) {
        try {
            appointmentLock.acquireRead();
            if (userID.equals(ownershipTree.getUserID())) {
                return StoreRole.STORE_FOUNDER;
            }
            for (String ownerId : ownershipList.keySet()) {
                if (ownerId.equals(userID)) { //except the founder
                    return StoreRole.STORE_OWNER;
                }
            }
            for (String managerId : managersList.keySet()) {
                if (managerId.equals(userID)) { //except the founder
                    return StoreRole.STORE_MANAGER;
                }
            }
            return StoreRole.NONE;
        }
        finally {
            appointmentLock.releaseRead();
        }
    }
}
