package com.nzarudna.shoppinglist.model.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nzarudna.shoppinglist.model.ShoppingList;

import java.util.List;

/**
 * Created by nsirobaba on 12/26/17.
 */

public interface ShoppingListDao {

    @Insert
    LiveData<ShoppingList> insert(ShoppingList list);

    @Update
    LiveData<ShoppingList> update(ShoppingList list);

    @Delete
    void delete(ShoppingList shoppingList);

    @Query(value = "SELECT * FROM shopping_lists WHERE list_id = :listID")
    List<LiveData<ShoppingList>> findByID(int listID);
}
