package com.amazonas.backend.business.suspended;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("suspendedController")
public class SuspendedController {

    private static SuspendedController instance;

    private final List<String> suspendList;

    private SuspendedController(){
        this.suspendList = new ArrayList<>();
    }

    public static synchronized  SuspendedController getInstance() {
        if (instance == null){
            instance = new SuspendedController();
        }
        return instance;
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
