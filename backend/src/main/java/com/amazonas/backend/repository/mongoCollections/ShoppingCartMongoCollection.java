package com.amazonas.backend.repository.mongoCollections;

import com.amazonas.backend.business.userProfiles.ShoppingCart;
import com.amazonas.backend.repository.abstracts.MongoCollection;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartMongoCollection extends MongoCollection<ShoppingCart> {
}
