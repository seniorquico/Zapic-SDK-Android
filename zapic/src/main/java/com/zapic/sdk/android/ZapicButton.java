package com.zapic.sdk.android;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class ZapicButton extends Button {
    public ZapicButton(Context context) {
        this(context, null);
    }

    public ZapicButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyle);
    }

    public ZapicButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        super.setBackgroundResource(R.drawable.zapic_button);
    }
}
