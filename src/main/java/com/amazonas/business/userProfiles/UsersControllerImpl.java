package com.amazonas.business.userProfiles;

import com.amazonas.business.inventory.Product;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component("usersController")
public class UsersControllerImpl implements UsersController {

    private final Map<String,User> users;
    private final Map<String, ShoppingCart> carts;
    private final Map<String,Guest> guests;
    private final Map<String,RegisteredUser> registeredUsers;
    private final Map<String,User> onlineRegisteredUsers;

    private String initialId;

    public UsersControllerImpl() {

        this.users = new HashMap<>();
        this.guests = new HashMap<>();
        this.registeredUsers = new HashMap<>();
        this.onlineRegisteredUsers = new HashMap<>();
        this.carts = new HashMap<>();

    }


    private User getUser(String initialId){
        return users.get(initialId);
    }

    private User getOnlineUser(String initialId){
        return users.get(initialId);
    }

    private RegisteredUser getRegisteredUser(String userName) {
        return registeredUsers.get(userName);
    }

    private Guest getGuest(String id) {
        return guests.get(id);
    }

    @Override
    public void register(String id,String email, String userName, String password) {

        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter and one special character.");
        }
        //Delete the guest from the list of users and add the registeredUser.
        users.remove(id);
        User newRegisteredUser = new RegisteredUser(id,userName,email);
        users.put(id,newRegisteredUser);



    }

    @Override
    public void enterAsGuest() {
        initialId = UUID.randomUUID().toString();
        Guest newGuest = new Guest(initialId);
        users.put(initialId,newGuest);
        carts.put(initialId,new ShoppingCart());

    }

    @Override
    public void loginToRegistered(String id) {
        if(!users.containsKey(id)){
            throw new RuntimeException("This user name is not exists in the system");
        }
        else{
            User loggedInUser = getUser(id);
            onlineRegisteredUsers.put(id,loggedInUser);
        }
    }

    @Override
    public void logout(String id) {
        //the registeredUser return to be a guest in the system
        if(!onlineRegisteredUsers.containsKey(id)){
            throw new RuntimeException("Wrong id: user with id: " + id + " is not online");
        }
        onlineRegisteredUsers.remove(id);
        User user = getUser(id);
        User guest =new Guest(id);
        users.replace(id,user,guest);
    }

    @Override
    public void logoutAsGuest(String id) {
        //the guest exits the system, therefore his cart removes from the system
        if(!users.containsKey(id)){
            throw new RuntimeException("Wrong id: guest with id: " + id + " is not in the market");
        }
        users.remove(id);
        carts.remove(id);

    }

    @Override
    public ShoppingCart getCart(String id) {
        if(!carts.containsKey(id)){
            throw new RuntimeException("The cart does not exists");
        }
        return carts.get(id);
    }

    @Override
    public void addProductToCart(String id, String storeName, Product product, int quantity) {
        if(quantity <= 0){
            throw  new RuntimeException("Quantity cannot be 0 or less");
        }
        if(!carts.containsKey(id)){
            throw new RuntimeException("The cart does not exists");
        }
        carts.get(id).addProduct(storeName,product,quantity);

    }

    @Override
    public void RemoveProductFromCart(String id,String storeName,String productId) {
        if(!carts.containsKey(id)){
            throw new RuntimeException("The cart does not exists");
        }
        carts.get(id).removeProduct(storeName,productId);


    }

    @Override
    public void changeProductQuantity(String id, String storeName, String productId, int quantity) {
        if(quantity <= 0){
            throw  new RuntimeException("Quantity cannot be 0 or less");
        }
        if(!carts.containsKey(id)){
            throw new RuntimeException("The cart does not exists");
        }
        carts.get(id).changeProductQuantity(storeName, productId,quantity);


    }

    //This method checks if the password contains at least one uppercase letter and one special character.
    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).+$";
        return password.matches(passwordPattern);
    }
}
