package com.amazonas.business.userProfiles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("usersController")
public class UsersControllerImpl implements UsersController {

    private final Map<String,Guest> guests;
    private final Map<String,RegisteredUser> registeredUsers;

    private int initialId;

    public UsersControllerImpl() {
        this.guests = new HashMap<>();
        this.registeredUsers = new HashMap<>();
        this.initialId = 0;
    }

    private RegisteredUser getRegisteredUser(String userName) {
        return registeredUsers.get(userName);
    }

    private Guest getGuest(String id) {
        return guests.get(id);
    }

    @Override
    public void register(String email, String userName, String password) {
        // TODO: validate if user name is legal and the password.
        if(!registeredUsers.containsKey(userName)){
            RegisteredUser newRegisteredUser = new RegisteredUser(String.valueOf(initialId) ,userName,email,password);
            registeredUsers.put(userName,newRegisteredUser);

        }
        else{
            throw new RuntimeException("This User name is already exists in the system");
        }

    }

    @Override
    public void enterAsGuest() {


//        Guest newGuest = new Guest(initialId);
//        guests.put(initialId,newGuest);
//        initialId++;
    }



    @Override
    public void login(String userName) {
        if(!registeredUsers.containsKey(userName)){
            throw new RuntimeException("This user name is not exists in the system");
        }
        else{
            RegisteredUser user = getRegisteredUser(userName);

            if(user.getLoggedIn()){
                throw new RuntimeException("This user is already connected to the system");
            }
            user.login();
//            if(guests.containsKey(user.getUserId()))
        }
    }

    @Override
    public void logout() {

    }

    @Override
    public void logoutAsGuest() {

    }

    @Override
    public ShoppingCart getCart() {
        return null;
    }

    @Override
    public void addProductToCart() {

    }

    @Override
    public void RemoveProductFromCart() {

    }

    @Override
    public void changeProductQuantity() {

    }
}
