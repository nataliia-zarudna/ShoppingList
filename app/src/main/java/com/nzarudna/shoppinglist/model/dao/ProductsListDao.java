package com.nzarudna.shoppinglist.model.dao;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nzarudna.shoppinglist.model.ProductsList;

/**
 * Dao
 */
@Dao
public interface ProductsListDao {

    @Insert
    long insert(ProductsList list);

    @Update
    void update(ProductsList list);

    @Delete
    void delete(ProductsList productList);

    @Query(value = "SELECT * FROM products_lists WHERE list_id = :listID")
    LiveData<ProductsList> findByID(long listID);

    @Query(value = "SELECT * FROM products_lists WHERE status = :status ORDER BY created_at")
    DataSource.Factory<Integer, ProductsList> findByStatusSortByCreatedAt(@ProductsList.ProductListStatus int status);

    @Query(value = "SELECT * FROM products_lists WHERE status = :status ORDER BY modified_at")
    DataSource.Factory<Integer, ProductsList> findByStatusSortByModifiedAt(@ProductsList.ProductListStatus int status);

    @Query(value = "SELECT * FROM products_lists WHERE status = :status ORDER BY created_by, name")
    DataSource.Factory<Integer, ProductsList> findByStatusSortByCreatedByAndName(@ProductsList.ProductListStatus int status);

}
