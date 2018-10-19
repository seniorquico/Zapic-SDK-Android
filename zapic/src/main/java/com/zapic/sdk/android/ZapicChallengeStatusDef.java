package com.zapic.sdk.android;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Kyle Dodson
 * @since 1.3.0
 */
@IntDef({
        ZapicChallengeStatus.INVITED,
        ZapicChallengeStatus.IGNORED,
        ZapicChallengeStatus.ACCEPTED
})
@Retention(RetentionPolicy.SOURCE)
public @interface ZapicChallengeStatusDef {
}
