package com.nzarudna.shoppinglist.model.product;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static com.nzarudna.shoppinglist.Constants.PRODUCT_ORDER_STEP;

/**
 * Product Dao
 */
@Dao
public interface ProductDao {
    
    String QUERY_GROUPED_PRODUCTS_BY_LIST_ID = 
            "SELECT product_id AS prod_product_id, " +
            "   name AS prod_name, " +
            "   category_id AS prod_category_id, " +
            "   list_id AS prod_list_id, " +
            "   unit_id AS prod_unit_id, " +
            "   count AS prod_count, " +
            "   status AS prod_status, " +
            "   comment AS prod_comment, " +
            "   template_id AS prod_template_id, " +
            "   `order` AS prod_order, " +
            "   null AS cat_category_id, " +
            "   null AS cat_name, " +
            "   category_id AS category_id, " +
            "   'product' AS type " +
            "FROM products  " +
            "WHERE list_id = :listID " +
            "UNION  " +
            "SELECT null AS prod_product_id, " +
            "   null AS prod_name, " +
            "   null AS prod_category_id, " +
            "   null AS prod_list_id, " +
            "   null AS prod_unit_id, " +
            "   null AS prod_count, " +
            "   null AS prod_status, " +
            "   null AS prod_comment, " +
            "   null AS prod_template_id, " +
            "   null AS prod_order, " +
            "   category_id AS cat_category_id, " +
            "   name AS cat_name, " +
            "   category_id AS category_id, " +
            "   'category' AS type " +
            "FROM categories " +
            "WHERE category_id in (SELECT DISTINCT category_id " +
            "                      FROM products " +
            "                      WHERE list_id = :listID) " +
            "ORDER BY category_id, type";

    String QUERY_PRODUCTS_BY_LIST_ID =
            "SELECT product_id AS prod_product_id, " +
            "   name AS prod_name, " +
            "   category_id AS prod_category_id, " +
            "   list_id AS prod_list_id, " +
            "   unit_id AS prod_unit_id, " +
            "   count AS prod_count, " +
            "   status AS prod_status, " +
            "   comment AS prod_comment, " +
            "   template_id AS prod_template_id, " +
            "   `order` AS prod_order, " +
            "   null AS cat_category_id, " +
            "   null AS cat_name, " +
            "   category_id AS category_id, " +
            "   'product' AS type " +
            "FROM products  " +
            "WHERE list_id = :listID ";

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

    @Query(value = QUERY_PRODUCTS_BY_LIST_ID + " ORDER BY prod_name")
    DataSource.Factory<Integer, CategoryProductItem> findByListIDSortByName(int listID);

    @Query(value = QUERY_PRODUCTS_BY_LIST_ID + " ORDER BY prod_status, prod_name")
    DataSource.Factory<Integer, CategoryProductItem> findByListIDSortByStatusAndName(int listID);

    @Query(value = QUERY_PRODUCTS_BY_LIST_ID + " ORDER BY prod_order")
    DataSource.Factory<Integer, CategoryProductItem> findByListIDSortByProductOrder(int listID);

    @Query(value = QUERY_GROUPED_PRODUCTS_BY_LIST_ID + ", prod_name")
    DataSource.Factory<Integer, CategoryProductItem> findByListIDSortByNameWithCategory(int listID);

    @Query(value = QUERY_GROUPED_PRODUCTS_BY_LIST_ID + ", prod_status, prod_name")
    DataSource.Factory<Integer, CategoryProductItem> findByListIDSortByStatusAndNameWithCategory(int listID);

    @Query(value = QUERY_GROUPED_PRODUCTS_BY_LIST_ID + ", prod_order")
    DataSource.Factory<Integer, CategoryProductItem> findByListIDSortByProductOrderWithCategory(int listID);

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
            "           ORDER BY name) * " + PRODUCT_ORDER_STEP)
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
            "           ORDER BY status, name) * " + PRODUCT_ORDER_STEP)
    void updateProductOrdersByListIDSortByStatusAndName(int listID);

    @Query(value = "UPDATE products" +
            "       SET template_id = null" +
            "       WHERE template_id = :templateID")
    void clearTemplateIDs(int templateID);
}
