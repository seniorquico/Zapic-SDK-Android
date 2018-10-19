package com.zapic.sdk.android;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Kyle Dodson
 * @since 1.3.0
 */
@StringDef({
        ZapicPage.CHALLENGE_LIST,
        ZapicPage.COMPETITION_LIST,
        ZapicPage.CREATE_CHALLENGE,
        ZapicPage.LOGIN,
        ZapicPage.PROFILE,
        ZapicPage.STAT_LIST
})
@Retention(RetentionPolicy.SOURCE)
public @interface ZapicPageDef {
}
