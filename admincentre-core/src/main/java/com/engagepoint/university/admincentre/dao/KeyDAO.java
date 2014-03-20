package com.engagepoint.university.admincentre.dao;

import com.engagepoint.university.admincentre.entity.Key;

public class KeyDAO extends AbstractDAO<Key> {

    private static KeyDAO instance;

    private KeyDAO() {
        super(Key.class);
    }

    public static synchronized KeyDAO getInstance() {
        if (instance == null) {
            instance = new KeyDAO();
        }
        return instance;
    }
}
