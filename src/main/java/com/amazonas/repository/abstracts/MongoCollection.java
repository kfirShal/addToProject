package com.amazonas.repository.abstracts;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

                                 //TODO: Uncomment the MongoRepository
public interface MongoCollection<T>  extends MongoRepository<T,String> {

    //TODO: REMOVE THE INTERFACE METHODS

    <S extends T> S insert(S entity);

    <S extends T> List<S> insert(Iterable<S> entities);

    <S extends T> List<S> findAll(Example<S> example);

    <S extends T> List<S> findAll(Example<S> example, Sort sort);
}
