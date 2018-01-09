package com.nzarudna.shoppinglist.model.dao;

/**
 * Factory for DAO classes
 */

public class DaoFactory {

    private static DaoFactory instance;

    public static synchronized DaoFactory getInstance() {
        if (instance == null) {
            instance = new DaoFactory();
        }
        return instance;
    }

    private DaoFactory() {}

}
