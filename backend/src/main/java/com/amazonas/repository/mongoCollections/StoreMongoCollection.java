package com.amazonas.repository.mongoCollections;

import com.amazonas.business.stores.Store;
import com.amazonas.repository.abstracts.MongoCollection;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreMongoCollection extends MongoCollection<Store> {
}
