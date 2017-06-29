package com.zhy.sample_circlemenu.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class RoundButton extends android.support.v7.widget.AppCompatButton {
    final Paint paint = new Paint();
    protected boolean pressed = false;

    class C02391 implements OnTouchListener {
        C02391() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == 0) {
                RoundButton.this.pressed = true;
                RoundButton.this.invalidate();
            } else if (event.getAction() == 1) {
                RoundButton.this.pressed = false;
                RoundButton.this.invalidate();
            }
            return false;
        }
    }

    public RoundButton(Context context) {
        super(context);
        init(context);
    }

    public RoundButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoundButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        setOnTouchListener(new C02391());
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int color1 = Color.parseColor("#07ccc1");
        int color2 = Color.parseColor("#055450");
        if (this.pressed) {
            color1 = Color.parseColor("#01625c");
            color2 = Color.parseColor("#002725");
        }
        PointF center = new PointF(((float) getWidth()) * 0.5f, ((float) getHeight()) * 0.5f);
        float radius = ((float) getWidth()) * 0.5f;
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Style.FILL_AND_STROKE);
        this.paint.setColor(Color.parseColor("#40000000"));
        canvas.drawCircle(center.x, center.y, radius, this.paint);
        float radius2 = radius * 0.925f;
        this.paint.setColor(color1);
        canvas.drawCircle(center.x, center.y, radius2, this.paint);
        this.paint.setColor(color2);
        canvas.drawCircle(center.x, center.y, 0.88f * radius2, this.paint);
        this.paint.setColor(color1);
        float radius3 = radius2 * 0.76f;
        if (this.pressed) {
            radius3 = radius2 * 0.74f;
        }
        canvas.drawCircle(center.x, center.y, radius3, this.paint);
    }
}
