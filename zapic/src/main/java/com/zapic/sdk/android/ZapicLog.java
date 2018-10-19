package com.zapic.sdk.android;

import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Configures Zapic SDK logging.
 * <p>
 * The Zapic SDK writes to the standard Android logs using {@code Log.v}, {@code Log.d}, {@code Log.i}, {@code Log.w},
 * and {@code Log.e}. However, to reduce noise, the Zapic SDK is initially configured to only write errors using
 * {@code Log.e}. When developing, testing, or troubleshooting your game's integration with the Zapic SDK, it may be
 * helpful to increase the log level. For better performance, you should keep the default log level.
 * <p>
 * The log level may be changed during runtime at anytime. However, when developing, testing, or troubleshooting your
 * game's integration with the Zapic SDK, it is recommended to change the log level before calling {@code Zapic.start}
 * to catch any startup messages.
 * <p>
 * Example:
 * <pre>
 * public class MyApp extends Application {
 *    {@literal @}Override
 *     public void onCreate() {
 *         super.onCreate();
 *
 *         if (BuildConfig.DEBUG) {
 *             ZapicLog.setLogLevel(Log.INFO);
 *         }
 *
 *         Zapic.start(this);
 *     }
 * }
 * </pre>
 *
 * @author Kyle Dodson
 * @since 1.3.0
 */
public final class ZapicLog {
    /**
     * The tag used to identify log messages.
     */
    @NonNull
    private static final String TAG = "ZapicLog";

    /**
     * The current log level.
     */
    @ZapicLogLevelDef
    private static int logLevel = Log.ERROR;

    /**
     * Prevents creating a new {@link ZapicLog} instance.
     */
    private ZapicLog() {
    }

    /**
     * Gets the current log level.
     *
     * @return The current log level.
     */
    @AnyThread
    @ZapicLogLevelDef
    static int getLogLevel() {
        return ZapicLog.logLevel;
    }

    /**
     * Sets the log level.
     *
     * @param logLevel The new log level.
     * @throws IllegalArgumentException If {@code logLevel} is not equal to {@link Log#VERBOSE}, {@link Log#DEBUG},
     *                                  {@link Log#INFO}, {@link Log#WARN}, or {@link Log#ERROR}.
     * @since 1.3.0
     */
    @AnyThread
    public static void setLogLevel(@ZapicLogLevelDef final int logLevel) {
        if (logLevel < Log.VERBOSE || logLevel > Log.ERROR) {
            throw new IllegalArgumentException(
                    "logLevel must be greater than or equal to android.util.Log.VERBOSE and less than or equal to " +
                            "android.util.Log.ERROR");
        }

        if (Log.isLoggable(ZapicLog.TAG, Log.INFO)) {
            String logLevel2;
            switch (logLevel) {
                case Log.ERROR:
                    logLevel2 = "ERROR";
                    break;
                case Log.WARN:
                    logLevel2 = "WARN";
                    break;
                case Log.INFO:
                    logLevel2 = "INFO";
                    break;
                case Log.DEBUG:
                    logLevel2 = "DEBUG";
                    break;
                case Log.VERBOSE:
                default:
                    logLevel2 = "VERBOSE";
                    break;
            }

            Log.i(ZapicLog.TAG, String.format("Zapic SDK log level set to %s", logLevel2));
        }

        ZapicLog.logLevel = logLevel;
    }

    /**
     * Writes a {@link Log#DEBUG} message to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param message The message.
     */
    @AnyThread
    static void d(@NonNull final String tag, @NonNull final String message) {
        if (ZapicLog.logLevel <= Log.DEBUG) {
            Log.d(tag, message);
        }
    }

    /**
     * Writes a {@link Log#DEBUG} message to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param message The message format string.
     * @param args    The arguments referenced by the message format string.
     */
    @AnyThread
    static void d(@NonNull final String tag, @NonNull final String message, @Nullable Object... args) {
        if (ZapicLog.logLevel <= Log.DEBUG) {
            Log.d(tag, String.format(message, args));
        }
    }

    /**
     * Writes a {@link Log#DEBUG} message and an exception to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param t       The exception.
     * @param message The message.
     */
    @AnyThread
    static void d(@NonNull final String tag, @Nullable final Throwable t, @NonNull final String message) {
        if (ZapicLog.logLevel <= Log.DEBUG) {
            Log.d(tag, message, t);
        }
    }

    /**
     * Writes a {@link Log#DEBUG} message and an exception to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param t       The exception.
     * @param message The message format string.
     * @param args    The arguments referenced by the message format string.
     */
    @AnyThread
    static void d(
            @NonNull final String tag,
            @Nullable final Throwable t,
            @NonNull final String message,
            @Nullable Object... args) {
        if (ZapicLog.logLevel <= Log.DEBUG) {
            Log.d(tag, String.format(message, args), t);
        }
    }

    /**
     * Writes an {@link Log#ERROR} message to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param message The message.
     */
    @AnyThread
    static void e(@NonNull final String tag, @NonNull final String message) {
        if (ZapicLog.logLevel <= Log.ERROR) {
            Log.e(tag, message);
        }
    }

    /**
     * Writes an {@link Log#ERROR} message to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param message The message format string.
     * @param args    The arguments referenced by the message format string.
     */
    @AnyThread
    static void e(@NonNull final String tag, @NonNull final String message, @Nullable Object... args) {
        if (ZapicLog.logLevel <= Log.ERROR) {
            Log.e(tag, String.format(message, args));
        }
    }

    /**
     * Writes an {@link Log#ERROR} message and an exception to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param t       The exception.
     * @param message The message.
     */
    @AnyThread
    static void e(@NonNull final String tag, @Nullable final Throwable t, @NonNull final String message) {
        if (ZapicLog.logLevel <= Log.ERROR) {
            Log.e(tag, message, t);
        }
    }

    /**
     * Writes an {@link Log#ERROR} message and an exception to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param t       The exception.
     * @param message The message format string.
     * @param args    The arguments referenced by the message format string.
     */
    @AnyThread
    static void e(
            @NonNull final String tag,
            @Nullable final Throwable t,
            @NonNull final String message,
            @Nullable Object... args) {
        if (ZapicLog.logLevel <= Log.ERROR) {
            Log.e(tag, String.format(message, args), t);
        }
    }

    /**
     * Writes an {@link Log#INFO} message to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param message The message.
     */
    @AnyThread
    static void i(@NonNull final String tag, @NonNull final String message) {
        if (ZapicLog.logLevel <= Log.INFO) {
            Log.i(tag, message);
        }
    }

    /**
     * Writes an {@link Log#INFO} message to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param message The message format string.
     * @param args    The arguments referenced by the message format string.
     */
    @AnyThread
    static void i(@NonNull final String tag, @NonNull final String message, @Nullable Object... args) {
        if (ZapicLog.logLevel <= Log.INFO) {
            Log.i(tag, String.format(message, args));
        }
    }

    /**
     * Writes an {@link Log#INFO} message and an exception to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param t       The exception.
     * @param message The message.
     */
    @AnyThread
    static void i(@NonNull final String tag, @Nullable final Throwable t, @NonNull final String message) {
        if (ZapicLog.logLevel <= Log.INFO) {
            Log.i(tag, message, t);
        }
    }

    /**
     * Writes an {@link Log#INFO} message and an exception to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param t       The exception.
     * @param message The message format string.
     * @param args    The arguments referenced by the message format string.
     */
    @AnyThread
    static void i(
            @NonNull final String tag,
            @Nullable final Throwable t,
            @NonNull final String message,
            @Nullable Object... args) {
        if (ZapicLog.logLevel <= Log.INFO) {
            Log.i(tag, String.format(message, args), t);
        }
    }

    /**
     * Writes a {@link Log#VERBOSE} message to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param message The message.
     */
    @AnyThread
    static void v(@NonNull final String tag, @NonNull final String message) {
        if (ZapicLog.logLevel <= Log.VERBOSE) {
            Log.v(tag, message);
        }
    }

    /**
     * Writes a {@link Log#VERBOSE} message to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param message The message format string.
     * @param args    The arguments referenced by the message format string.
     */
    @AnyThread
    static void v(@NonNull final String tag, @NonNull final String message, @Nullable Object... args) {
        if (ZapicLog.logLevel <= Log.VERBOSE) {
            Log.v(tag, String.format(message, args));
        }
    }

    /**
     * Writes a {@link Log#VERBOSE} message and an exception to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param t       The exception.
     * @param message The message.
     */
    @AnyThread
    static void v(@NonNull final String tag, @Nullable final Throwable t, @NonNull final String message) {
        if (ZapicLog.logLevel <= Log.VERBOSE) {
            Log.v(tag, message, t);
        }
    }

    /**
     * Writes a {@link Log#VERBOSE} message and an exception to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param t       The exception.
     * @param message The message format string.
     * @param args    The arguments referenced by the message format string.
     */
    @AnyThread
    static void v(
            @NonNull final String tag,
            @Nullable final Throwable t,
            @NonNull final String message,
            @Nullable Object... args) {
        if (ZapicLog.logLevel <= Log.VERBOSE) {
            Log.v(tag, String.format(message, args), t);
        }
    }

    /**
     * Writes a {@link Log#WARN} message to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param message The message.
     */
    @AnyThread
    static void w(@NonNull final String tag, @NonNull final String message) {
        if (ZapicLog.logLevel <= Log.WARN) {
            Log.w(tag, message);
        }
    }

    /**
     * Writes a {@link Log#WARN} message to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param message The message format string.
     * @param args    The arguments referenced by the message format string.
     */
    @AnyThread
    static void w(@NonNull final String tag, @NonNull final String message, @Nullable Object... args) {
        if (ZapicLog.logLevel <= Log.WARN) {
            Log.w(tag, String.format(message, args));
        }
    }

    /**
     * Writes a {@link Log#WARN} message and an exception to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param t       The exception.
     * @param message The message.
     */
    @AnyThread
    static void w(@NonNull final String tag, @Nullable final Throwable t, @NonNull final String message) {
        if (ZapicLog.logLevel <= Log.WARN) {
            Log.w(tag, message, t);
        }
    }

    /**
     * Writes a {@link Log#WARN} message and an exception to the standard Android logs.
     *
     * @param tag     The source of the message.
     * @param t       The exception.
     * @param message The message format string.
     * @param args    The arguments referenced by the message format string.
     */
    @AnyThread
    static void w(
            @NonNull final String tag,
            @Nullable final Throwable t,
            @NonNull final String message,
            @Nullable Object... args) {
        if (ZapicLog.logLevel <= Log.WARN) {
            Log.w(tag, String.format(message, args), t);
        }
    }
}
