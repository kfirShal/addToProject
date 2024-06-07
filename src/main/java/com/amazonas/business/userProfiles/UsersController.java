package com.amazonas.business.userProfiles;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.inventory.Product;
import com.amazonas.business.payment.PaymentService;
import com.amazonas.business.stores.reservations.Reservation;
import com.amazonas.business.transactions.Transaction;
import com.amazonas.exceptions.PurchaseFailedException;
import com.amazonas.exceptions.ShoppingCartException;
import com.amazonas.exceptions.UserException;
import com.amazonas.repository.*;
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
    private final ShoppingCartRepository shoppingCartRepository;
    private final PaymentService paymentService;
    private final ShoppingCartFactory shoppingCartFactory;
    private final ProductRepository productRepository;
    private final AuthenticationController authenticationController;

    private final Map<String, ShoppingCart> guestCarts;
    private final Map<String,Guest> guests;
    private final Map<String,User> onlineRegisteredUsers;

    private final ReadWriteLock lock;
    private String guestInitialId;

    public UsersController(UserRepository userRepository,
                           ReservationRepository reservationRepository,
                           TransactionRepository transactionRepository,
                           ProductRepository productRepository,
                           PaymentService paymentService,
                           ShoppingCartFactory shoppingCartFactory,
                           AuthenticationController authenticationController,
                           ShoppingCartRepository shoppingCartRepository) {
        this.userRepository = userRepository;
        this.paymentService = paymentService;
        this.shoppingCartFactory = shoppingCartFactory;
        this.reservationRepository = reservationRepository;
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
        this.authenticationController = authenticationController;
        this.shoppingCartRepository = shoppingCartRepository;

        guests = new HashMap<>();
        onlineRegisteredUsers = new HashMap<>();
        guestCarts = new HashMap<>();
        lock = new ReadWriteLock();
    }

    // =============================================================================== |
    // ================================ USER MANAGEMENT ============================== |
    // =============================================================================== |


    public void register(String email, String userId, String password) throws UserException {
        try {
            lock.acquireWrite();
            if(userRepository.userIdExists(userId)){
                throw new UserException("This user name is already exists in the system");
            }

            if (!isValidPassword(password)) {
                throw new UserException("Password must contain at least one uppercase letter and one special character.");
            }

            if (!isValidEmail(email)) {
                throw new UserException("Invalid email address.");
            }

            RegisteredUser newRegisteredUser = new RegisteredUser(userId,email);
            userRepository.saveUser(newRegisteredUser);
            shoppingCartRepository.saveCart(shoppingCartFactory.get(userId));
            authenticationController.addUserCredentials(userId, password);
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
            guestCarts.put(guestInitialId,shoppingCartFactory.get(guestInitialId));
            return guestInitialId;
        }
        finally {
            lock.releaseWrite();
        }
    }

    public ShoppingCart loginToRegistered(String guestInitialId,String userId) throws UserException {

        try{
            lock.acquireWrite();
            if(! userRepository.userIdExists(userId)){
                throw new UserException("Login failed");
            }

            //we delete this user from the list of guest
            guests.remove(guestInitialId);
            authenticationController.revokeAuthentication(guestInitialId);
            ShoppingCart cartOfGuest = guestCarts.remove(guestInitialId);
            ShoppingCart cartOfUser = shoppingCartRepository.getCart(userId);
            ShoppingCart mergedShoppingCart = cartOfUser.mergeGuestCartWithRegisteredCart(cartOfGuest);
            shoppingCartRepository.saveCart(mergedShoppingCart);
            User loggedInUser = userRepository.getUser(userId);
            onlineRegisteredUsers.put(userId,loggedInUser);
            return mergedShoppingCart;
        }
        finally {
            lock.releaseWrite();
        }

    }
    public String logout(String userId) throws UserException {

        try{
            lock.acquireWrite();
            //the registeredUser return to be a guest in the system
            if(!onlineRegisteredUsers.containsKey(userId)){
                throw new UserException("User with id: " + userId + " is not online");
            }
            onlineRegisteredUsers.remove(userId);
            authenticationController.revokeAuthentication(userId);
            guestInitialId = UUID.randomUUID().toString();
            Guest guest = new Guest(guestInitialId);
            guests.put(guestInitialId,guest);
            guestCarts.put(guestInitialId,shoppingCartFactory.get(guestInitialId));
            return guestInitialId;
        }
        finally {
            lock.releaseWrite();
        }

    }

    public void logoutAsGuest(String guestInitialId) throws UserException {

        try{
            lock.acquireWrite();
            //the guest exits the system, therefore his cart removes from the system
            if(!guests.containsKey(guestInitialId)){
                throw new UserException("Guest with id: " + guestInitialId + " is not in the market");
            }
            guestCarts.remove(guestInitialId);
            guests.remove(guestInitialId);
            authenticationController.revokeAuthentication(guestInitialId);
        }
        finally {
            lock.releaseWrite();
        }
    }

    public User getUser(String userId) throws UserException {
        try{
            lock.acquireRead();
            if(userRepository.userIdExists(userId)){
                return userRepository.getUser(userId);
            }
            if(guests.containsKey(userId)){
                return guests.get(userId);
            }
            else{
                throw new UserException("The user does not exists");
            }
        }
        finally {
            lock.releaseRead();
        }
    }

    // =============================================================================== |
    // ================================ SHOPPING CART ================================ |
    // =============================================================================== |

    public void addProductToCart(String userId, String storeId, String productId, int quantity) throws UserException, ShoppingCartException {
        ShoppingCart cart = getCartWithValidation(userId);
        cart.addProduct(storeId, productId,quantity);
    }

    public void RemoveProductFromCart(String userId,String storeName,String productId) throws UserException, ShoppingCartException {
        ShoppingCart cart = getCartWithValidation(userId);
        cart.removeProduct(storeName,productId);
    }

    public void changeProductQuantity(String userId, String storeName, String productId, int quantity) throws UserException, ShoppingCartException {
        ShoppingCart cart = getCartWithValidation(userId);
        cart.changeProductQuantity(storeName, productId,quantity);
    }

    // =============================================================================== |
    // ================================ PURCHASE ===================================== |
    // =============================================================================== |

    public void startPurchase(String userId) throws PurchaseFailedException, UserException {
        ShoppingCart cart = getCartWithValidation(userId);
        Map<String, Reservation> reservations = cart.reserveCart();
        reservations.values().forEach(r -> reservationRepository.saveReservation(userId,r));
    }

    public void payForPurchase(String userId) throws PurchaseFailedException, UserException {
        ShoppingCart cart = getCartWithValidation(userId);
        List<Reservation> reservations = reservationRepository.getReservations(userId);

        // charge the user
        User user = userRepository.getUser(userId);
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

    // =============================================================================== |
    // ============================= HELPER METHODS ================================== |
    // =============================================================================== |

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

    private ShoppingCart getCartWithValidation(String userId) throws UserException {
        ShoppingCart cart = shoppingCartRepository.getCart(userId);
        if(cart == null){
            throw new UserException("Invalid userId");
        }
        return cart;
    }

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
    // ================================ GETTERS ====================================== |
    // =============================================================================== |

    public Map<String, Guest> getGuests() {
        return guests;
    }

    public Map<String, User> getOnlineRegisteredUsers() {
        return onlineRegisteredUsers;
    }

    public User getOnlineUser(String UserId){
        return onlineRegisteredUsers.get(UserId);
    }

    public Guest getGuest(String GuestInitialId) {
        return guests.get(GuestInitialId);
    }
}
