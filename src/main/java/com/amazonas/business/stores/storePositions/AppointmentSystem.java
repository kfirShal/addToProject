package com.amazonas.business.stores.storePositions;

import com.amazonas.utils.ReadWriteLock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentSystem {
    private Map<String, OwnerNode> managersList;
    private final OwnerNode ownershipTree;
    private Map<String, OwnerNode> ownershipList;
    private final ReadWriteLock appointmentLock;
    public AppointmentSystem(String storeFounderId) {
        this.managersList = new HashMap<>();
        this.ownershipTree = new OwnerNode(storeFounderId, null);
        this.ownershipList = new HashMap<>();
        this.appointmentLock = new ReadWriteLock();
    }

    public void addManager(String appointeeOwnerUserId, String appointedUserId) {
        try {
            appointmentLock.acquireWrite();
            if (appointedUserId != null && appointeeOwnerUserId != null) {
                OwnerNode appointeeNode = ownershipList.get(appointeeOwnerUserId);
                if (appointeeNode != null) {
                    if (!managersList.containsKey(appointedUserId)) {
                        appointeeNode.addManager(appointedUserId);
                        managersList.put(appointedUserId, null);
                    }
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
            if (appointedUserId != null && appointeeOwnerUserId != null) {
                OwnerNode appointeeNode = ownershipList.get(appointeeOwnerUserId);
                if (appointeeNode != null) {
                    if (appointeeNode.deleteManager(appointedUserId)) {
                        managersList.remove(appointedUserId);
                    }
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
            if (appointedUserId != null && appointeeOwnerUserId != null) {
                OwnerNode appointeeNode = ownershipList.get(appointeeOwnerUserId);
                if (appointeeNode != null) {
                    if (!ownershipList.containsKey(appointedUserId)) {
                        OwnerNode appointedNode = appointeeNode.addOwner(appointedUserId);
                        ownershipList.put(appointeeOwnerUserId, appointedNode);
                    }
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
            if (appointedUserId != null && appointeeOwnerUserId != null) {
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
        }
        finally {
            appointmentLock.releaseWrite();
        }
    }
}
