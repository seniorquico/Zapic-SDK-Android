package com.zapic.sdk.android;

import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * A future that represents a completed operation.
 */
final class ImmediateFuture implements Future {
    /**
     * The singleton instance.
     */
    private static final ImmediateFuture instance = new ImmediateFuture();

    /**
     * Creates a new {@link ImmediateFuture} instance.
     */
    private ImmediateFuture() {
    }

    /**
     * Gets the singleton instance.
     *
     * @return The singleton instance.
     */
    static ImmediateFuture getInstance() {
        return ImmediateFuture.instance;
    }

    @AnyThread
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @AnyThread
    @Override
    public boolean isCancelled() {
        return false;
    }

    @AnyThread
    @Override
    public boolean isDone() {
        return true;
    }

    @AnyThread
    @Override
    public Object get() {
        return null;
    }

    @AnyThread
    @Override
    public Object get(long timeout, @NonNull TimeUnit unit) {
        return null;
    }
}
