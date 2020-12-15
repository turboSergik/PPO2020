package com.sergey.codeeditorPPO2020.custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.core.content.ContextCompat;

import com.sergey.codeeditorPPO2020.R;

public class SyntaxHighlightingEditText extends androidx.appcompat.widget.AppCompatEditText {
    private Paint paint;

    public SyntaxHighlightingEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(ContextCompat.getColor(context, R.color.numberLine));
        paint.setTextSize(40);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int baseline = getBaseline();
        for (int i = 0; i < getLineCount(); i++) {


            canvas.drawText(" " + (i+1) + " ", 5, baseline, paint);
            baseline += getLineHeight();
        }

//        canvas.drawLine(0, 0, getWidth(), getHeight(), paint);

        super.onDraw(canvas);
    }
}
