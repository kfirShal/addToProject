package com.amazonas.business.userProfiles;

public abstract class User {

    private String initialId;

    public User(String initialId){
        this.initialId = initialId;

    }


    public String getUserId(){
        return initialId;
    };
}
