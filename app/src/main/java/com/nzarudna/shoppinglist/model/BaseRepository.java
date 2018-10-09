package com.nzarudna.shoppinglist.model;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.nzarudna.shoppinglist.utils.AppExecutors;

import javax.inject.Inject;

public abstract class BaseRepository<T> {

    @Inject
    protected AppExecutors mAppExecutors;

    @WorkerThread
    protected abstract T create(T entity) throws Exception;

    public void createAsync(T entity, @Nullable AsyncResultListener<T> listener) {
        mAppExecutors.loadAsync(() -> create(entity), listener);
    }

    @WorkerThread
    protected abstract T update(T entity) throws Exception;

    public void updateAsync(T entity, @Nullable AsyncResultListener<T> listener) {
        mAppExecutors.loadAsync(() -> update(entity), listener);
    }

    @WorkerThread
    protected abstract void remove(T entity);

    public void removeAsync(T entity, @Nullable AsyncListener listener) {
        mAppExecutors.loadAsync(() -> remove(entity), listener);
    }

}
