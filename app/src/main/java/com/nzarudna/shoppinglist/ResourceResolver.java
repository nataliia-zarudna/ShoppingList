package com.nzarudna.shoppinglist;

import javax.inject.Singleton;

/**
 * Resolves resources, e.g. strings
 */
@Singleton
public interface ResourceResolver {

    String getString(int resID);
}
