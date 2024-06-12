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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("usersController")
public class UsersController {
    private static final Logger log = LoggerFactory.getLogger(UsersController.class);
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

        if(userRepository.userIdExists(userId)){
            log.debug("User with id: {} already exists in the system", userId);
            throw new UserException("This user name is already exists in the system");
        }

        if (!isValidPassword(password)) {
            log.debug("Password does not meet the requirements");
            throw new UserException("Password must contain at least one uppercase letter and one special character.");
        }

        if (!isValidEmail(email)) {
            log.debug("Invalid email address");
            throw new UserException("Invalid email address.");
        }

        RegisteredUser newRegisteredUser = new RegisteredUser(userId,email);
        userRepository.saveUser(newRegisteredUser);
        shoppingCartRepository.saveCart(shoppingCartFactory.get(userId));
        authenticationController.addUserCredentials(userId, password);
        log.debug("User with id: {} registered successfully", userId);
    }

    public String enterAsGuest() {
        try{
            guestInitialId = UUID.randomUUID().toString();
            Guest newGuest = new Guest(guestInitialId);

            lock.acquireWrite();
            guests.put(guestInitialId,newGuest);
            guestCarts.put(guestInitialId,shoppingCartFactory.get(guestInitialId));
            log.debug("Guest with id: {} entered the market", guestInitialId);
            return guestInitialId;
        }
        finally {
            lock.releaseWrite();
        }

    }

    public ShoppingCart loginToRegistered(String guestInitialId,String userId) throws UserException {

        if(! userRepository.userIdExists(userId)){
            log.debug("User with id: {} does not exist in the system", userId);
            throw new UserException("Login failed");
        }

        User loggedInUser = userRepository.getUser(userId);
        ShoppingCart cartOfGuest;
        try{
            lock.acquireWrite();
            //remove guest from system
            guests.remove(guestInitialId);
            authenticationController.revokeAuthentication(guestInitialId);
            cartOfGuest = guestCarts.remove(guestInitialId);

            //add the registered user to the online users
            onlineRegisteredUsers.put(userId,loggedInUser);
            log.debug("User with id: {} logged in successfully", userId);
        } finally {
            lock.releaseWrite();
        }

        ShoppingCart cartOfUser = shoppingCartRepository.getCart(userId);
        ShoppingCart mergedShoppingCart = cartOfUser.mergeGuestCartWithRegisteredCart(cartOfGuest);
        shoppingCartRepository.saveCart(mergedShoppingCart);
        log.debug("Guest cart merged with user cart successfully");
        return mergedShoppingCart;
    }

    public String logout(String userId) throws UserException {
        if(!onlineRegisteredUsers.containsKey(userId)){
            log.debug("User with id: {} is not online", userId);
            throw new UserException("User with id: " + userId + " is not online");
        }

        authenticationController.revokeAuthentication(userId);
        guestInitialId = UUID.randomUUID().toString();
        Guest guest = new Guest(guestInitialId);

        try{
            lock.acquireWrite();
            onlineRegisteredUsers.remove(userId);
            guests.put(guestInitialId,guest);
            guestCarts.put(guestInitialId,shoppingCartFactory.get(guestInitialId));
            log.debug("User with id: {} logged out successfully", userId);
            return guestInitialId;
        }
        finally {
            lock.releaseWrite();
        }
    }

    public void logoutAsGuest(String guestInitialId) throws UserException {
        if(!guests.containsKey(guestInitialId)){
            log.debug("Guest with id: {} is not in the market", guestInitialId);
            throw new UserException("Guest with id: " + guestInitialId + " is not in the market");
        }
        authenticationController.revokeAuthentication(guestInitialId);

        try{
            lock.acquireWrite();
            //the guest exits the system, therefore his cart removes from the system
            guestCarts.remove(guestInitialId);
            guests.remove(guestInitialId);
            log.debug("Guest with id: {} logged out successfully", guestInitialId);
        }
        finally {
            lock.releaseWrite();
        }
    }

    public User getUser(String userId) throws UserException {
        if(userRepository.userIdExists(userId)){
            return userRepository.getUser(userId);
        }
        try{
            lock.acquireRead();
            if(guests.containsKey(userId)){
                return guests.get(userId);
            }
        }
        finally {
            lock.releaseRead();
        }
        log.debug("User with id: {} does not exist", userId);
        throw new UserException("The user does not exists");
    }

    public List<Transaction> getUserTransactionHistory(String userId) throws UserException {
        if(!userRepository.userIdExists(userId)){
            log.debug("User with id: {} does not exist", userId);
            throw new UserException("Invalid userId");
        }
        return transactionRepository.getTransactionHistoryByUser(userId);
    }
    // =============================================================================== |
    // ================================ SHOPPING CART ================================ |
    // =============================================================================== |

    public void addProductToCart(String userId, String storeId, String productId, int quantity) throws UserException, ShoppingCartException {
        ShoppingCart cart = getCartWithValidation(userId);
        cart.addProduct(storeId, productId,quantity);
        log.debug("Product with id: {} added to the cart of user with id: {}", productId, userId);
    }

    public void removeProductFromCart(String userId,String storeName,String productId) throws UserException, ShoppingCartException {
        ShoppingCart cart = getCartWithValidation(userId);
        cart.removeProduct(storeName,productId);
        log.debug("Product with id: {} removed from the cart of user with id: {}", productId, userId);
    }

    public void changeProductQuantity(String userId, String storeName, String productId, int quantity) throws UserException, ShoppingCartException {
        ShoppingCart cart = getCartWithValidation(userId);
        cart.changeProductQuantity(storeName, productId,quantity);
        log.debug("Product with id: {} quantity changed in the cart of user with id: {}", productId, userId);
    }

    public ShoppingCart viewCart(String userId) throws UserException {
        return getCartWithValidation(userId);
    }
    // =============================================================================== |
    // ================================ PURCHASE ===================================== |
    // =============================================================================== |

    public void startPurchase(String userId) throws PurchaseFailedException, UserException {
        ShoppingCart cart = getCartWithValidation(userId);
        Map<String, Reservation> reservations = cart.reserveCart();
        reservations.values().forEach(r -> reservationRepository.saveReservation(userId,r));
        log.debug("Cart of user with id: {} reserved successfully", userId);
    }

    public void payForPurchase(String userId) throws PurchaseFailedException, UserException {
        ShoppingCart cart = getCartWithValidation(userId);
        List<Reservation> reservations = reservationRepository.getReservations(userId);

        // charge the user
        User user = userRepository.getUser(userId);
        if(! paymentService.charge(user.getPaymentMethod(), cart.getTotalPrice())){
            cancelPurchase(userId);
            log.debug("Payment failed");
            throw new PurchaseFailedException("Payment failed");
        }

        // mark the reservations as paid
        reservations.forEach(Reservation::setPaid);
        log.debug("Mark the reservation as paid successfully");

        // document the transactions
        LocalDateTime transactionTime = LocalDateTime.now();
        for (var reservation : reservations) {
            Transaction t = reservationToTransaction(userId, reservation, transactionTime);
            transactionRepository.addNewTransaction(t);

        }
        log.debug("Document the transactions successfully");

        // give the user a new empty cart
        shoppingCartRepository.saveCart(shoppingCartFactory.get(userId));
        log.debug("The purchase completed");
    }

    public void cancelPurchase(String userId) throws UserException {
        if(!userRepository.userIdExists(userId)){
            log.debug("Cancel Purchase - invalid userId");
            throw new UserException("Invalid userId");
        }
        List<Reservation> reservations = reservationRepository.getReservations(userId);
        reservations.forEach(r -> {
            r.cancelReservation();
            reservationRepository.removeReservation(userId,r);
        });
        getCartWithValidation(userId).cancelReservation();
        log.debug("The purchase canceled");
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
        String passwordPattern = "^(?=.*[A-Z].*)(?=.*[!@#$%^&*()\\-=\\[\\]{};':\"<>?|])(?=.*[0-9].*)(?=.*[a-z].*).{8,}$";
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
