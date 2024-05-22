package com.amazonas.business.userProfiles;

import com.amazonas.business.inventory.Product;

import java.util.Objects;

public class RegisteredUser extends User{


    private String email;


    public RegisteredUser(String userId, String email){
        super(userId);
        this.email = email;

    }

    @Override
    public String getUserId() {
        return super.getUserId();
    }

}

