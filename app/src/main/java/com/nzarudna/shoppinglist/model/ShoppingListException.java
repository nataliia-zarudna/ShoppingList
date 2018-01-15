package com.nzarudna.shoppinglist.model;

/**
 * Exception in model logic
 */

public class ShoppingListException extends Exception {

    public ShoppingListException(String message, Exception cause) {
        super(message, cause);
    }

    public ShoppingListException(String message) {
        super(message);
    }
}
