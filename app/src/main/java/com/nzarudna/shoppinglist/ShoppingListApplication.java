package com.nzarudna.shoppinglist;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.nzarudna.shoppinglist.persistence.RoomDaoModule;
import com.nzarudna.shoppinglist.persistence.db.AppDatabase;

/**
 * Application class
 */
public class ShoppingListApplication extends Application {

    private static final String DATABASE_NAME = "shopping_list";

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        AppDatabase database = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME).build();

        RoomDaoModule daoModule = new RoomDaoModule(database);
        AppModule appModule = new AppModule(this);
        appComponent = DaggerAppComponent.builder()
                .appModule(appModule)
                .roomDaoModule(daoModule)
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
