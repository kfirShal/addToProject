package com.amazonas.common.dtos;

public class Suspend {
    private final String suspendId;

    public Suspend(String suspendId){
        this.suspendId = suspendId;
    }

    public String getSuspendId() {
        return suspendId;
    }
}
