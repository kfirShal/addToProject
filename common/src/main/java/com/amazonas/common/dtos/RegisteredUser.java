package com.amazonas.common.dtos;

public class RegisteredUser extends User {

    private String email;


    public RegisteredUser(String userId, String email){
        super(userId);
        this.email = email;
    }

    @Override
    public String getUserId() {
        return super.getUserId();
    }

    public String getEmail() {
        return email;
    }

}
