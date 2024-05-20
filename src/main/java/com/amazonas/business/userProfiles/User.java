package com.amazonas.business.userProfiles;

import com.amazonas.business.inventory.Product;

public abstract class User {

    private String initialId;

    public User(String initialId){
        this.initialId = initialId;

    }


    public String getUserId(){
        return initialId;
    };
}
