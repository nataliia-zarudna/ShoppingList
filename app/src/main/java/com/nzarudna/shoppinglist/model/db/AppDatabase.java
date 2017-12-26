package com.nzarudna.shoppinglist.model.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.nzarudna.shoppinglist.model.Category;
import com.nzarudna.shoppinglist.model.Product;
import com.nzarudna.shoppinglist.model.ProductTemplate;
import com.nzarudna.shoppinglist.model.ShoppingList;
import com.nzarudna.shoppinglist.model.User;

/**
 * Room application database
 */
@Database(entities = {Product.class, Category.class, ProductTemplate.class, ShoppingList.class, User.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ProductDao productDao();

    public abstract ShoppingListDao shoppingListDao();
}
