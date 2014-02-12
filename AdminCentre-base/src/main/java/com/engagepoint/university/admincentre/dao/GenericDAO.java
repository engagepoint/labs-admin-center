package com.engagepoint.university.admincentre.dao;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import com.engagepoint.university.admincentre.entity.AbstractEntity;


public interface GenericDAO<T extends AbstractEntity> extends Serializable {
    /** Put the newInstance object into storage */
    void create(T newInstance) throws IOException;

    /**
     * Retrieve an object that was previously persisted to the database using
     * the indicated id as primary key
     */
    T read(String id) throws IOException;

    /** Save changes made to an object. */
    void update(T transientObject) throws IOException;

    /** Remove an object from storage */
    void delete(String keyId) throws IOException;

    /** Try to find an entity */
    List<T> search(String name) throws IOException;
}
