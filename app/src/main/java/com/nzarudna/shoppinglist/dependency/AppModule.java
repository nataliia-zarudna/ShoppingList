package com.nzarudna.shoppinglist.dependency;

import android.content.Context;
import android.content.SharedPreferences;

import com.nzarudna.shoppinglist.AndroidResourceResolver;
import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.ResourceResolver;

import dagger.Module;
import dagger.Provides;

/**
 * Provides application context
 */
@Module
public class AppModule {

    private Context mContext;

    public AppModule(Context context) {
        this.mContext = context;
    }

    @Provides
    public Context getContext() {
        return mContext;
    }

    @Provides
    public SharedPreferences provideSharedPreferences() {
        return mContext.getSharedPreferences(mContext.getString(R.string.preference_key_file), Context.MODE_PRIVATE);
    }

    @Provides
    public ResourceResolver provideResourceResolver() {
        return new AndroidResourceResolver(mContext);
    }

}
