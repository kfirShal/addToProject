package com.amazonas.business.userProfiles;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.payment.PaymentService;
import com.amazonas.business.stores.reservations.Reservation;
import com.amazonas.business.transactions.Transaction;
import com.amazonas.exceptions.PurchaseFailedException;
import com.amazonas.repository.ProductRepository;
import com.amazonas.repository.ReservationRepository;
import com.amazonas.repository.TransactionRepository;
import com.amazonas.repository.UserRepository;
import com.amazonas.utils.ReadWriteLock;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("usersController")
public class UsersController {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentService paymentService;
    private final ShoppingCartFactory shoppingCartFactory;
    private final ProductRepository productRepository;

    private final Map<String, ShoppingCart> carts;
    private final Map<String,Guest> guests;
    private final Map<String,RegisteredUser> registeredUsers;
    private final Map<String,User> onlineRegisteredUsers;

    private final ReadWriteLock lock;
    private String guestInitialId;

    public UsersController(UserRepository userRepository,
                           ReservationRepository reservationRepository,
                           TransactionRepository transactionRepository,
                           ProductRepository productRepository,
                           PaymentService paymentService,
                           ShoppingCartFactory shoppingCartFactory) {

        this.userRepository = userRepository;
        this.paymentService = paymentService;
        this.shoppingCartFactory = shoppingCartFactory;
        this.reservationRepository = reservationRepository;
        this.guests = new HashMap<>();
        this.registeredUsers = new HashMap<>();
        this.onlineRegisteredUsers = new HashMap<>();
        this.carts = new HashMap<>();
        lock = new ReadWriteLock();
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
    }


    public Map<String, Guest> getGuests() {
        return guests;
    }

    public Map<String, RegisteredUser> getRegisteredUsers() {
        return registeredUsers;
    }

    public Map<String, User> getOnlineRegisteredUsers() {
        return onlineRegisteredUsers;
    }

    public User getOnlineUser(String UserId){
        return onlineRegisteredUsers.get(UserId);
    }

    public RegisteredUser getRegisteredUser(String UserId) {
        return registeredUsers.get(UserId);
    }

    public Guest getGuest(String GuestInitialId) {
        return guests.get(GuestInitialId);
    }

    public void register(String email, String userName, String password) {
        try {
            lock.acquireWrite();
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
            carts.put(userName,shoppingCartFactory.get(userName));

        }
        finally {
            lock.releaseWrite();
        }

    }

    public String enterAsGuest() {
        try{
            lock.acquireWrite();
            guestInitialId = UUID.randomUUID().toString();
            Guest newGuest = new Guest(guestInitialId);
            guests.put(guestInitialId,newGuest);
            carts.put(guestInitialId,shoppingCartFactory.get(guestInitialId));
            return guestInitialId;
        }
        finally {
            lock.releaseWrite();
        }
    }

    //this is when the guest logs in to the market and turn to registeredUser
    public boolean loginToRegistered(String guestInitialId,String userName) {

        try{
            lock.acquireWrite();
            if(!registeredUsers.containsKey(userName)){
                return false;
            }
            //we delete this user from the list of guest
            guests.remove(guestInitialId);
            ShoppingCart cartOfGuest = carts.get(guestInitialId);
            carts.remove(guestInitialId);
            ShoppingCart cartOfUser = carts.get(userName);
            ShoppingCart mergedShoppingCart = cartOfUser != null ? cartOfUser.mergeGuestCartWithRegisteredCart(cartOfGuest):cartOfGuest;
            carts.put(userName,mergedShoppingCart);
            RegisteredUser loggedInUser = getRegisteredUser(userName);
            onlineRegisteredUsers.put(userName,loggedInUser);
            return true;
        }
        finally {
            lock.releaseWrite();
        }

    }

    public void logout(String userId) {

        try{
            lock.acquireWrite();
            //the registeredUser return to be a guest in the system
            if(!onlineRegisteredUsers.containsKey(userId)){
                throw new RuntimeException("Wrong id: user with id: " + userId + " is not online");
            }
            onlineRegisteredUsers.remove(userId);
            guestInitialId = UUID.randomUUID().toString();
            Guest guest =new Guest(guestInitialId);
            guests.put(guestInitialId,guest);
            carts.put(guestInitialId,shoppingCartFactory.get(guestInitialId));
        }
        finally {
            lock.releaseWrite();
        }

    }

    public void logoutAsGuest(String guestInitialId) {

        try{
            lock.acquireWrite();
            //the guest exits the system, therefore his cart removes from the system
            if(!guests.containsKey(guestInitialId)){
                throw new RuntimeException("Wrong id: guest with id: " + guestInitialId + " is not in the market");
            }
            carts.remove(guestInitialId);
            guests.remove(guestInitialId);
        }
        finally {
            lock.releaseWrite();
        }

    }

    
    public ShoppingCart getCart(String userId) {
        try{
            lock.acquireRead();
            if(!carts.containsKey(userId)){
                throw new RuntimeException("The cart does not exists");
            }
            return carts.get(userId);
        }
        finally {
            lock.releaseRead();
        }

    }

    
    public void addProductToCart(String userId, String storeName, String productId, int quantity) {

        try{
            lock.acquireWrite();
            if(quantity <= 0){
                throw  new RuntimeException("Quantity cannot be 0 or less");
            }
            if(!carts.containsKey(userId)){
                throw new RuntimeException("The cart does not exists");
            }
            carts.get(userId).addProduct(storeName, productId,quantity);
        }
        finally {
            lock.releaseWrite();
        }
    }

    
    public void RemoveProductFromCart(String userId,String storeName,String productId) {

        try{
            lock.acquireWrite();
            if(!carts.containsKey(userId)){
                throw new RuntimeException("The cart does not exists");
            }
            if(!carts.get(userId).isStoreExists(storeName)){
                throw new RuntimeException("The store does not exists in the cart");
            }
            if(!carts.get(userId).getBasket(storeName).isProductExists(productId)){
                throw new RuntimeException("The product does not exists in the store" + storeName);
            }
            carts.get(userId).removeProduct(storeName,productId);
        }
        finally {
            lock.releaseWrite();
        }

    }

    public void changeProductQuantity(String userId, String storeName, String productId, int quantity) {
        try{
            lock.acquireWrite();
            if(quantity <= 0){
                throw  new RuntimeException("Quantity cannot be 0 or less");
            }
            if(!carts.containsKey(userId)){
                throw new RuntimeException("The cart does not exists");
            }
            carts.get(userId).changeProductQuantity(storeName, productId,quantity);
        }
        finally {
            lock.releaseWrite();
        }

    }

    public User getUser(String userId) {
        try{
            lock.acquireRead();
            if(registeredUsers.containsKey(userId)){
                return registeredUsers.get(userId);
            }
            else if(guests.containsKey(userId)){
                return guests.get(userId);
            }
            else{
                throw new RuntimeException("The user does not exists");
            }
            //return repository.getUser(userId);

        }
        finally {
            lock.releaseRead();

        }
    }

    //This method checks if the password contains at least one uppercase letter and one special character.
    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).+$";
        return password.matches(passwordPattern);
    }
    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        Matcher matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

    // =============================================================================== |
    // ================================ PURCHASE ===================================== |
    // =============================================================================== |

    public void startPurchase(String userId) throws PurchaseFailedException {
        Map<String, Reservation> reservations = carts.get(userId).reserveCart();
        reservations.values().forEach(r -> reservationRepository.saveReservation(userId,r));
    }

    public void payForPurchase(String userId) throws PurchaseFailedException {
        User user = userRepository.getUser(userId);
        ShoppingCart cart = carts.get(userId);
        List<Reservation> reservations = reservationRepository.getReservations(userId);

        // charge the user
        if(! paymentService.charge(user.getPaymentMethod(), cart.getTotalPrice())){
            cancelPurchase(userId);
            throw new PurchaseFailedException("Payment failed");
        }

        // mark the reservations as paid
        reservations.forEach(Reservation::setPaid);

        // document the transactions
        LocalDateTime transactionTime = LocalDateTime.now();
        for (var reservation : reservations) {
            Transaction t = reservationToTransaction(userId, reservation, transactionTime);
            transactionRepository.addNewTransaction(t);
        }
    }

    public void cancelPurchase(String userId) {
        List<Reservation> reservations = reservationRepository.getReservations(userId);
        reservations.forEach(r -> {
            r.cancelReservation();
            reservationRepository.removeReservation(userId,r);
        });

    }

    private Transaction reservationToTransaction(String userId, Reservation reservation, LocalDateTime transactionTime) {
        String transactionId = UUID.randomUUID().toString();
        Map<Product,Integer> productToQuantity = new HashMap<>();
        reservation.productIdToQuantity().forEach((productId, quantity) -> {
            Product product = productRepository.getProduct(productId);
            productToQuantity.put(product, quantity);
        });
        return new Transaction(transactionId,
                reservation.storeId(),
                userId,
                transactionTime,
                productToQuantity);
    }
}
