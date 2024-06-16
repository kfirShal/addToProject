package com.amazonas.repository.mongoCollections;

import com.amazonas.business.authentication.UserCredentials;
import com.amazonas.repository.abstracts.MongoCollection;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCredentialsMongoCollection extends MongoCollection<UserCredentials> {
}
