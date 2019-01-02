package com.nzarudna.shoppinglist;

import com.nzarudna.shoppinglist.model.AsyncListener;
import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.BaseRepository;

import java.util.concurrent.CountDownLatch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

public abstract class BaseRepositoryTest<T> extends BaseAsyncTest {

    protected void verifyCreate(T newEntity) throws InterruptedException {
        verifyCreate(newEntity, newEntity);
    }

    protected void verifyCreate(T inputEntity, T resultEntity) throws InterruptedException {

        final CountDownLatch countDown = new CountDownLatch(1);

        AsyncResultListener<T> asyncListener = getAsyncResultListener(resultEntity, countDown);
        getRepositorySubject().createAsync(inputEntity, asyncListener);
        await(countDown);

        verify(asyncListener).onAsyncSuccess(resultEntity);
    }

    protected void verifyCreateWithException(T newEntity, Class<? extends Exception> exceptionClass) throws InterruptedException {

        final CountDownLatch countDown = new CountDownLatch(1);

        AsyncResultListener<T> asyncResultListener = getAsyncResultListenerForError(exceptionClass, countDown);

        getRepositorySubject().createAsync(newEntity, asyncResultListener);
        await(countDown);

        verify(asyncResultListener).onAsyncError(any(exceptionClass));
    }

    protected void verifyUpdate(T entity) throws InterruptedException {
        verifyUpdate(entity, entity);
    }

    protected void verifyUpdate(T inputEntity, T resultEntity) throws InterruptedException {

        final CountDownLatch countDown = new CountDownLatch(1);

        AsyncResultListener<T> asyncListener = getAsyncResultListener(resultEntity, countDown);
        getRepositorySubject().updateAsync(inputEntity, asyncListener);
        await(countDown);

        verify(asyncListener).onAsyncSuccess(resultEntity);
    }

    protected void verifyUpdateWithException(T newEntity, Class<? extends Exception> exceptionClass) throws InterruptedException {

        final CountDownLatch countDown = new CountDownLatch(1);

        AsyncResultListener<T> asyncResultListener = getAsyncResultListenerForError(exceptionClass, countDown);

        getRepositorySubject().updateAsync(newEntity, asyncResultListener);
        await(countDown);

        verify(asyncResultListener).onAsyncError(any(exceptionClass));
    }

    protected void verifyRemove(T entity) throws InterruptedException {

        final CountDownLatch countDown = new CountDownLatch(1);

        AsyncListener asyncListener = getEmptyAsyncListener(countDown);
        getRepositorySubject().removeAsync(entity, asyncListener);
        await(countDown);

        verify(asyncListener).onAsyncSuccess();
    }

    protected abstract BaseRepository<T> getRepositorySubject();
}
