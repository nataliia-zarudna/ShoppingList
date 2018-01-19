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

    private static AppComponent sAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        AppDatabase database = AppDatabase.getInstance(this);

        RoomDaoModule daoModule = new RoomDaoModule(database);
        AppModule appModule = new AppModule(this);
        sAppComponent = DaggerAppComponent.builder()
                .appModule(appModule)
                .roomDaoModule(daoModule)
                .build();
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }
}
