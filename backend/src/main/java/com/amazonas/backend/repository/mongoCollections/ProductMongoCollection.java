package com.amazonas.backend.repository.mongoCollections;

import com.amazonas.common.dtos.Product;
import com.amazonas.backend.repository.abstracts.MongoCollection;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductMongoCollection extends MongoCollection<Product> {
}
