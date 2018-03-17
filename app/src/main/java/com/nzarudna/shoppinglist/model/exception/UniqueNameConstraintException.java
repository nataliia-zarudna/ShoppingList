package com.nzarudna.shoppinglist.model.exception;

import java.util.UUID;

/**
 * Created by nsirobaba on 3/14/18.
 */

public class UniqueNameConstraintException extends Exception {

    private UUID mDuplicateEntityID;

    public UniqueNameConstraintException(String message) {
        super(message);
    }

    public UniqueNameConstraintException(String message, Exception cause) {
        super(message, cause);
    }

    public void setDuplicateEntityID(UUID duplicateEntityID) {
        this.mDuplicateEntityID = duplicateEntityID;
    }

    public UUID getDuplicateEntityID() {
        return mDuplicateEntityID;
    }
}
