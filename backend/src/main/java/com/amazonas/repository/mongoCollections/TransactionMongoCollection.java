package com.amazonas.repository.mongoCollections;

import com.amazonas.business.transactions.Transaction;
import com.amazonas.repository.abstracts.MongoCollection;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionMongoCollection extends MongoCollection<Transaction> {
}
