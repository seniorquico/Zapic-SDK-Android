package com.zapic.sdk.android;

import android.support.annotation.MainThread;
import android.support.annotation.Nullable;

/**
 * Represents a callback used to return the result of a Zapic query.
 *
 * @param <Result> The type of the result.
 * @author Kyle Dodson
 * @since 1.3.0
 */
public interface ZapicQueryCallback<Result> {
    /**
     * Called when the Zapic query is complete.
     *
     * @param result The result. This will be {@code null} if the query completed with an error. This may be
     *               {@code null} if the query completed normally. For example, querying for the current player when the
     *               player has not yet logged in will return a {@code null} result.
     * @param error  The error. This will be {@code null} if the query completed normally.
     * @since 1.3.0
     */
    @MainThread
    void onComplete(@Nullable Result result, @Nullable ZapicException error);
}
