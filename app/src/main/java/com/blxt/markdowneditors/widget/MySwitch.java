package com.blxt.markdowneditors.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Switch;

/**
 *
 */
public class MySwitch extends Switch {

    public MySwitch(Context context) {
        super(context);
        Log.i("按钮1","text:" + getText() + "tag:" + getTag() + "Id:" + getId() + "isChecked:" + isChecked());
    }

    public MySwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("按钮2","text:" + getText() + "tag:" + getTag() + "Id:" + getId() + "isChecked:" + isChecked());
    }

    public MySwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.i("按钮3","text:" + getText() + "tag:" + getTag() + "Id:" + getId() + "isChecked:" + isChecked());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MySwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        Log.i("按钮4","text:" + getText() + "tag:" + getTag() + "Id:" + getId() + "isChecked:" + isChecked());
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 应为点击事件还没有传递到,所以这里取反
        Log.i("按钮", "isChecked:" + !isChecked());
        return super.onTouchEvent(ev);
    }

}

