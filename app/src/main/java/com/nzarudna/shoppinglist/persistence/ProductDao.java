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

    @Query(value = "SELECT product_id as prod_product_id, " +
            "           name as prod_name, " +
            "           category_id as prod_category_id, " +
            "           list_id as prod_list_id, " +
            "           unit_id as prod_unit_id, " +
            "           count as prod_count, " +
            "           status as prod_status, " +
            "           comment as prod_comment, " +
            "           template_id as prod_template_id, " +
            "           `order` as prod_order, " +
            "           null as cat_category_id, " +
            "           null as cat_name, " +
            "           category_id AS category_id, " +
            "           'product' AS type " +
            "       FROM products  " +
            "       WHERE list_id = :listID " +
            "       UNION  " +
            "       SELECT null as prod_product_id, " +
            "           null as prod_name, " +
            "           null as prod_category_id, " +
            "           null as prod_list_id, " +
            "           null as prod_unit_id, " +
            "           null as prod_count, " +
            "           null as prod_status, " +
            "           null as prod_comment, " +
            "           null as prod_template_id, " +
            "           null as prod_order, " +
            "           category_id as cat_category_id, " +
            "           name as cat_name, " +
            "           category_id AS category_id, " +
            "           'category' AS type " +
            "       FROM categories " +
            "       WHERE category_id in (SELECT DISTINCT category_id " +
            "                             FROM products " +
            "                             WHERE list_id = :listID) " +
            "       ORDER BY category_id, type, prod_name")
    DataSource.Factory<Integer, CategoryProductItem> findByListIDSortByNameWithCategory(int listID);

    //@Query(value = "SELECT * FROM products WHERE list_id = :listID ORDER BY category_id, name")
    //DataSource.Factory<Integer, Product> findByListIDSortByCategoryIDAndName(int listID);

    @Query(value = "SELECT * FROM products WHERE list_id = :listID ORDER BY status, name")
    DataSource.Factory<Integer, Product> findByListIDSortByStatusAndName(int listID);

    @Query(value = "SELECT * FROM products WHERE list_id = :listID ORDER BY `order`")
    DataSource.Factory<Integer, Product> findByListIDSortByProductOrder(int listID);

    @Query(value = "SELECT max(`order`) FROM products WHERE list_id = :listID")
    double getMaxProductOrderByListID(int listID);

    @Query(value = "SELECT min(`order`) FROM products WHERE list_id = :listID")
    double getMinProductOrderByListID(int listID);

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

    @Query(value = "UPDATE products " +
            "       SET `order` = (" +
            "           SELECT (SELECT count(*)" +
            "                   FROM products" +
            "                   WHERE status < product.status" +
            "                       OR (status = product.status AND name < product.name)" +
            "                       AND list_id = :listID)" +
            "           FROM products product" +
            "           WHERE products.product_id = product.product_id" +
            "               AND list_id = :listID" +
            "           ORDER BY status, name) * 10")
    void updateProductOrdersByListIDSortByStatusAndName(int listID);
}
