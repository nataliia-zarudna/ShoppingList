package com.nzarudna.shoppinglist.model.template;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

/**
 * Product Template DAO
 */
@Dao
public interface ProductTemplateDao {

    String QUERY_ALL_GROUPED_TEMPLATES = "" +
            "SELECT template_id AS temp_template_id, " +
            "   name AS temp_name, " +
            "   category_id AS temp_category_id, " +
            "   unit_id AS temp_unit_id, " +
            "   NULL AS cat_category_id, " +
            "   NULL AS cat_name, " +
            "   category_id AS category_id, " +
            "   '" + CategoryTemplateItem.TYPE_TEMPLATE + "' AS type " +
            "FROM product_templates " +
            "UNION " +
            "SELECT NULL AS temp_template_id, " +
            "   NULL AS temp_name, " +
            "   NULL AS temp_category_id, " +
            "   NULL AS temp_unit_id, " +
            "   category_id AS cat_category_id, " +
            "   name AS cat_name, " +
            "   category_id AS category_id, " +
            "   '" + CategoryTemplateItem.TYPE_CATEGORY + "' AS type " +
            "FROM categories " +
            "WHERE category_id in (SELECT DISTINCT category_id " +
            "                      FROM product_templates) " +
            "ORDER BY category_id, type, temp_name ";

    String QUERY_ALL_TEMPLATES = "" +
            "SELECT template_id AS temp_template_id, " +
            "   name AS temp_name, " +
            "   category_id AS temp_category_id, " +
            "   unit_id AS temp_unit_id, " +
            "   '" + CategoryTemplateItem.TYPE_TEMPLATE + "' AS type " +
            "FROM product_templates " +
            "ORDER BY temp_name ";

    String QUERY_ALL_GROUPED_TEMPLATES_WITH_USED_IN_LIST = "" +
            "SELECT template_id AS temp_template_id, " +
            "   name AS temp_name, " +
            "   category_id AS temp_category_id, " +
            "   unit_id AS temp_unit_id, " +
            "   (SELECT 1 " +
            "       FROM products " +
            "       WHERE list_id = :listID " +
            "           AND template_id = template.template_id " +
            "   ) AS is_used_in_list, " +
            "   NULL AS cat_category_id, " +
            "   NULL AS cat_name, " +
            "   category_id AS category_id, " +
            "   '" + CategoryTemplateItem.TYPE_TEMPLATE + "' AS type " +
            "FROM product_templates template " +
            "UNION " +
            "SELECT NULL AS temp_template_id, " +
            "   NULL AS temp_name, " +
            "   NULL AS temp_category_id, " +
            "   NULL AS temp_unit_id, " +
            "   NULL AS is_used_in_list, " +
            "   category_id AS cat_category_id, " +
            "   name AS cat_name, " +
            "   category_id AS category_id, " +
            "   '" + CategoryTemplateItem.TYPE_CATEGORY + "' AS type " +
            "FROM categories " +
            "WHERE category_id in (SELECT DISTINCT category_id " +
            "                      FROM product_templates) " +
            "ORDER BY category_id, type, temp_name ";

    String QUERY_ALL_TEMPLATES_WITH_USED_IN_LIST = "" +
            "SELECT template_id AS temp_template_id, " +
            "   name AS temp_name, " +
            "   category_id AS temp_category_id, " +
            "   unit_id AS temp_unit_id, " +
            "   (SELECT 1 " +
            "       FROM products " +
            "       WHERE list_id = :listID " +
            "           AND template_id = template.template_id " +
            "   ) AS is_used_in_list, " +
            "   '" + CategoryTemplateItem.TYPE_TEMPLATE + "' AS type " +
            "FROM product_templates template " +
            "ORDER BY temp_name ";

    @Insert
    long insert(ProductTemplate template);

    @Update
    void update(ProductTemplate template);

    @Delete
    void delete(ProductTemplate template);

    @Query(value = "SELECT * FROM product_templates WHERE template_id = :templateID")
    ProductTemplate findByIDSync(int templateID);

    @Query(value = "SELECT * FROM product_templates WHERE template_id = :templateID")
    LiveData<ProductTemplate> findByID(int templateID);

    @Query(value = QUERY_ALL_TEMPLATES)
    DataSource.Factory<Integer, CategoryTemplateItem> findAllSortByName();

    @Query(value = QUERY_ALL_GROUPED_TEMPLATES)
    DataSource.Factory<Integer, CategoryTemplateItem> findAllSortByNameWithCategory();

    @Query(value = QUERY_ALL_TEMPLATES_WITH_USED_IN_LIST)
    DataSource.Factory<Integer, CategoryTemplateItemWithListStatistics> findAllSortByNameWithListStatistics(int listID);

    @Query(value = QUERY_ALL_GROUPED_TEMPLATES_WITH_USED_IN_LIST)
    DataSource.Factory<Integer, CategoryTemplateItemWithListStatistics> findAllSortByNameWithCategoryAndListStatistics(int listID);
}
