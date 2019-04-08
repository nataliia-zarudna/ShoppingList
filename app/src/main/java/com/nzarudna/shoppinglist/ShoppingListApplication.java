package com.nzarudna.shoppinglist;

import com.facebook.stetho.Stetho;
import com.nzarudna.shoppinglist.dependency.AppComponent;
import com.nzarudna.shoppinglist.dependency.AppModule;
import com.nzarudna.shoppinglist.dependency.DaggerAppComponent;
import com.nzarudna.shoppinglist.model.persistence.RoomDaoModule;
import com.nzarudna.shoppinglist.model.persistence.db.AppDatabase;

import androidx.multidex.MultiDexApplication;

/**
 * Application class
 */
public class ShoppingListApplication extends MultiDexApplication {

    private static final String DATABASE_NAME = "shopping_list";

    private static AppComponent sAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);

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
