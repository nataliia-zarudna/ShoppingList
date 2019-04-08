package com.nzarudna.shoppinglist.model.category;

import java.util.List;
import java.util.UUID;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

/**
 * Category DAO
 */
@Dao
public interface CategoryDao {

    @Insert
    void insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories WHERE category_id = :categoryID")
    Category findByIDSync(UUID categoryID);

    @Query("SELECT * FROM categories WHERE category_id = :categoryID")
    LiveData<Category> findByID(UUID categoryID);

    @Query("SELECT category_id FROM categories WHERE lower(name) = lower(:name) LIMIT 1")
    UUID findBySimilarName(String name);

    @Query("SELECT * FROM categories WHERE category_id <> '" + Category.DEFAULT_CATEGORY_ID_STRING + "'")
    DataSource.Factory<Integer, Category> findAll();

    @Query("SELECT category.*, " +
            "   (SELECT 1 " +
            "    FROM products " +
            "    WHERE category_id = category.category_id " +
            "    UNION " +
            "    SELECT 1 " +
            "    FROM product_templates " +
            "    WHERE category_id = category.category_id " +
            "    LIMIT 1 " +
            "    ) AS is_used " +
            "FROM categories category " +
            "WHERE category_id <> '" + Category.DEFAULT_CATEGORY_ID_STRING + "' " +
            "ORDER BY name ")
    DataSource.Factory<Integer, CategoryStatisticsItem> findAllWithStatistics();

    @Query("SELECT * FROM categories")
    LiveData<List<Category>> findAllLiveData();
}
