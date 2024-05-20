package com.amazonas.business.userProfiles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("usersController")
public class UsersControllerImpl implements UsersController {

    private Map<Integer,User> guests;
    private Map<String,User> registeredUsers;
    private int initialId;

    @Autowired
    public UsersControllerImpl(Map<Integer, User> guests,Map<String,User> registeredUsers) {
        this.guests = new HashMap<>(guests);
        this.registeredUsers = new HashMap<>(registeredUsers);
        this.initialId = 0;
    }

    @Override
    public Map<Integer, User> getUser() {
        return null;
    }

    @Override
    public void register(String email, String userName, String password) {
        // TODO: validate if user name is legal and the password.
        if(!registeredUsers.containsKey(userName)){
            User newRegisteredUser = new RegisteredUser(initialId,userName,email,password);
            registeredUsers.put(userName,newRegisteredUser);
        }
        else{
            throw new RuntimeException("This User name is already exists in the system");
        }

    }

    @Override
    public void enterAsGuest() {

        User newGuest = new Guest(initialId);
        guests.put(initialId,newGuest);
        initialId++;
    }



    @Override
    public void login(String userName, String password) {

    }

    @Override
    public void returnToGuest() {

    }

    @Override
    public void logoutAsGuest() {

    }

    @Override
    public StoreBasket getBasket() {
        return null;
    }

    @Override
    public ShoppingCart getCart() {
        return null;
    }

    @Override
    public void addProductToBasket() {

    }

    @Override
    public void RemoveProductFromBasket() {

    }

    @Override
    public void changeProductQuantity() {

    }
}
