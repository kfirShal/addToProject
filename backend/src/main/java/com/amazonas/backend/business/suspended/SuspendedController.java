package com.amazonas.backend.business.suspended;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("suspendedController")
public class SuspendedController {
    private static List<String> suspendList = null;

    public SuspendedController() {
        if(suspendList == null){
            suspendList = new ArrayList<>();
        }

    }

    public List<String> getSuspendList() {
        return suspendList;
    }

    public void addSuspend(String id){
        suspendList.add(id);
    }

    public boolean removeSuspend(String id){
        return suspendList.remove(id);

    }

    public boolean isIDInList(String id){
        return suspendList.contains(id);
    }
}
