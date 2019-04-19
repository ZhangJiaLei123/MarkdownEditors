package com.blxt.markdowneditors.widget;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Switch;

import com.bigbai.mfileutils.spControl.FalBoolean;
import com.blxt.markdowneditors.view.MainActivity;

/**
 * 绑定 SharedPreferences 的Switch开关,
 * 需要在MainActivity中进行一下初始化
 * SharedPreferences SP = getSharedPreferences(this.getPackageName(), MODE_PRIVATE);
 * @author Zhang
 */
public class MySwitch extends Switch {

    /** 绑定的数据 */
    FalBoolean falData;

    public MySwitch(Context context) {
        super(context);
        initData(isChecked());
    }

    public MySwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(isChecked());
    }

    public MySwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(isChecked());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MySwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initData(isChecked());
    }


    /**
     * 初始化 FalBoolean 数据
     * @param isCheck
     */
    private void initData(boolean isCheck){
        if (getTag() == null || getTag().toString().trim().length() <= 0){
            falData = new FalBoolean(MainActivity.SP,getId() + "",isCheck);
        }
        else{
            falData = new FalBoolean(MainActivity.SP,getTag().toString() ,isCheck);
        }
        setChecked(falData.getValue());
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // 应为点击事件还没有传递到,所以这里取反
        falData.setValue(!isChecked());
        return super.onTouchEvent(ev);
    }

    public FalBoolean getFalData() {
        return falData;
    }
}

