package com.nzarudna.shoppinglist.model.category;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;
import java.util.UUID;

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

    //TODO: add test
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
