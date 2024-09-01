package com.amazonas.backend.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("suspendedService")
public class SuspendedService {
    private static List<String> suspendList = null;

    public SuspendedService() {
        if(suspendList == null){
            suspendList = new ArrayList<>();
        }

    }

    public static List<String> getSuspendList() {
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
