package edu.uoregon.casls.aris_android.object_controllers;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by mtolly on 10/10/17.
 */

public class DialogOptionLayout extends RelativeLayout {
    public DialogOptionLayout(Context context) {
        super(context);
    }
    public DialogOptionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public DialogOptionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        // This is necessary to make sure the ARISWebView doesn't receive the touch event.
        return true;
    }
}
