package com.nzarudna.shoppinglist.utils;

/**
 * Created by Nataliia on 09.03.2018.
 */

public class GenericFactory<T> {

    private Class<T> tClass;

    public T newInstance() throws IllegalAccessException, InstantiationException {
        return tClass.newInstance();
    }
}
