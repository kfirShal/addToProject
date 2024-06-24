package com.amazonas.backend.repository.mongoCollections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TempConfig {


    @Bean
    public UserCredentialsMongoCollection userCredentialsMongoCollection() {
        return new UserCredentialsMongoCollection(){};
    }

    @Bean
    public PermissionProfileMongoCollection permissionProfileMongoCollection() {
        return new PermissionProfileMongoCollection(){};
    }

    @Bean
    public TransactionMongoCollection transactionMongoCollection() {
        return new TransactionMongoCollection(){};
    }

    @Bean
    public UserMongoCollection userMongoCollection() {
        return new UserMongoCollection(){};
    }

    @Bean
    public StoreMongoCollection storeMongoCollection() {
        return new StoreMongoCollection(){};
    }

    @Bean
    public ProductMongoCollection productMongoCollection() {
        return new ProductMongoCollection(){};
    }

    @Bean
    public ShoppingCartMongoCollection shoppingCartMongoCollection() {
        return new ShoppingCartMongoCollection(){};
    }

    @Bean
    public NotificationMongoCollection notificationMongoCollection() {
        return new NotificationMongoCollection(){};
    }

}
