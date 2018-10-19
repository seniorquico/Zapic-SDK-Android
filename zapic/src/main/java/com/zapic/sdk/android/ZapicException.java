package com.zapic.sdk.android;

import android.support.annotation.AnyThread;
import android.support.annotation.CheckResult;

/**
 * An exception thrown by the Zapic SDK.
 *
 * @author Kyle Dodson
 * @since 1.3.0
 */
public final class ZapicException extends Exception {
    /**
     * The error code.
     */
    private final int code;

    /**
     * Creates a new {@link ZapicException} instance with the specified error code and {@code null} as its detail
     * message.
     *
     * @param code The error code.
     * @since 1.3.0
     */
    @AnyThread
    public ZapicException(int code) {
        super();
        this.code = code;
    }

    /**
     * Creates a new {@link ZapicException} instance with the specified error code and detail message.
     *
     * @param code    The error code.
     * @param message The detail message.
     * @since 1.3.0
     */
    @AnyThread
    public ZapicException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Creates a new {@link ZapicException} instance with the specified detail message and cause.
     *
     * @param code    The error code.
     * @param message The detail message.
     * @param cause   The cause. (A {@code null} value is permitted, and indicates that the cause is nonexistent or
     *                unknown.)
     * @since 1.3.0
     */
    @AnyThread
    public ZapicException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * Creates a new {@link ZapicException} instance with the specified cause and a detail message of
     * {@code (cause==null ? null : cause.toString())} (which typically contains the class and detail message of
     * {@code cause}).
     *
     * @param code  The error code.
     * @param cause The cause. (A {@code null} value is permitted, and indicates that the cause is nonexistent or
     *              unknown.)
     * @since 1.3.0
     */
    @AnyThread
    public ZapicException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    /**
     * Gets the error code.
     *
     * @return The error code.
     * @see ZapicException.ErrorCode
     * @since 1.3.0
     */
    @AnyThread
    @CheckResult
    public int getCode() {
        return this.code;
    }

    /**
     * Provides constant values that identify the different codes returned by the Zapic SDK in a {@link ZapicException}.
     *
     * @author Kyle Dodson
     * @since 1.3.0
     */
    public static final class ErrorCode {
        /**
         * Identifies an error code that indicates the Zapic application failed to start.
         *
         * @since 1.3.0
         */
        public static final int FAILED_TO_START = 2600;

        /**
         * Identifies an error code that indicates the Zapic application sent an invalid message.
         *
         * @since 1.3.0
         */
        public static final int INVALID_RESPONSE = 2601;

        /**
         * Identifies an error code that indicates the version of the Zapic SDK is not supported by the Zapic
         * application.
         *
         * @since 1.3.0
         */
        public static final int VERSION_NOT_SUPPORTED = 2650;

        /**
         * Identifies an error code that indicates the Zapic application received an invalid message.
         *
         * @since 1.3.0
         */
        public static final int INVALID_QUERY = 2651;

        /**
         * Identifies an error code that indicates the Zapic application encountered a network-related error (offline,
         * timeout, etc).
         *
         * @since 1.3.0
         */
        public static final int NETWORK_ERROR = 2652;

        /**
         * Identifies an error code that indicates the Zapic application encountered a message that requires a logged-in
         * user.
         *
         * @since 1.3.0
         */
        public static final int LOGIN_REQUIRED = 2653;

        /**
         * Prevents creating a new {@link ZapicException.ErrorCode} instance.
         */
        private ErrorCode() {
        }
    }
}
