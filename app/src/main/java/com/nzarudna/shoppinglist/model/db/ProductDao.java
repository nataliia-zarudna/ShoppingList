package com.nzarudna.shoppinglist.model.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nzarudna.shoppinglist.model.Product;

import java.util.List;

/**
 * Created by nsirobaba on 12/26/17.
 */
@Dao
public interface ProductDao {

    @Insert
    LiveData<Product> insert(Product product);

    @Update
    LiveData<Product> update(Product product);

    @Delete
    void delete(Product product);

    @Query(value = "SELECT * FROM products WHERE product_id = :productID")
    LiveData<Product> findByID(int productID);

    @Query(value = "SELECT * FROM products WHERE list_id = :listID")
    List<LiveData<Product>> findByListID(int listID);
}
