package com.amazonas.repository.mongoCollections;

import com.amazonas.business.authentication.UserCredentials;
import org.springframework.stereotype.Repository;
import com.amazonas.repository.abstracts.MongoCollection;

@Repository
public interface UserCredentialsMongoCollection extends MongoCollection<UserCredentials> {
}
