package com.amazonas.repository;

import com.amazonas.business.authentication.UserCredentials;
import com.amazonas.business.userProfiles.User;
import com.amazonas.repository.abstracts.AbstractCachingRepository;
import com.amazonas.repository.mongoCollections.UserCredentialsMongoCollection;
import com.amazonas.repository.mongoCollections.UserMongoCollection;
import org.springframework.stereotype.Component;

@Component
public class UserCredentialsRepository extends AbstractCachingRepository<UserCredentials> {

    public UserCredentialsRepository(UserCredentialsMongoCollection repo) {
        super(repo);
    }

}