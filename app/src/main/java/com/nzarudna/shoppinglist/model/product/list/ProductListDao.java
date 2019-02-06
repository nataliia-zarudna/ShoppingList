package com.nzarudna.shoppinglist.model.product.list;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nzarudna.shoppinglist.model.product.Product;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Dao
 */
@Dao
public interface ProductListDao {

    String QUERY_STATISTICS =
            "       SELECT list.*," +
                    "           SUM(CASE WHEN product.status = " + Product.ACTIVE + " THEN 1 ELSE 0 END) AS active_products_count, " +
                    "           SUM(CASE WHEN product.status = " + Product.ABSENT + " THEN 1 ELSE 0 END) AS absent_products_count, " +
                    "           SUM(CASE WHEN product.status = " + Product.BOUGHT + " THEN 1 ELSE 0 END) AS bought_products_count " +
                    "       FROM product_lists list " +
                    "           LEFT JOIN products product " +
                    "               ON product.list_id = list.list_id " +
                    "       WHERE list.status = :status " +
                    "       GROUP BY list.list_id";

    String QUERY_ONE_LIST_STATISTICS =
            "       SELECT list.*," +
                    "           SUM(CASE WHEN product.status = " + Product.ACTIVE + " THEN 1 ELSE 0 END) AS active_products_count, " +
                    "           SUM(CASE WHEN product.status = " + Product.ABSENT + " THEN 1 ELSE 0 END) AS absent_products_count, " +
                    "           SUM(CASE WHEN product.status = " + Product.BOUGHT + " THEN 1 ELSE 0 END) AS bought_products_count " +
                    "       FROM product_lists list " +
                    "           LEFT JOIN products product " +
                    "               ON product.list_id = list.list_id " +
                    "       WHERE list.list_id = :listID ";

    @Insert
    void insert(ProductList list);

    @Update
    void update(ProductList list);

    @Query(value = "UPDATE product_lists " +
            "       SET modified_at = :modifiedAt " +
            "           , modified_by = :modifiedBy" +
            "       WHERE list_id = :listID")
    void update(UUID listID, Date modifiedAt, UUID modifiedBy);

    @Delete
    void delete(ProductList productList);

    @Query(value = "DELETE FROM product_lists WHERE list_id = :productListID")
    void deleteByID(UUID productListID);

    @Query(value = "SELECT * FROM product_lists WHERE list_id = :listID")
    ProductList findByIDSync(UUID listID);

    @Query(value = "SELECT * FROM product_lists WHERE list_id = :listID")
    LiveData<ProductList> findByID(UUID listID);

    @Query(value = QUERY_STATISTICS + " ORDER BY list.name")
    DataSource.Factory<Integer, ProductListWithStatistics> findWithStatisticsByStatusSortByName(
            @ProductList.ProductListStatus int status);

    @Query(value = QUERY_STATISTICS + " ORDER BY list.created_by, list.name")
    DataSource.Factory<Integer, ProductListWithStatistics> findWithStatisticsByStatusSortByCreatedByAndName(
            @ProductList.ProductListStatus int status);

    @Query(value = QUERY_STATISTICS + " ORDER BY list.created_at DESC")
    DataSource.Factory<Integer, ProductListWithStatistics> findWithStatisticsByStatusSortByCreatedAtDesc(
            @ProductList.ProductListStatus int status);

    @Query(value = QUERY_STATISTICS + " ORDER BY list.modified_at DESC")
    DataSource.Factory<Integer, ProductListWithStatistics> findWithStatisticsByStatusSortByModifiedAtDesc(
            @ProductList.ProductListStatus int status);

    @Query(value = QUERY_STATISTICS + " ORDER BY list.assigned_id, list.name")
    DataSource.Factory<Integer, ProductListWithStatistics> findWithStatisticsByStatusSortByAssignedAndName(
            @ProductList.ProductListStatus int status);

    @Query(value = "SELECT * FROM product_lists WHERE status = :status ORDER BY name")
    DataSource.Factory<Integer, ProductList> findByStatusSortByName(
            @ProductList.ProductListStatus int status);

    @Query(value = "SELECT * FROM product_lists WHERE status = :status ORDER BY modified_at DESC")
    DataSource.Factory<Integer, ProductList> findStatusSortByModifiedAtDesc(
            @ProductList.ProductListStatus int status);

    @Query(value = "SELECT * FROM product_lists ORDER BY modified_at DESC")
    LiveData<List<ProductList>> findAllSortByModifiedAtDesc();

    @Query(value = "SELECT * FROM product_lists WHERE status = :status ORDER BY modified_at DESC")
    DataSource.Factory<Integer, ProductList> findByStatusSortByModifiedAtDesc(@ProductList.ProductListStatus int status);

    @Query(value = QUERY_ONE_LIST_STATISTICS)
    LiveData<ProductListWithStatistics> findOneListWithStatistics(UUID listID);
}
