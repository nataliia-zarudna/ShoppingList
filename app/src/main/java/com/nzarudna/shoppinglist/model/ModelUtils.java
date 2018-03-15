package com.nzarudna.shoppinglist.model;

import com.nzarudna.shoppinglist.model.exception.NameIsEmptyException;

/**
 * Created by Nataliia on 15.03.2018.
 */

public class ModelUtils {

    public static void validateNameIsNotEmpty(String name) throws NameIsEmptyException {
        if (name == null || name.trim().isEmpty()) {
            throw new NameIsEmptyException("Entity name cannot be null or empty");
        }
    }
}
