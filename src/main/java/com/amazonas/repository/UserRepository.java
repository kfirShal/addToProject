package com.amazonas.repository;

import com.amazonas.business.stores.Store;
import com.amazonas.business.userProfiles.User;
import com.amazonas.repository.abstracts.AbstractCachingRepository;
import com.amazonas.repository.mongoCollections.StoreMongoCollection;
import com.amazonas.repository.mongoCollections.UserMongoCollection;
import org.springframework.stereotype.Component;

@Component
public class UserRepository extends AbstractCachingRepository<User> {

    public UserRepository(UserMongoCollection repo) {
        super(repo);
    }

}