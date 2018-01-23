package com.nzarudna.shoppinglist.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nzarudna.shoppinglist.product.ProductListWithStatistics;
import com.nzarudna.shoppinglist.product.ProductsList;

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
    ProductsList findByIDSync(long listID);

    @Query(value = "SELECT * FROM products_lists WHERE list_id = :listID")
    LiveData<ProductsList> findByID(long listID);

    @Query(value = "SELECT list.list_id," +
            "           list.name," +
            "           count(product) " +
            "       FROM products_lists list " +
            "           LEFT JOIN products product " +
            "               ON product.list_id = list.list_id " +
            "       WHERE list.status = :status ORDER BY name")
    DataSource.Factory<Integer, ProductListWithStatistics> findWithStaticticsByStatusSortByName(
            @ProductsList.ProductListStatus int status);

    @Query(value = "SELECT * FROM products_lists WHERE status = :status ORDER BY created_by, name")
    DataSource.Factory<Integer, ProductListWithStatistics> findWithStaticticsByStatusSortByCreatedByAndName(
            @ProductsList.ProductListStatus int status);

    @Query(value = "SELECT * FROM products_lists WHERE status = :status ORDER BY created_at DESC")
    DataSource.Factory<Integer, ProductListWithStatistics> findWithStaticticsByStatusSortByCreatedAtDesc(
            @ProductsList.ProductListStatus int status);

    @Query(value = "SELECT * FROM products_lists WHERE status = :status ORDER BY modified_at DESC")
    DataSource.Factory<Integer, ProductListWithStatistics> findWithStaticticsByStatusSortByModifiedAtDesc(
            @ProductsList.ProductListStatus int status);

    @Query(value = "SELECT * FROM products_lists WHERE status = :status ORDER BY assigned_id, name")
    DataSource.Factory<Integer, ProductListWithStatistics> findWithStaticticsByStatusSortByAssignedAndName(
            @ProductsList.ProductListStatus int status);

    @Query(value = "SELECT * FROM products_lists WHERE status = :status ORDER BY modified_at DESC")
    DataSource.Factory<Integer, ProductsList> findByStatusSortByModifiedAtDesc(@ProductsList.ProductListStatus int status);

}
