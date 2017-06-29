package com.zhy.sample_circlemenu.views;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class AutoResizeTextView extends android.support.v7.widget.AppCompatTextView {
    protected float minTextSize;

    public AutoResizeTextView(Context context) {
        this(context, null);
    }

    public AutoResizeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoResizeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.minTextSize = 0.0f;
    }

    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            adjustTextSize();
        }
    }

    public void setMinTextSize(float t) {
        this.minTextSize = t;
    }

    protected void adjustTextSize() {
        if (getWidth() != 0 && getHeight() != 0) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            float testTextSize = (float) getHeight();
            Paint paint = new Paint();
            paint.setTypeface(getTypeface());
            paint.setTextSize(testTextSize);
            Rect bounds = new Rect();
            String t = (String) getText();
            paint.getTextBounds(t, 0, t.length(), bounds);
            if (bounds.width() < getWidth()) {
                setTextSize((testTextSize / metrics.density) * 0.86f);
                return;
            }
            float desiredTextSize = (((float) getWidth()) * testTextSize) / ((float) bounds.width());
            if (this.minTextSize > 0.0f && desiredTextSize < this.minTextSize) {
                desiredTextSize = this.minTextSize;
            }
            setTextSize((desiredTextSize / metrics.density) * 0.86f);
        }
    }
}
