package com.nzarudna.shoppinglist.model;

import com.nzarudna.shoppinglist.utils.AppExecutors;

import javax.inject.Singleton;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

@Singleton
public abstract class BaseRepository<T> {

    protected AppExecutors mAppExecutors;

    public BaseRepository(AppExecutors appExecutors) {
        mAppExecutors = appExecutors;
    }

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
