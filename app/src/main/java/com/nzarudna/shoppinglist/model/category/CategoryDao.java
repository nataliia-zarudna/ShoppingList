package com.nzarudna.shoppinglist.model.category;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nzarudna.shoppinglist.model.category.Category;

/**
 * Category DAO
 */
@Dao
public interface CategoryDao {

    @Insert
    long insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories WHERE category_id = :categoryID")
    Category findByIDSync(int categoryID);

    @Query("SELECT * FROM categories WHERE category_id = :categoryID")
    LiveData<Category> findByID(int categoryID);

    @Query("SELECT * FROM categories")
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
            "FROM categories category")
    DataSource.Factory<Integer, CategoryStatisticsItem> findAllWithStatistics();
}
