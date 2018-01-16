package com.nzarudna.shoppinglist;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Provides application context
 */
@Module
public class ContextModule {

    private Context mContext;

    public ContextModule(Context context) {
        this.mContext = context;
    }

    @Provides
    public Context getContext() {
        return mContext;
    }

}
