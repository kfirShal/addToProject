package com.amazonas.backend.repository.mongoCollections;

import com.amazonas.backend.business.authentication.UserCredentials;
import com.amazonas.backend.repository.abstracts.MongoCollection;
import org.springframework.stereotype.Repository;

@Repository("userCredentialsMongoCollection")
public interface UserCredentialsMongoCollection extends MongoCollection<UserCredentials> {
}
