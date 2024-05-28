package com.amazonas.business.stores;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ManagementNode {
    private List<ManagementNode> managersChildren;
    private final ManagementNode parent;
    private final String userID;

    public ManagementNode(String userID, ManagementNode appointee) {
        this.userID = userID;
        this.parent = appointee;
        managersChildren = new LinkedList<>();
    }

    public String getUserID() {
        return userID;
    }

    public ManagementNode addManager(String userID) {
        ManagementNode ret = new ManagementNode(userID, this);
        managersChildren.add(ret);
        return ret;
    }

    public ManagementNode deleteManager(String userID) {
        Iterator<ManagementNode> iter = managersChildren.iterator();
        while (iter.hasNext()) {
            ManagementNode manager = iter.next(); // must be called before you can call i.remove()
            if (manager.userID.equals(userID)) {
                iter.remove();
                return manager;
            }
        }
        return null;
    }

    public List<String> getAllChildren() {
        List<String> ret = new LinkedList<>();
        ret.add(getUserID());
        for(ManagementNode managementChild : managersChildren) {
            ret.addAll(managementChild.getAllChildren());
        }
        return ret;
    }
}
