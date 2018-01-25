package com.nzarudna.shoppinglist.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nzarudna.shoppinglist.product.Product;
import com.nzarudna.shoppinglist.product.ProductList;
import com.nzarudna.shoppinglist.product.ProductListWithStatistics;

/**
 * Dao
 */
@Dao
public interface ProductListDao {

    String STATISTICS_QUERY =
            "       SELECT list.list_id," +
            "           list.name," +
            "           COUNT(DISTINCT to_buy_product.product_id) AS to_buy_products_count, " +
            "           COUNT(DISTINCT absent_product.product_id) AS absent_products_count, " +
            "           COUNT(DISTINCT bought_product.product_id) AS bought_products_count " +
            "       FROM product_lists list " +
            "           LEFT JOIN products to_buy_product " +
            "               ON to_buy_product.list_id = list.list_id " +
            "                   AND to_buy_product.status = " + Product.TO_BUY +
            "           LEFT JOIN products absent_product " +
            "               ON absent_product.list_id = list.list_id " +
            "                   AND absent_product.status = " + Product.ABSENT +
            "           LEFT JOIN products bought_product " +
            "               ON bought_product.list_id = list.list_id " +
            "                   AND bought_product.status = " + Product.BOUGHT +
            "       WHERE list.status = :status " +
            "       GROUP BY list.list_id";

    @Insert
    long insert(ProductList list);

    @Update
    void update(ProductList list);

    @Delete
    void delete(ProductList productList);

    @Query(value = "DELETE FROM product_lists WHERE list_id = :productListID")
    void deleteByID(int productListID);

    @Query(value = "SELECT * FROM product_lists WHERE list_id = :listID")
    ProductList findByIDSync(long listID);

    @Query(value = "SELECT * FROM product_lists WHERE list_id = :listID")
    LiveData<ProductList> findByID(long listID);

    @Query(value = STATISTICS_QUERY + " ORDER BY list.name")
    DataSource.Factory<Integer, ProductListWithStatistics> findWithStaticticsByStatusSortByName(
            @ProductList.ProductListStatus int status);

    @Query(value = STATISTICS_QUERY + " ORDER BY list.created_by, list.name")
    DataSource.Factory<Integer, ProductListWithStatistics> findWithStaticticsByStatusSortByCreatedByAndName(
            @ProductList.ProductListStatus int status);

    @Query(value = STATISTICS_QUERY + " ORDER BY list.created_at DESC")
    DataSource.Factory<Integer, ProductListWithStatistics> findWithStaticticsByStatusSortByCreatedAtDesc(
            @ProductList.ProductListStatus int status);

    @Query(value = STATISTICS_QUERY + " ORDER BY list.modified_at DESC")
    DataSource.Factory<Integer, ProductListWithStatistics> findWithStaticticsByStatusSortByModifiedAtDesc(
            @ProductList.ProductListStatus int status);

    @Query(value = STATISTICS_QUERY + " ORDER BY list.assigned_id, list.name")
    DataSource.Factory<Integer, ProductListWithStatistics> findWithStaticticsByStatusSortByAssignedAndName(
            @ProductList.ProductListStatus int status);

    @Query(value = "SELECT * FROM product_lists ORDER BY modified_at DESC")
    DataSource.Factory<Integer, ProductList> findAllSortByModifiedAtDesc();

    @Query(value = "SELECT * FROM product_lists WHERE status = :status ORDER BY modified_at DESC")
    DataSource.Factory<Integer, ProductList> findByStatusSortByModifiedAtDesc(@ProductList.ProductListStatus int status);
}
