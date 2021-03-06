package com.nzarudna.shoppinglist.model.product;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nzarudna.shoppinglist.model.category.Category;

import java.util.List;
import java.util.UUID;

import static com.nzarudna.shoppinglist.Constants.PRODUCT_ORDER_STEP;

/**
 * Product Dao
 */
@Dao
public interface ProductDao {

    String QUERY_GROUPED_PRODUCTS_BY_LIST_ID = "" +
            "SELECT product_id AS prod_product_id, " +
            "   product.name AS prod_name, " +
            "   category_id AS prod_category_id, " +
            "   list_id AS prod_list_id, " +
            "   product.unit_id AS prod_unit_id, " +
            "   count AS prod_count, " +
            "   status AS prod_status, " +
            "   comment AS prod_comment, " +
            "   template_id AS prod_template_id, " +
            "   `order` AS prod_order, " +
            "   unit.unit_id AS unit_unit_id, " +
            "   unit.name AS unit_name, " +
            "   null AS cat_category_id, " +
            "   null AS cat_name, " +
            "   category_id AS category_id, " +
            "   '" + CategoryProductItem.TYPE_PRODUCT + "' AS type " +
            "FROM products product" +
            "   LEFT JOIN units unit ON product.unit_id = unit.unit_id " +
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
            "   null AS unit_unit_id, " +
            "   null AS unit_name, " +
            "   category_id AS cat_category_id, " +
            "   name AS cat_name, " +
            "   category_id AS category_id, " +
            "   '" + CategoryProductItem.TYPE_CATEGORY + "' AS type " +
            "FROM categories " +
            "WHERE category_id in (SELECT DISTINCT category_id " +
            "                      FROM products " +
            "                      WHERE list_id = :listID) " +
            "ORDER BY category_id, type";

    String QUERY_GROUPED_PRODUCTS_BY_LIST_ID_EXCLUDE_STATUS = "" +
            "SELECT product_id AS prod_product_id, " +
            "   product.name AS prod_name, " +
            "   category_id AS prod_category_id, " +
            "   list_id AS prod_list_id, " +
            "   product.unit_id AS prod_unit_id, " +
            "   count AS prod_count, " +
            "   status AS prod_status, " +
            "   comment AS prod_comment, " +
            "   template_id AS prod_template_id, " +
            "   `order` AS prod_order, " +
            "   unit.unit_id AS unit_unit_id, " +
            "   unit.name AS unit_name, " +
            "   null AS cat_category_id, " +
            "   null AS cat_name, " +
            "   category_id AS category_id, " +
            "   '" + CategoryProductItem.TYPE_PRODUCT + "' AS type " +
            "FROM products product" +
            "   LEFT JOIN units unit ON product.unit_id = unit.unit_id " +
            "WHERE list_id = :listID " +
            "   AND status <> :excludeStatus " +
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
            "   null AS unit_unit_id, " +
            "   null AS unit_name, " +
            "   category_id AS cat_category_id, " +
            "   name AS cat_name, " +
            "   category_id AS category_id, " +
            "   '" + CategoryProductItem.TYPE_CATEGORY + "' AS type " +
            "FROM categories " +
            "WHERE category_id in (SELECT DISTINCT category_id " +
            "                      FROM products " +
            "                      WHERE list_id = :listID) " +
            "ORDER BY category_id, type";

    String QUERY_PRODUCTS_BY_LIST_ID =
            "SELECT product_id AS prod_product_id, " +
                    "   product.name AS prod_name, " +
                    "   category_id AS prod_category_id, " +
                    "   list_id AS prod_list_id, " +
                    "   product.unit_id AS prod_unit_id, " +
                    "   count AS prod_count, " +
                    "   status AS prod_status, " +
                    "   comment AS prod_comment, " +
                    "   template_id AS prod_template_id, " +
                    "   `order` AS prod_order, " +
                    "   unit.unit_id AS unit_unit_id, " +
                    "   unit.name AS unit_name, " +
                    "   null AS cat_category_id, " +
                    "   null AS cat_name, " +
                    "   category_id AS category_id, " +
                    "   '" + CategoryProductItem.TYPE_PRODUCT + "' AS type " +
                    "FROM products  product" +
                    "   LEFT JOIN units unit ON product.unit_id = unit.unit_id " +
                    "WHERE list_id = :listID ";

    String QUERY_PRODUCTS_BY_LIST_ID_EXCLUDE_STATUS =
            "SELECT product_id AS prod_product_id, " +
                    "   product.name AS prod_name, " +
                    "   category_id AS prod_category_id, " +
                    "   list_id AS prod_list_id, " +
                    "   product.unit_id AS prod_unit_id, " +
                    "   count AS prod_count, " +
                    "   status AS prod_status, " +
                    "   comment AS prod_comment, " +
                    "   template_id AS prod_template_id, " +
                    "   `order` AS prod_order, " +
                    "   unit.unit_id AS unit_unit_id, " +
                    "   unit.name AS unit_name, " +
                    "   null AS cat_category_id, " +
                    "   null AS cat_name, " +
                    "   category_id AS category_id, " +
                    "   '" + CategoryProductItem.TYPE_PRODUCT + "' AS type " +
                    "FROM products  product" +
                    "   LEFT JOIN units unit ON product.unit_id = unit.unit_id " +
                    "WHERE list_id = :listID " +
                    "   AND status <> :excludeStatus ";

    @Insert
    void insert(Product product);

    @Update
    void update(Product product);

    @Query("SELECT 1 " +
            "FROM products " +
            "WHERE lower(name) = lower(:name) " +
            "   AND list_id = :listID " +
            "LIMIT 1")
    boolean isProductsWithSameNameAndListExists(String name, UUID listID);

    @Query(value = "UPDATE products" +
            "       SET status = :status" +
            "       WHERE list_id = :listID")
    void updateStatus(@Product.ProductStatus int status, UUID listID);

    @Query(value = "UPDATE products" +
            "       SET template_id = null" +
            "       WHERE template_id = :templateID")
    void clearTemplateIDs(UUID templateID);

    @Query(value = "UPDATE products " +
            "       SET category_id = '" + Category.DEFAULT_CATEGORY_ID_STRING + "'" +
            "       WHERE category_id = :categoryID")
    void setDefaultCategoryID(UUID categoryID);

    @Delete
    void delete(Product product);

    @Query(value = "DELETE FROM products WHERE template_id = :templateID AND list_id = :listID")
    void delete(UUID templateID, UUID listID);

    @Query(value = "DELETE FROM products WHERE status = :status AND list_id = :listID")
    void delete(@Product.ProductStatus int status, UUID listID);

    @Query(value = "SELECT * FROM products WHERE product_id = :productID")
    LiveData<Product> findByID(UUID productID);

    @Query(value = "SELECT * FROM products WHERE product_id = :productID")
    Product findByIDSync(UUID productID);

    @Query(value = "SELECT * FROM products WHERE list_id = :listID ORDER BY name ")
    List<Product> findByListIDSync(UUID listID);


    @Query(value = QUERY_PRODUCTS_BY_LIST_ID + " ORDER BY prod_name")
    DataSource.Factory<Integer, CategoryProductItem> findByListIDSortByName(UUID listID);

    @Query(value = QUERY_PRODUCTS_BY_LIST_ID + " ORDER BY prod_status, prod_name")
    DataSource.Factory<Integer, CategoryProductItem> findByListIDSortByStatusAndName(UUID listID);

    @Query(value = QUERY_PRODUCTS_BY_LIST_ID + " ORDER BY prod_order")
    DataSource.Factory<Integer, CategoryProductItem> findByListIDSortByProductOrder(UUID listID);

    @Query(value = QUERY_GROUPED_PRODUCTS_BY_LIST_ID + ", prod_name")
    DataSource.Factory<Integer, CategoryProductItem> findByListIDSortByNameWithCategory(UUID listID);

    @Query(value = QUERY_GROUPED_PRODUCTS_BY_LIST_ID + ", prod_status, prod_name")
    DataSource.Factory<Integer, CategoryProductItem> findByListIDSortByStatusAndNameWithCategory(UUID listID);

    @Query(value = QUERY_GROUPED_PRODUCTS_BY_LIST_ID + ", prod_order")
    DataSource.Factory<Integer, CategoryProductItem> findByListIDSortByProductOrderWithCategory(UUID listID);


    @Query(value = QUERY_PRODUCTS_BY_LIST_ID_EXCLUDE_STATUS + " ORDER BY prod_name")
    List<CategoryProductItem> findByListIDSortByName(UUID listID, @Product.ProductStatus int excludeStatus);

    @Query(value = QUERY_PRODUCTS_BY_LIST_ID_EXCLUDE_STATUS + " ORDER BY prod_status, prod_name")
    List<CategoryProductItem> findByListIDSortByStatusAndName(UUID listID, @Product.ProductStatus int excludeStatus);

    @Query(value = QUERY_PRODUCTS_BY_LIST_ID_EXCLUDE_STATUS + " ORDER BY prod_order")
    List<CategoryProductItem> findByListIDSortByProductOrder(UUID listID, @Product.ProductStatus int excludeStatus);

    @Query(value = QUERY_GROUPED_PRODUCTS_BY_LIST_ID_EXCLUDE_STATUS + ", prod_name")
    List<CategoryProductItem> findByListIDSortByNameWithCategory(UUID listID, @Product.ProductStatus int excludeStatus);

    @Query(value = QUERY_GROUPED_PRODUCTS_BY_LIST_ID_EXCLUDE_STATUS + ", prod_status, prod_name")
    List<CategoryProductItem> findByListIDSortByStatusAndNameWithCategory(UUID listID, @Product.ProductStatus int excludeStatus);

    @Query(value = QUERY_GROUPED_PRODUCTS_BY_LIST_ID_EXCLUDE_STATUS + ", prod_order")
    List<CategoryProductItem> findByListIDSortByProductOrderWithCategory(UUID listID, @Product.ProductStatus int excludeStatus);


    @Query(value = "SELECT max(`order`) FROM products WHERE list_id = :listID")
    double getMaxProductOrderByListID(UUID listID);

    @Query(value = "SELECT min(`order`) FROM products WHERE list_id = :listID")
    double getMinProductOrderByListID(UUID listID);

    @Query(value = "UPDATE products " +
            "       SET `order` = (" +
            "           SELECT (SELECT count(*)" +
            "                   FROM products" +
            "                   WHERE name < product.name" +
            "                       AND list_id = :listID)" +
            "           FROM products product" +
            "           WHERE products.product_id = product.product_id" +
            "               AND list_id = :listID" +
            "           ORDER BY name) * " + PRODUCT_ORDER_STEP +
            "        WHERE list_id = :listID")
    void updateProductOrdersByListIDSortByName(UUID listID);

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
            "           ORDER BY status, name) * " + PRODUCT_ORDER_STEP +
            "        WHERE list_id = :listID")
    void updateProductOrdersByListIDSortByStatusAndName(UUID listID);

    @Query("SELECT CASE" +
            "         WHEN COUNT(1) > 0 THEN 1 " +
            "         ELSE 0" +
            "       END " +
            "FROM products " +
            "WHERE list_id = :listID " +
            "   AND status <> :status " +
            "LIMIT 1")
    boolean hasProductsWithStatusNot(UUID listID, @Product.ProductStatus int status);
}
