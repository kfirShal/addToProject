package com.amazonas.business.stores.storePositions;

import com.amazonas.common.utils.ReadWriteLock;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AppointmentSystem {
    private final Map<String, OwnerNode> managersList; // contains all the managers of the store every moment
    private final OwnerNode ownershipTree; // handle the appointment hierarchy as a tree
    private final Map<String, OwnerNode> ownershipList; //contains all the owners of the store every moment
    private final ReadWriteLock appointmentLock;

    public AppointmentSystem(String storeFounderId) {
        this.managersList = new HashMap<>();
        this.ownershipTree = new OwnerNode(storeFounderId, null);
        this.ownershipList = new HashMap<>();
        ownershipList.put(ownershipTree.getUserID(), ownershipTree);
        this.appointmentLock = new ReadWriteLock();
    }

    /**
     * Add a new user to the administration team of the store as a manager.
     * @param appointeeOwnerUserId an exist owner of the store
     * @param appointedUserId a user who isn't part of the administration team of the store (neither owner nor manager)
     * @return true - if the operation done well, false - otherwise
     */
    public boolean addManager(String appointeeOwnerUserId, String appointedUserId) {
        try {
            appointmentLock.acquireWrite();
            OwnerNode appointeeNode = ownershipList.get(appointeeOwnerUserId);
            if (appointeeNode != null) {
                if (!managersList.containsKey(appointedUserId) && !ownershipList.containsKey(appointedUserId)) {
                    if(appointeeNode.addManager(appointedUserId)) {
                        managersList.put(appointedUserId, null);
                        return true;
                    }
                }
            }
            return false;
        }
        finally {
            appointmentLock.releaseWrite();
        }
    }

    /**
     * Remove exist manager from the administration team of the store.
     * @param appointeeOwnerUserId the original owner who appointed the manager
     * @param appointedUserId the manager ID to remove
     * @return true - if the operation done well, false - otherwise
     */
    public boolean removeManager(String appointeeOwnerUserId, String appointedUserId) {
        try {
            appointmentLock.acquireWrite();
            OwnerNode appointeeNode = ownershipList.get(appointeeOwnerUserId);
            if (appointeeNode != null) {
                if (appointeeNode.deleteManager(appointedUserId)) {
                    managersList.remove(appointedUserId);
                    return true;
                }
            }
            return false;
        }
        finally {
            appointmentLock.releaseWrite();
        }
    }

    /**
     * Add a new user to the administration team of the store as a owner.
     * @param appointeeOwnerUserId an exist owner of the store
     * @param appointedUserId a user who isn't part of the administration team of the store (neither owner nor manager)
     * @return true - if the operation done well, false - otherwise
     */
    public boolean addOwner(String appointeeOwnerUserId, String appointedUserId) {
        try {
            appointmentLock.acquireWrite();
            OwnerNode appointeeNode = ownershipList.get(appointeeOwnerUserId);
            if (appointeeNode != null) {
                if (!ownershipList.containsKey(appointedUserId) && !managersList.containsKey(appointedUserId)) {
                    OwnerNode appointedNode = appointeeNode.addOwner(appointedUserId);
                    if (appointedNode != null) {
                        ownershipList.put(appointedUserId, appointedNode);
                        return true;
                    }
                }
            }
            return false;
        }
        finally {
            appointmentLock.releaseWrite();
        }
    }

    /**
     * Remove exist owner from the administration team of the store. Along with him, all the other owners and managers appointed by him, and by his descendants, will be removed.
     * @param appointeeOwnerUserId the original owner who appointed the owner
     * @param appointedUserId the owner ID to remove
     * @return true - if the operation done well, false - otherwise
     */
    public boolean removeOwner(String appointeeOwnerUserId, String appointedUserId) {
        try {
            appointmentLock.acquireWrite();
            OwnerNode appointeeNode = ownershipList.get(appointeeOwnerUserId);
            if (appointeeNode != null) {
                OwnerNode deletedOwner = appointeeNode.deleteOwner(appointedUserId);
                if (deletedOwner != null) {
                    List<String> appointerChildren = deletedOwner.getAllChildren();
                    for (String appointerToRemove : appointerChildren) {
                        if (ownershipList.remove(appointerToRemove) == null) {
                            managersList.remove(appointerToRemove);
                        }
                    }
                    return true;
                }
            }
            return false;
        }
        finally {
            appointmentLock.releaseWrite();
        }
    }

    /**
     * The method returns details of the founder of the store.
     * @return StorePosition with the founder's userId
     */
    public StorePosition getFounder() {
        try {
            appointmentLock.acquireRead();
            return new StorePosition(ownershipTree.getUserID(), StoreRole.STORE_FOUNDER);
        }
        finally {
            appointmentLock.releaseRead();
        }
    }

    /**
     * The method returns the details of all the owners except the founder.
     * @return List of StorePositions with all owners' usernames
     */
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

    /**
     * The method return the details of all the managers of the store.
     * @return List of StorePositions with all managers' usernames
     */
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

    /**
     * The method return the details of all the store admins.
     * @return List of StorePositions with all admins' usernames and their roles
     */
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

    /**
     * get a responsibility of a user in the store, if he has some.
     * @param userID the user ID for checking
     * @return the StoreRole of the user if the user has a role in the store, otherwise returns StoreRole.NONE
     */
    public StoreRole getRoleOfUser(String userID) {
        try {
            appointmentLock.acquireRead();
            if (userID.equals(ownershipTree.getUserID())) {
                return StoreRole.STORE_FOUNDER;
            }
            for (String ownerId : ownershipList.keySet()) {
                if (ownerId.equals(userID)) {
                    return StoreRole.STORE_OWNER;
                }
            }
            for (String managerId : managersList.keySet()) {
                if (managerId.equals(userID)) {
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
