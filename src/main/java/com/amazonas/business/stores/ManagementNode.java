package com.amazonas.business.stores;

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


}
