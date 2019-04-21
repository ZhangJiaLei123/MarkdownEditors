package com.blxt.markdowneditors.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.Gravity;

import com.blxt.markdowneditors.R;

/**
 * 带行号的 EditText
 * @author Zhang
 */
public class MyEditText extends AppCompatEditText {



    /** 是否绘画行号和分割隔线 */
    private boolean isDrawLine = true;

    private Paint line;
    private Paint line_row;
    private int textlines = 2;

    @SuppressLint("ResourceAsColor")
    public MyEditText(Context context, AttributeSet As){
        super( context, As);
        if(isDrawLine){
            setFocusable(true);
            line=new Paint();
            line.setColor(Color.GRAY);
            line.setStrokeWidth(2);

            line_row=new Paint();
            line_row.setColor(Color.GRAY);
            line_row.setStrokeWidth(4);

            int w = (int)getTextSize() * textlines;

            setPadding( w +  10,0,0,10);
            setGravity(Gravity.TOP);
        }

    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onDraw(final Canvas canvas)
    {

        if(isDrawLine){
            if(getLineCount() >= 1000){
                textlines = 3;
            }

            // 行号
            if(getText().toString().length()!=0){
                float y=0;
                Paint p=new Paint();
                p.setColor(R.color.colorPrimary);
                p.setTextSize(getTextSize());
                for(int l=0;l<getLineCount();l++){
                    y=((l+1)*getLineHeight())-(getLineHeight()/4);
                    canvas.drawText(String.valueOf(l+1),0,y,p);
                    canvas.save();
                }
            }

            // 分隔线
            int k=getLineHeight();
            int i=getLineCount();
         //   int w = (int)(SystemUtils.px2dip(getContext(), getTextSize()));
            int w = (int)getTextSize() * textlines;

            //竖线
            canvas.drawLine( w ,0, w ,getHeight()+(i*k),line);
            int y=(getLayout().getLineForOffset(getSelectionStart())+1)*k;
            //行标
            canvas.drawLine(0,y-1, w,y-1,line_row);
            canvas.save();
            canvas.restore();


        }

        super.onDraw(canvas);
    }


    public boolean isDrawLine() {
        return isDrawLine;
    }

    public void setDrawLine(boolean drawLine) {
        isDrawLine = drawLine;
    }
}
