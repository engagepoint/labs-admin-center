package com.engagepoint.university.admincentre.dao;

import com.engagepoint.university.admincentre.entity.Key;


public class KeyDAO extends AbstractDAO<Key> {
    private static volatile KeyDAO instance;

    private KeyDAO() {
        super(Key.class);
    }

    public static KeyDAO getInstance() {
        if (instance == null) {
            instance = new KeyDAO();
        }
        return instance;
    }
}
