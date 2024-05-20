package com.amazonas.business.userProfiles;

import com.amazonas.business.inventory.Product;

import java.util.Objects;

public class RegisteredUser extends User{

    private String userName;

    private String email;


    public RegisteredUser(String id, String userName, String email){
        super(id);
        this.userName = userName;
        this.email = email;



    }

    @Override
    public String getUserId() {
        return super.getUserId();
    }

}

