package com.amazonas.repository;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.userProfiles.User;
import com.amazonas.repository.abstracts.AbstractCachingRepository;
import com.amazonas.repository.mongoCollections.ProductMongoCollection;
import com.amazonas.repository.mongoCollections.UserMongoCollection;
import com.amazonas.utils.ReadWriteLock;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class ProductRepository extends AbstractCachingRepository<Product> {

    private final Map<String, Product> productCache;
    private final ReadWriteLock productLock;

    public ProductRepository(ProductMongoCollection repo) {
        super(repo);
        productLock = new ReadWriteLock();
        productCache = new HashMap<>();

    }


    public Product getProduct(String productId) {
        productLock.acquireRead();
        try {
            return productCache.get(productId);
        } finally {
            productLock.releaseRead();
        }
    }

    public void saveProduct(Product product) {
        productLock.acquireWrite();
        try {
            productCache.put(product.productId(), product);
        } finally {
            productLock.releaseWrite();
        }
    }

    public void saveAllProducts(Collection<Product> products) {
        productLock.acquireWrite();
        try {
            products.forEach(product -> productCache.put(product.productId(), product));
        } finally {
            productLock.releaseWrite();
        }
    }
}