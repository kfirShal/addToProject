package com.amazonas.repository.abstracts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractCachingRepository<T> {

    private final Map<String, T> cache;
    private final MongoCollection<T> repo;

    //TODO: ADD CALLS TO THE REPO AFTER SAVING TO THE CACHE

    public AbstractCachingRepository(MongoCollection<T> repo) {
        this.cache = new HashMap<>();
        this.repo = repo;
    }

    public void save(String id, T object) {
        cache.put(id, object);
    }

    public void saveAll(Map<String, T> objects) {
        cache.putAll(objects);
    }

    public T get(String id) {
        return cache.get(id);
    }

    public List<T> getAll() {
        return List.copyOf(cache.values());
    }
}
