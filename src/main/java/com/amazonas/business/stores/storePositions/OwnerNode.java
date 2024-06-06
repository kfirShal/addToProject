package com.amazonas.business.stores.storePositions;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class OwnerNode {
    private List<OwnerNode> ownersChildren;
    private List<String> managersChildren;
    private final OwnerNode parent;
    private final String userID;

    public OwnerNode(String userID, OwnerNode appointee) {
        this.userID = userID;
        this.parent = appointee;
        managersChildren = new LinkedList<>();
        ownersChildren = new LinkedList<>();
    }

    public String getUserID() {
        return userID;
    }

    public OwnerNode addOwner(String userID) {
        OwnerNode ret = new OwnerNode(userID, this);
        ownersChildren.add(ret);
        return ret;
    }

    public void addManager(String userID) {
        managersChildren.add(userID);
    }

    public OwnerNode deleteOwner(String userID) {
        Iterator<OwnerNode> iter = ownersChildren.iterator();
        while (iter.hasNext()) {
            OwnerNode owner = iter.next(); // must be called before you can call i.remove()
            if (owner.userID.equals(userID)) {
                iter.remove();
                return owner;
            }
        }
        return null;
    }

    public boolean deleteManager(String userID) {
        Iterator<String> iter = managersChildren.iterator();
        while (iter.hasNext()) {
            String manager = iter.next(); // must be called before you can call i.remove()
            if (manager.equals(userID)) {
                iter.remove();
                return true;
            }
        }
        return false;
    }

    public List<String> getAllChildren() {
        List<String> ret = new LinkedList<>();
        ret.add(getUserID());
        for(OwnerNode ownershipChild : ownersChildren) {
            ret.addAll(ownershipChild.getAllChildren());
        }
        return ret;
    }
}
