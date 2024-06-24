package com.amazonas.backend.repository.abstracts;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public abstract class AbstractCachingRepository<T> {

    private final Map<String, T> cache;
    private final MongoCollection<T> repo;

    //TODO: ADD CALLS TO THE REPO AFTER SAVING TO THE CACHE

    public AbstractCachingRepository(MongoCollection<T> repo) {
        this.cache = new HashMap<>();
        this.repo = repo;
    }

}
