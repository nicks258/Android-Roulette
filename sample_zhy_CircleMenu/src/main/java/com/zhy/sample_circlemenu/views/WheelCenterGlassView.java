package com.zhy.sample_circlemenu.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.FillType;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;

public class WheelCenterGlassView extends View {
    public WheelCenterGlassView(Context context) {
        super(context);
        init(context);
    }

    public WheelCenterGlassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WheelCenterGlassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        setLayerType(1, null);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Path path = new Path();
        Paint paint = new Paint();
        PointF center = new PointF(((float) getWidth()) * 0.5f, ((float) getHeight()) * 0.5f);
        float radius = (((float) getWidth()) * 0.5f) * 0.8f;
        paint.setAntiAlias(true);
        paint.setStyle(Style.FILL_AND_STROKE);
        paint.setShadowLayer(((float) getWidth()) * 0.013f, 0.0f, ((float) getWidth()) * 0.0105f, Color.argb(128, 0, 0, 0));
        paint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, (float) getHeight(), Color.argb(140, 0, 0, 0), Color.argb(165, 0, 0, 0), TileMode.MIRROR));
        path.addArc(new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius), 285.0f, 330.0f);
        path.lineTo(center.x, 0.0f);
        path.close();
        path.setFillType(FillType.EVEN_ODD);
        canvas.drawPath(path, paint);
    }
}
