package com.grum.raphael.projectmanagerclient;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.TimePicker;

/**
 * Created by Raphael on 08.11.2017.
 */

public class CustomTimePicker extends TimePicker {

    public CustomTimePicker(Context context) {
        super(context);
    }

    public CustomTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ViewParent parent = getParent();
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
        return false;
    }
}
