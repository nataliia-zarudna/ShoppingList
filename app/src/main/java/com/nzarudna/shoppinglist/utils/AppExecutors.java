package com.nzarudna.shoppinglist.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nzarudna.shoppinglist.model.AsyncListener;
import com.nzarudna.shoppinglist.model.AsyncResultListener;
import com.nzarudna.shoppinglist.model.ResultCallback;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

@Singleton
public class AppExecutors {

    private static final String TAG = AppExecutors.class.getSimpleName();

    private Executor mDiscIO;
    private Executor mNetworkIO;
    protected Executor mMainThread;


    public AppExecutors() {
        mDiscIO = Executors.newSingleThreadExecutor();
        mNetworkIO = Executors.newFixedThreadPool(3);
        mMainThread = new MainThreadExecutor();
    }

    public Executor getDiscIO() {
        return mDiscIO;
    }

    public Executor getNetworkIO() {
        return mNetworkIO;
    }

    public Executor getMainThread() {
        return mMainThread;
    }

    public <T> void loadAsync(Callable<T> asyncTask,
                              @Nullable AsyncResultListener<T> resultListener) {

        mDiscIO.execute(() -> {
            T result = null;
            try {
                result = asyncTask.call();
            } catch (Exception e) {
                ErrorHandler.logError(TAG, "Error in async task", e);
                mMainThread.execute(() -> {
                    if (resultListener != null) {
                        resultListener.onAsyncError(e);
                    }
                });
            }

            final T finalResult = result;
            mMainThread.execute(() -> {
                if (resultListener != null) {
                    resultListener.onAsyncSuccess(finalResult);
                }
            });
        });
    }

    public <T> void loadAsync(Callable<T> asyncTask,
                              @Nullable ResultCallback<T> resultCallback) {

        mDiscIO.execute(() -> {
            T result = null;
            try {
                result = asyncTask.call();
            } catch (Exception e) {
                ErrorHandler.logError(TAG, "Error in async task", e);
            }

            final T finalResult = result;
            mMainThread.execute(() -> {
                if (resultCallback != null) {
                    resultCallback.onResult(finalResult);
                }
            });
        });
    }

    public void loadAsync(Runnable asyncTask,
                              @Nullable AsyncListener listener) {

        mDiscIO.execute(() -> {
            try {
                asyncTask.run();
            } catch (Exception e) {
                ErrorHandler.logError(TAG, "Error in async task", e);
                mMainThread.execute(() -> {
                    if (listener != null) {
                        listener.onAsyncError(e);
                    }
                });
            }

            mMainThread.execute(() -> {
                if (listener != null) {
                    listener.onAsyncSuccess();
                }
            });
        });
    }

    private class MainThreadExecutor implements Executor {

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

//    public interface SuccessResultCallback<T> {
//        void onResult(T result);
//    }
//
//    public interface ErrorResultCallback<T> {
//        void onResult(Exception e);
//    }
}
