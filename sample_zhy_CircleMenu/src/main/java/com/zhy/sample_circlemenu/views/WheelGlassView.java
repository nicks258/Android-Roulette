package com.zhy.sample_circlemenu.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Path.FillType;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;

public class WheelGlassView extends View {
    private Bitmap bufferedImage;
    private Canvas canvas = new Canvas();
    protected PointF center;
    protected boolean metricsSet;
    protected float radius;

    public WheelGlassView(Context context) {
        super(context);
        init(context);
    }

    public WheelGlassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WheelGlassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
    }

    public WheelGlassView setMetrics(PointF aCenter, float aRadius) {
        if (!(this.center == aCenter && this.radius == aRadius)) {
            this.center = aCenter;
            this.radius = aRadius;
            this.metricsSet = true;
            invalidate();
        }
        return this;
    }

    protected void drawOnBitmap() {
        if (this.bufferedImage == null && this.metricsSet) {
            int width = getWidth();
            int height = getHeight();
            this.bufferedImage = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            this.canvas.setBitmap(this.bufferedImage);
            Path path = new Path();
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Style.FILL_AND_STROKE);
            paint.setShader(new RadialGradient(this.center.x, this.center.y, this.center.y, Color.argb(100, 0, 0, 0), Color.argb(240, 0, 0, 0), TileMode.CLAMP));
            path.moveTo(0.0f, 0.0f);
            path.lineTo((float) width, 0.0f);
            path.lineTo((float) width, (float) height);
            path.lineTo(0.0f, (float) height);
            path.close();
            path.addCircle(this.center.x, this.center.y, this.radius, Direction.CCW);
            path.setFillType(FillType.EVEN_ODD);
            this.canvas.drawPath(path, paint);
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawOnBitmap();
        canvas.drawBitmap(this.bufferedImage, 0.0f, 0.0f, null);
    }
}
