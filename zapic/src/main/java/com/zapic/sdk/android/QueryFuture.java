package com.zapic.sdk.android;

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
 * A future that represents a query to the Zapic application.
 * <p>
 * The {@code done} method must be invoked on the application's main thread to complete the future and invoke the result
 * callback. The {@code cancel} method may be invoked on any thread to cancel the future. The {@code get} methods always
 * return {@code null} when the future is complete.
 *
 * @param <Result> The data type of the result.
 */
final class QueryFuture<Result> implements Future {
    /**
     * The number of nanoseconds per microsecond.
     */
    private static final int NANOSECONDS_PER_MICROSECOND = 1000;

    /**
     * The number of nanoseconds per millisecond.
     */
    private static final int NANOSECONDS_PER_MILLISECOND = 1000000;

    /**
     * The tag used to identify log messages.
     */
    @NonNull
    private static final String TAG = "QueryFuture";

    /**
     * The request identifier.
     */
    @NonNull
    private final UUID requestId;

    /**
     * The
     */
    private final int dataType;

    /**
     * The
     */
    private final int dataTypeVersion;
    /**
     * The result callback invoked when the future is complete.
     */
    @NonNull
    private final ZapicQueryCallback<Result> callback;

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
     * Creates a new {@link QueryFuture} instance with the specified result callback.
     *
     * @param callback The result callback invoked when the future is complete.
     */
    QueryFuture(
            @NonNull final UUID requestId,
            @QueryFuture.DataTypeDef final int dataType,
            final int dataTypeVersion,
            @NonNull final ZapicQueryCallback<Result> callback) {
        this.requestId = requestId;
        this.dataType = dataType;
        this.dataTypeVersion = dataTypeVersion;
        this.callback = callback;
        this.cancelled = false;
        this.done = false;
        this.lock = new Object();
        this.startTime = System.nanoTime();
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

                ZapicLog.d(QueryFuture.TAG, "Cancelled asynchronous query to the Zapic application");
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

        long callbackStartTime = System.nanoTime();
        try {
            this.callback.onComplete(result, error);
        } catch (Exception e) {
            ZapicLog.e(QueryFuture.TAG, e, "An uncaught error occurred invoking the result callback");
        } catch (Throwable t) {
            ZapicLog.e(QueryFuture.TAG, t, "An uncaught error occurred invoking the result callback");
            throw t;
        } finally {
            int logLevel = ZapicLog.getLogLevel();
            if (logLevel <= Log.DEBUG) {
                final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
                long endTime = System.nanoTime();

                long callbackDuration = endTime - callbackStartTime;
                String callbackDuration2;
                if (callbackDuration >= QueryFuture.NANOSECONDS_PER_MILLISECOND) {
                    callbackDuration2 = numberFormat.format(TimeUnit.NANOSECONDS.toMillis(callbackDuration)) + "ms";
                } else if (callbackDuration >= QueryFuture.NANOSECONDS_PER_MICROSECOND) {
                    callbackDuration2 = numberFormat.format(TimeUnit.NANOSECONDS.toMicros(callbackDuration)) +
                            "\u00B5s";
                } else {
                    callbackDuration2 = numberFormat.format(callbackDuration) + "ns";
                }

                long duration = endTime - this.startTime;
                String duration2;
                if (duration >= QueryFuture.NANOSECONDS_PER_MILLISECOND) {
                    duration2 = numberFormat.format(TimeUnit.NANOSECONDS.toMillis(duration)) + "ms";
                } else if (duration >= QueryFuture.NANOSECONDS_PER_MICROSECOND) {
                    duration2 = numberFormat.format(TimeUnit.NANOSECONDS.toMicros(duration)) + "\u00B5s";
                } else {
                    duration2 = numberFormat.format(duration) + "ns";
                }

                ZapicLog.d(
                        QueryFuture.TAG,
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

    @IntDef({
            QueryFuture.DataType.CHALLENGE_LIST,
            QueryFuture.DataType.COMPETITION_LIST,
            QueryFuture.DataType.PLAYER,
            QueryFuture.DataType.STATISTIC_LIST,
    })
    @Retention(RetentionPolicy.SOURCE)
    @SuppressWarnings({"unused", "WeakerAccess"})
    @interface DataTypeDef {
    }

    /**
     * Represents the types of queries supported by the Zapic SDK and the Zapic application.
     */
    final static class DataType {
        /**
         * Identifies a query for the list of challenges.
         */
        final static int CHALLENGE_LIST = 1000;

        /**
         * Identifies a query for the list of competitions.
         */
        final static int COMPETITION_LIST = 1001;

        /**
         * Identifies a query for the player.
         */
        final static int PLAYER = 1002;

        /**
         * Identifies a query for the list of statistics.
         */
        final static int STATISTIC_LIST = 1003;

        /**
         * Prevents creating a new {@link QueryFuture.DataType} instance.
         */
        private DataType() {
        }
    }
}
