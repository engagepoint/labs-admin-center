package com.engagepoint.university.admincentre.dao;

import com.engagepoint.university.admincentre.entity.AbstractEntity;

public interface GenericDAO<T extends AbstractEntity> {
    /** Put the newInstance object into storage */
    void create(T newInstance) throws Exception;

    /**
     * Retrieve an object that was previously persisted to the database using
     * the indicated id as primary key
     */
    T read(String id) throws Exception;

    /** Save changes made to an object. */
    void update(T transientObject) throws Exception;

    /** Remove an object from storage */
    void delete(T persistentObject) throws Exception;


}
