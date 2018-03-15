package com.nzarudna.shoppinglist.model.exception;

/**
 * Created by nsirobaba on 3/14/18.
 */

public class UniqueNameConstraintException extends Exception {

    public UniqueNameConstraintException(String message) {
        super(message);
    }

    public UniqueNameConstraintException(String message, Exception cause) {
        super(message, cause);
    }
}
