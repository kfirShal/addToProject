package com.amazonas.backend.repository.mongoCollections;

import com.amazonas.backend.business.stores.Store;
import com.amazonas.backend.repository.abstracts.MongoCollection;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreMongoCollection extends MongoCollection<Store> {
}
