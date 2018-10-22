package com.zapic.sdk.android;

import android.os.SystemClock;
import android.support.annotation.*;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.NumberFormat;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Represents a Zapic query as a {@link Future}.
 * <p>
 * The {@code done} method must be invoked to complete the future and invoke the callback. The {@code done} method must
 * be invoked on the application's main thread. The {@code cancel} method may be invoked on any thread to cancel the
 * future. The {@code get} methods always return {@code null} when the future is complete.
 *
 * @param <Result> The type of the result.
 */
final class ZapicQuery<Result> implements Future {
    /**
     * The tag used to identify log messages.
     */
    @NonNull
    private static final String TAG = "ZapicQuery";

    /**
     * The result callback invoked when the future is complete.
     */
    @NonNull
    private final ZapicQueryCallback<Result> callback;

    /**
     * The data type.
     */
    private final String dataType;

    /**
     * The data type version.
     */
    private final int dataTypeVersion;

    /**
     * The request identifier.
     */
    @NonNull
    private final UUID requestId;

    /**
     * The lock object used to synchronize access to {@code cancelled} and {@code done}.
     */
    @NonNull
    private final Object lock;

    /**
     * The system time when the future started.
     */
    private final long startTime;

    /**
     * A value indicating whether the future has been cancelled.
     */
    private boolean cancelled;

    /**
     * A value indicating whether the future has completed.
     */
    private boolean done;

    /**
     * Creates a new {@link ZapicQuery} instance with the specified result callback.
     *
     * @param requestId       The request identifier.
     * @param dataType        The data type.
     * @param dataTypeVersion The data type version.
     * @param callback        The result callback invoked when the future is complete.
     */
    @AnyThread
    ZapicQuery(
            @NonNull final UUID requestId,
            @ZapicQuery.DataTypeDef final String dataType,
            final int dataTypeVersion,
            @NonNull final ZapicQueryCallback<Result> callback) {
        this.callback = callback;
        this.cancelled = false;
        this.dataType = dataType;
        this.dataTypeVersion = dataTypeVersion;
        this.done = false;
        this.lock = new Object();
        this.requestId = requestId;
        this.startTime = SystemClock.uptimeMillis();
    }

    @AnyThread
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (this.done) {
            return false;
        }

        if (this.cancelled) {
            return true;
        }

        synchronized (this.lock) {
            if (this.done) {
                return false;
            }

            if (this.cancelled) {
                return true;
            }

            if (mayInterruptIfRunning) {
                this.cancelled = true;
                this.lock.notifyAll();

                ZapicLog.i(ZapicQuery.TAG, "Cancelled asynchronous query to the Zapic application");
                return true;
            }

            return false;
        }
    }

    /**
     * Attempts to complete the future. This attempt will fail if the future is already complete, is already cancelled,
     * or could not be completed for some other reason.
     * <p>
     * After this method returns, subsequent calls to {@link #isDone()} will always return {@code true}. Subsequent
     * calls to {@link #isCancelled()} will always return {@code false} if this method completed the future.
     *
     * @param result The result. This must be {@code null} if the asynchronous query encountered an error.
     * @param error  The error. This must be {@code null} if the asynchronous query completed normally.
     */
    @MainThread
    void done(@Nullable final Result result, @Nullable final ZapicException error) {
        if (this.cancelled || this.done) {
            return;
        }

        synchronized (this.lock) {
            if (this.cancelled || this.done) {
                return;
            }

            this.done = true;
            this.lock.notifyAll();
        }

        long callbackStartTime = SystemClock.uptimeMillis();
        try {
            this.callback.onComplete(result, error);
        } catch (Exception e) {
            ZapicLog.e(ZapicQuery.TAG, e, "An uncaught error occurred invoking the result callback");
        } catch (Throwable t) {
            ZapicLog.e(ZapicQuery.TAG, t, "An uncaught error occurred invoking the result callback");
            throw t;
        } finally {
            int logLevel = ZapicLog.getLogLevel();
            if (logLevel <= Log.INFO) {
                final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
                long endTime = SystemClock.uptimeMillis();

                long callbackDuration = endTime - callbackStartTime;
                String callbackDuration2 = numberFormat.format(callbackDuration) + "ms";

                long duration = endTime - this.startTime;
                String duration2 = numberFormat.format(duration) + "ms";

                ZapicLog.i(
                        ZapicQuery.TAG,
                        "Completed asynchronous query to the Zapic application in %s (completed result callback in %s)",
                        duration2,
                        callbackDuration2);
            }
        }
    }

    @AnyThread
    @Override
    public boolean isCancelled() {
        if (this.cancelled) {
            return true;
        }

        if (this.done) {
            return false;
        }

        synchronized (this.lock) {
            return this.cancelled;
        }
    }

    @AnyThread
    @Override
    public boolean isDone() {
        if (this.cancelled || this.done) {
            return true;
        }

        synchronized (this.lock) {
            return this.cancelled || this.done;
        }
    }

    @AnyThread
    @Override
    public Object get() throws CancellationException, InterruptedException {
        if (!this.cancelled && !this.done) {
            synchronized (this.lock) {
                if (!this.cancelled && !this.done) {
                    this.lock.wait();
                    if (this.cancelled) {
                        throw new CancellationException();
                    }
                }
            }
        }

        return null;
    }

    @AnyThread
    @Override
    public Object get(long timeout, @NonNull TimeUnit unit)
            throws CancellationException, InterruptedException, TimeoutException {
        if (!this.cancelled && !this.done) {
            synchronized (this.lock) {
                if (!this.cancelled && !this.done) {
                    unit.timedWait(this.lock, timeout);
                    if (this.cancelled) {
                        throw new CancellationException();
                    }

                    if (!this.done) {
                        throw new TimeoutException();
                    }
                }
            }
        }

        return null;
    }

    /**
     * Gets the data type.
     *
     * @return The data type.
     */
    @AnyThread
    @CheckResult
    @ZapicQuery.DataTypeDef
    String getDataType() {
        return this.dataType;
    }

    /**
     * Gets the data type version.
     *
     * @return The data type version.
     */
    @AnyThread
    @CheckResult
    int getDataTypeVersion() {
        return this.dataTypeVersion;
    }

    /**
     * Gets the request identifier.
     *
     * @return The request identifier.
     */
    @AnyThread
    @CheckResult
    @NonNull
    UUID getRequestId() {
        return this.requestId;
    }

    @StringDef({
            ZapicQuery.DataType.CHALLENGE_LIST,
            ZapicQuery.DataType.COMPETITION_LIST,
            ZapicQuery.DataType.PLAYER,
            ZapicQuery.DataType.STATISTIC_LIST,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface DataTypeDef {
    }

    /**
     * Provides constant values that identify the different data types.
     */
    final static class DataType {
        /**
         * Identifies a query for the list of challenges.
         */
        final static String CHALLENGE_LIST = "challenges";

        /**
         * Identifies a query for the list of competitions.
         */
        final static String COMPETITION_LIST = "competitions";

        /**
         * Identifies a query for the player.
         */
        final static String PLAYER = "player";

        /**
         * Identifies a query for the list of statistics.
         */
        final static String STATISTIC_LIST = "statistics";

        /**
         * Prevents creating a new {@link ZapicQuery.DataType} instance.
         */
        private DataType() {
        }
    }
}
