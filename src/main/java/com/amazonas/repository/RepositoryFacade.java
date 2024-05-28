package com.amazonas.repository;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.permissions.profiles.PermissionsProfile;
import com.amazonas.business.stores.Store;
import com.amazonas.business.transactions.Transaction;
import com.amazonas.business.userProfiles.ShoppingCart;
import com.amazonas.business.userProfiles.User;
import com.amazonas.utils.Cache;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("repositoryFacade")
public class RepositoryFacade {

        private final Cache<User> userCache;
        private final Cache<Product> productCache;
        private final Cache<ShoppingCart> shoppingCartCache;
        private final Cache<Store> storeCache;
        private final Cache<PermissionsProfile> permissionsProfileCache;
        private final Cache<Transaction> transactionCache;

        public RepositoryFacade(){
            userCache = new Cache<>();
            productCache = new Cache<>();
            shoppingCartCache = new Cache<>();
            storeCache = new Cache<>();
            permissionsProfileCache = new Cache<>();
            transactionCache = new Cache<>();
        }

        public User getUser(String userId){
            return null;
        }

        public Product getProduct(String productId){
            return null;
        }

        public ShoppingCart getShoppingCart(String cartId){
            return null;
        }

        public Store getStore(String storeId){
            return null;
        }

        public PermissionsProfile getPermissionsProfile(String profileId){
            return null;
        }

        public Transaction getTransaction(String transactionId){
            return null;
        }

        public void saveUser(User user){

        }

        public void saveAllUsers(Collection<User> users){

        }

        public void saveProduct(Product product){

        }

        public void saveShoppingCart(ShoppingCart cart){

        }

        public void saveStore(Store store){

        }

        public void savePermissionsProfile(PermissionsProfile profile){

        }

        public void saveTransaction(Transaction transaction){

        }
}
