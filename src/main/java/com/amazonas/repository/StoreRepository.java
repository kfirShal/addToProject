package com.amazonas.repository;

import com.amazonas.business.stores.Store;
import com.amazonas.repository.abstracts.AbstractCachingRepository;
import com.amazonas.repository.mongoCollections.StoreMongoCollection;
import org.springframework.stereotype.Component;

@Component
public class StoreRepository extends AbstractCachingRepository<Store> {

    public StoreRepository(StoreMongoCollection repo) {
        super(repo);
    }

}