package com.nzarudna.shoppinglist;

import android.support.annotation.NonNull;

import com.nzarudna.shoppinglist.model.AsyncListener;
import com.nzarudna.shoppinglist.model.AsyncResultListener;

import org.mockito.Mockito;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

public class BaseAsyncTest {

    public static final int COUNT_DOWN_TIMEOUT = 3000;

    @NonNull
    protected <T> AsyncResultListener<T> getAsyncResultListener(T result, CountDownLatch countDownLatch) {
        AsyncResultListener<T> asyncListener = Mockito.mock(AsyncResultListener.class);
        doAnswer(invocation -> {

            assertEquals(result, invocation.getArgument(0));

            countDownLatch.countDown();
            return null;
        }).when(asyncListener).onAsyncSuccess(result);
        return asyncListener;
    }

    @NonNull
    protected <T> AsyncResultListener<T> getEmptyAsyncResultListener(CountDownLatch countDownLatch) {
        return new AsyncResultListener<T>() {
            @Override
            public void onAsyncSuccess(T result) {
                countDownLatch.countDown();
            }

            @Override
            public void onAsyncError(Exception e) {
                countDownLatch.countDown();
            }
        };
    }

    @NonNull
    protected AsyncListener getEmptyAsyncListener(CountDownLatch countDownLatch) {
        AsyncListener asyncListener = Mockito.mock(AsyncListener.class);

        doAnswer(invocation -> {
            countDownLatch.countDown();
            return null;
        }).when(asyncListener).onAsyncSuccess();

        return asyncListener;
    }

    protected <T> AsyncResultListener<T> getAsyncResultListenerForError(Class<? extends Exception> exceptionClass, CountDownLatch countDown) {
        AsyncResultListener<T> asyncResultListener = Mockito.mock(AsyncResultListener.class);
        doAnswer(invocation -> {
            countDown.countDown();
            return null;
        }).when(asyncResultListener).onAsyncError(any(exceptionClass));
        return asyncResultListener;
    }

    protected void await(CountDownLatch countDownLatch) throws InterruptedException {
        countDownLatch.await(COUNT_DOWN_TIMEOUT, TimeUnit.MILLISECONDS);
    }
}
