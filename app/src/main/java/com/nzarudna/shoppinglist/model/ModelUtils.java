package com.nzarudna.shoppinglist.model;

import com.nzarudna.shoppinglist.model.exception.EmptyNameException;

/**
 * Created by Nataliia on 15.03.2018.
 */

public class ModelUtils {

    public static void validateNameIsNotEmpty(String name) throws EmptyNameException {
        if (name == null || name.trim().isEmpty()) {
            throw new EmptyNameException("Entity name cannot be null or empty");
        }
    }
}
