package com.nzarudna.shoppinglist.model.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.nzarudna.shoppinglist.model.Category;
import com.nzarudna.shoppinglist.model.Product;
import com.nzarudna.shoppinglist.model.ProductTemplate;
import com.nzarudna.shoppinglist.model.ProductsList;
import com.nzarudna.shoppinglist.model.User;
import com.nzarudna.shoppinglist.model.dao.CategoryDao;
import com.nzarudna.shoppinglist.model.dao.ProductDao;
import com.nzarudna.shoppinglist.model.dao.ProductsListDao;
import com.nzarudna.shoppinglist.model.dao.UserDao;

/**
 * Room application database
 */
@Database(entities = {Product.class, Category.class, ProductTemplate.class, ProductsList.class, User.class}, version = 1)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ProductDao productDao();

    public abstract ProductsListDao productsListDao();

    public abstract UserDao userDao();

    public abstract CategoryDao categoryDao();
}
