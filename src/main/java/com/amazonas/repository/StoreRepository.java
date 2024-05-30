package com.amazonas.repository;

import com.amazonas.business.stores.Store;
import com.amazonas.repository.abstracts.AbstractRepository;
import com.amazonas.repository.mongoCollections.StoreMongoCollection;
import org.springframework.stereotype.Component;

@Component
public class StoreRepository extends AbstractRepository<Store> {

    public StoreRepository(StoreMongoCollection repo) {
        super(repo);
    }

    // Add methods specific to Store here
}