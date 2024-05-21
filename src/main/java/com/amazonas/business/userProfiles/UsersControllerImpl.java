package com.amazonas.business.userProfiles;

import com.amazonas.business.inventory.Product;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component("usersController")
public class UsersControllerImpl implements UsersController {

    private final Map<String, ShoppingCart> carts;
    private final Map<String,Guest> guests;
    private final Map<String,RegisteredUser> registeredUsers;
    private final Map<String,User> onlineRegisteredUsers;

    private String guestInitialId;

    public UsersControllerImpl() {

        this.guests = new HashMap<>();
        this.registeredUsers = new HashMap<>();
        this.onlineRegisteredUsers = new HashMap<>();
        this.carts = new HashMap<>();

    }

    private User getOnlineUser(String UserId){
        return onlineRegisteredUsers.get(UserId);
    }

    private RegisteredUser getRegisteredUser(String UserId) {
        return registeredUsers.get(UserId);
    }

    private Guest getGuest(String GuestInitialId) {
        return guests.get(GuestInitialId);
    }

    @Override
    public void register(String email, String userName, String password) {
       if(registeredUsers.containsKey(userName)){
           throw new RuntimeException("This user name is already exists in the system");
       }

       if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter and one special character.");
        }
        //Now the id of the registeredUser is the userName
        //the user is still in the guests until he logs in
        RegisteredUser newRegisteredUser = new RegisteredUser(userName,email);
        registeredUsers.put(userName,newRegisteredUser);
    }

    @Override
    public void enterAsGuest() {
        guestInitialId = UUID.randomUUID().toString();
        Guest newGuest = new Guest(guestInitialId);
        guests.put(guestInitialId,newGuest);
        carts.put(guestInitialId,new ShoppingCart());

    }

    @Override
    //this is when the guest logs in to the market and turn to registeredUser
    public void loginToRegistered(String guestInitialId,String userName) {

        if(!registeredUsers.containsKey(userName)){
            throw new RuntimeException("The user with user name: " + userName + " does not register to the system");
        }
            //we delete this user from the list of guest
            guests.remove(guestInitialId);
            ShoppingCart cartOfGuest = carts.get(guestInitialId);
            carts.remove(guestInitialId);
            ShoppingCart cartOfUser = carts.get(userName);
            ShoppingCart mergedShoppingCart = cartOfUser.mergeGuestCartWithRegisteredCart(cartOfGuest);
            carts.put(userName,mergedShoppingCart);
            RegisteredUser loggedInUser = getRegisteredUser(userName);
            onlineRegisteredUsers.put(userName,loggedInUser);
    }



    @Override
    public void logout(String userId) {
        //the registeredUser return to be a guest in the system
        if(!onlineRegisteredUsers.containsKey(userId)){
            throw new RuntimeException("Wrong id: user with id: " + userId + " is not online");
        }

        onlineRegisteredUsers.remove(userId);
        guestInitialId = UUID.randomUUID().toString();
        Guest guest =new Guest(guestInitialId);
        guests.put(guestInitialId,guest);
        carts.put(guestInitialId,new ShoppingCart());
    }

    @Override
    public void logoutAsGuest(String guestInitialId) {
        //the guest exits the system, therefore his cart removes from the system
        if(!guests.containsKey(guestInitialId)){
            throw new RuntimeException("Wrong id: guest with id: " + guestInitialId + " is not in the market");
        }
        carts.remove(guestInitialId);
        guests.remove(guestInitialId);


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
