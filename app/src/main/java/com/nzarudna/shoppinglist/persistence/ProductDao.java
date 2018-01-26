package com.nzarudna.shoppinglist.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nzarudna.shoppinglist.product.Product;

import java.util.List;

/**
 * Product Dao
 */
@Dao
public interface ProductDao {

    @Insert
    long insert(Product product);

    @Update
    void update(Product product);

    @Delete
    void delete(Product product);

    @Query(value = "SELECT * FROM products WHERE product_id = :productID")
    LiveData<Product> findByID(int productID);

    @Query(value = "SELECT * FROM products WHERE product_id = :productID")
    Product findByIDSync(int productID);

    @Query(value = "SELECT * FROM products WHERE list_id = :listID")
    List<Product> findByListIDSync(int listID);

    @Query(value = "SELECT * FROM products WHERE list_id = :listID ORDER BY name")
    DataSource.Factory<Integer, Product> findByListIDSortByName(int listID);

    @Query(value = "SELECT * FROM products WHERE list_id = :listID ORDER BY category_id, name")
    DataSource.Factory<Integer, Product> findByListIDSortByCategoryIDAndName(int listID);

    @Query(value = "SELECT * FROM products WHERE list_id = :listID ORDER BY status, name")
    DataSource.Factory<Integer, Product> findByListIDSortByStatusAndName(int listID);

    @Query(value = "SELECT * FROM products WHERE list_id = :listID ORDER BY `order`")
    DataSource.Factory<Integer, Product> findByListIDSortByProductOrder(int listID);

    @Query(value = "SELECT max(`order`) FROM products WHERE list_id = :listID")
    double getMaxProductOrderByListID(int listID);

    @Query(value = "UPDATE products " +
            "       SET `order` = (" +
            "           SELECT (SELECT count(*)" +
            "                   FROM products" +
            "                   WHERE name < product.name" +
            "                       AND list_id = :listID)" +
            "           FROM products product" +
            "           WHERE products.product_id = product.product_id" +
            "               AND list_id = :listID" +
            "           ORDER BY name) * 10")
    void updateProductOrdersByListIDSortByName(int listID);
}
