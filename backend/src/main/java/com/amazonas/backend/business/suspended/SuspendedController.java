package com.amazonas.backend.business.suspended;

import com.amazonas.common.dtos.Suspend;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("suspendedController")
public class SuspendedController {

    private static SuspendedController instance;

    private final Map<String, Suspend> suspendList;

    private SuspendedController(){
        this.suspendList = new HashMap<>();
    }

    public static synchronized  SuspendedController getInstance() {
        if (instance == null){
            instance = new SuspendedController();
        }
        return instance;
    }

    public Map<String, Suspend> getSuspendList() {
        return suspendList;
    }

    public void addSuspend(Suspend suspend){
        suspendList.put(suspend.getSuspendId(), suspend);
    }

    public Suspend removeSuspend(String id){
        return suspendList.remove(id);

    }

    public boolean isIDInList(String id){
        return suspendList.containsKey(id);
    }
}
