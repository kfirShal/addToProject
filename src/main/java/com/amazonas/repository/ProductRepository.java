package com.amazonas.repository;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.userProfiles.User;
import com.amazonas.repository.abstracts.AbstractCachingRepository;
import com.amazonas.repository.mongoCollections.ProductMongoCollection;
import com.amazonas.repository.mongoCollections.UserMongoCollection;
import org.springframework.stereotype.Component;

@Component
public class ProductRepository extends AbstractCachingRepository<Product> {

    public ProductRepository(ProductMongoCollection repo) {
        super(repo);
    }

}