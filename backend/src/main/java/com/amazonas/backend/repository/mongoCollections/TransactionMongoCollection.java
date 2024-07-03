package com.amazonas.backend.repository.mongoCollections;

import com.amazonas.common.dtos.Transaction;
import com.amazonas.backend.repository.abstracts.MongoCollection;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionMongoCollection extends MongoCollection<Transaction> {
}
