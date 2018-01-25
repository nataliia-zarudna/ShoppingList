package com.nzarudna.shoppinglist.persistence;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nzarudna.shoppinglist.product.ProductTemplate;

/**
 * Product Template DAO
 */
@Dao
public interface ProductTemplateDao {

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

    @Query(value = "SELECT * FROM product_templates ORDER BY name")
    DataSource.Factory<Integer, ProductTemplate> findAllSortByName();

    @Query(value = "SELECT * FROM product_templates ORDER BY category_id, name")
    DataSource.Factory<Integer, ProductTemplate> findAllSortByCategoryIDAndName();
}
