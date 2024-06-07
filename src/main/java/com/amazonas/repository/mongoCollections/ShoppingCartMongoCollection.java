package com.amazonas.repository.mongoCollections;

import com.amazonas.business.userProfiles.ShoppingCart;
import com.amazonas.business.userProfiles.User;
import com.amazonas.repository.abstracts.MongoCollection;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartMongoCollection extends MongoCollection<ShoppingCart> {
}
