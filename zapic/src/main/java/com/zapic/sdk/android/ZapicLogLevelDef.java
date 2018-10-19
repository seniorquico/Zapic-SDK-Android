package com.zapic.sdk.android;

import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Kyle Dodson
 * @since 1.3.0
 */
@IntDef({Log.VERBOSE, Log.DEBUG, Log.INFO, Log.WARN, Log.ERROR})
@Retention(RetentionPolicy.SOURCE)
public @interface ZapicLogLevelDef {
}
