package com.amazonas.repository;

import com.amazonas.business.transactions.Transaction;
import com.amazonas.business.userProfiles.User;
import com.amazonas.repository.abstracts.AbstractCachingRepository;
import com.amazonas.repository.mongoCollections.TransactionMongoCollection;
import com.amazonas.repository.mongoCollections.UserMongoCollection;
import org.springframework.stereotype.Component;

@Component
public class TransactionRepository extends AbstractCachingRepository<Transaction> {

    public TransactionRepository(TransactionMongoCollection repo) {
        super(repo);
    }

}