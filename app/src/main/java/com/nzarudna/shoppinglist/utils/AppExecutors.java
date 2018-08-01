package com.nzarudna.shoppinglist.utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutors {

    private Executor mDiscIO;
    private Executor mNetworkIO;
    private Executor mMainThread;

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

    private class MainThreadExecutor implements Executor {

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
