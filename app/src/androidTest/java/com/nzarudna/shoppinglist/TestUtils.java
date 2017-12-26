package com.nzarudna.shoppinglist;

import com.nzarudna.shoppinglist.model.ShoppingList;

/**
 * Created by nsirobaba on 12/26/17.
 */

public class TestUtils {

    public static ShoppingList getShoppingList() {
        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setName("Shopping List #1");
        return shoppingList;
    }
}
