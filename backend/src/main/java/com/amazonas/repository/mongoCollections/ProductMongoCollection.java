package com.amazonas.repository.mongoCollections;

import com.amazonas.business.inventory.Product;
import com.amazonas.repository.abstracts.MongoCollection;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductMongoCollection extends MongoCollection<Product> {
}
