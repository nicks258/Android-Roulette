package com.zhy.sample_circlemenu.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zhy.sample_circlemenu.models.Wheel;
import com.zhy.sample_circlemenu.models.WheelSection;


public class WheelSectionsView extends View {
    private Paint borderPaint = new Paint();
    private Bitmap bufferedImage;
    private PointF centerPoint;
    private float effectiveRadius = -1.0f;
    private Canvas innerCanvas = new Canvas();
    private float innerRadius;
    private Typeface labelFont;
    private float radius;
    private float selectedSectionAngle = -1.0f;
    public Boolean useBitmap = Boolean.valueOf(false);
    private Wheel wheel;

    public WheelSectionsView(Context context) {
        super(context);
        init(context);
    }

    public WheelSectionsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WheelSectionsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.labelFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/roboto_condensed_bold.ttf");
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.useBitmap.booleanValue()) {
            drawOnBitmap(null);
            canvas.drawBitmap(this.bufferedImage, 0.0f, 0.0f, null);
            return;
        }
        drawOnBitmap(canvas);
    }

    public WheelSectionsView updateView() {
        this.bufferedImage = null;
        invalidate();
        return this;
    }

    public WheelSectionsView setWheel(Wheel aWheel) {
        this.wheel = aWheel;
        updateView();
        return this;
    }

    public WheelSectionsView setEffectiveRadius(float er) {
        if (er != this.effectiveRadius) {
            this.effectiveRadius = er;
            initMetrics();
            updateView();
        }
        return this;
    }

    public WheelSectionsView selectAtAngle(float angle) {
        this.selectedSectionAngle = angle;
        updateView();
        return this;
    }

    private void drawOnBitmap(Canvas canvas) {
        if ((!this.useBitmap.booleanValue() || this.bufferedImage == null) && this.wheel != null) {
            int i;
            WheelSection section;
            if (this.useBitmap.booleanValue()) {
                Log.d("WheelSectionsView", "drawing to bitmap");
                this.bufferedImage = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
                canvas = this.innerCanvas;
                canvas.setBitmap(this.bufferedImage);
            }
            float sectionAngle = 360.0f / ((float) this.wheel.sections.size());
            float currentAngle = 0.0f;
            WheelSection selectedSection = null;
            if (this.selectedSectionAngle != -1.0f) {
                selectedSection = this.wheel.getSectionAtAngle(this.selectedSectionAngle);
            }
            int disabledBackgroundColor = Color.parseColor("#404040");
            for (i = 0; i < this.wheel.sections.size(); i++) {
                section = (WheelSection) this.wheel.sections.get(i);
                if (this.selectedSectionAngle == -1.0f) {
                    int backgroundColor;
                    if (section.enabled.booleanValue()) {
                        backgroundColor = section.colorSection.backgroundColor;
                    } else {
                        backgroundColor = disabledBackgroundColor;
                    }
                    drawSection(canvas, currentAngle, currentAngle + sectionAngle, backgroundColor, section.enabled.booleanValue() ? section.colorSection.fontColor : Color.argb(64, 255, 255, 255), section.label, -1, -1);
                } else if (section == selectedSection) {
                    drawSection(canvas, currentAngle, currentAngle + sectionAngle, Color.parseColor("#0f63ce"), -1, section.label, -1, -1);
                }
                currentAngle += sectionAngle;
            }
            if (this.selectedSectionAngle == -1.0f) {
                currentAngle = 0.0f;
                for (i = 0; i < this.wheel.sections.size(); i++) {
                    section = (WheelSection) this.wheel.sections.get(i);
                    if (section.colorSection.borderColor != -1) {
                        drawBorder(canvas, currentAngle, section.colorSection.borderColor);
                    }
                    currentAngle += sectionAngle;
                }
            }
        }
    }

    private void initMetrics() {
        this.radius = ((float) Math.min(getWidth(), getHeight())) * 0.5f;
        if (this.effectiveRadius == -1.0f) {
            this.effectiveRadius = this.radius;
        }
        this.innerRadius = this.effectiveRadius * 0.425f;
        this.centerPoint = new PointF(((float) getWidth()) * 0.5f, ((float) getHeight()) * 0.5f);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        initMetrics();
        updateView();
    }

    private void drawSection(Canvas canvas, float startAngle, float endAngle, int sectionColor, int fontColor, String labelStr, int borderLeft, int borderRight) {
        Paint paint1 = new Paint();
        paint1.setStyle(Style.FILL);
        paint1.setColor(sectionColor);
        paint1.setStrokeWidth(0.0f);
        paint1.setFlags(1);
        float x = Math.max(0.0f, ((float) (getWidth() - getHeight())) * 0.5f);
        float y = Math.max(0.0f, ((float) (getHeight() - getWidth())) * 0.5f);
        canvas.drawArc(new RectF(x, y, (this.radius * 2.0f) + x, (this.radius * 2.0f) + y), startAngle, endAngle - startAngle, true, paint1);
        labelStr = labelStr.toUpperCase();
        Paint textPaint = new Paint();
        textPaint.setColor(fontColor);
        textPaint.setStyle(Style.FILL);
        textPaint.setFlags(1);
        textPaint.setTypeface(this.labelFont);
        textPaint.setTextSize(Math.min(Math.min(((float) ((((double) (this.innerRadius * 2.0f)) * 3.141592653589793d) / ((double) this.wheel.sections.size()))) * 0.75f, this.effectiveRadius * 0.1f), getTextSizeForWidth(labelStr, (this.effectiveRadius - this.innerRadius) * 0.8f, textPaint)));
        Rect rectText = new Rect();
        textPaint.getTextBounds(labelStr, 0, labelStr.length(), rectText);
        PointF labelCenter = new PointF((float) (((double) this.centerPoint.x) + (((double) ((this.effectiveRadius + this.innerRadius) * 0.5f)) * Math.cos(Math.toRadians((double) ((startAngle + endAngle) * 0.5f))))), (float) (((double) this.centerPoint.y) + (((double) ((this.effectiveRadius + this.innerRadius) * 0.5f)) * Math.sin(Math.toRadians((double) ((startAngle + endAngle) * 0.5f))))));
        canvas.save();
        canvas.rotate((startAngle + endAngle) * 0.5f, labelCenter.x, labelCenter.y);
        canvas.drawText(labelStr, labelCenter.x - (((float) rectText.width()) * 0.5f), labelCenter.y + (((float) rectText.height()) * 0.5f), textPaint);
        canvas.restore();
    }

    private void drawBorder(Canvas canvas, float angle, int color) {
        this.borderPaint.setStyle(Style.STROKE);
        this.borderPaint.setColor(color);
        this.borderPaint.setStrokeWidth(1.0f);
        this.borderPaint.setFlags(1);
        canvas.drawLine(this.centerPoint.x + 0.0f, this.centerPoint.y + 0.0f, (this.centerPoint.x + (this.radius * ((float) Math.cos(Math.toRadians((double) angle))))) + 0.0f, (this.centerPoint.y + (this.radius * ((float) Math.sin(Math.toRadians((double) angle))))) + 0.0f, this.borderPaint);
    }

    private float getTextSizeForWidth(String text, float desiredWidth, Paint paint) {
        float testTextSize = (float) ((6.283185307179586d * ((double) this.radius)) / ((double) this.wheel.sections.size()));
        float originalTextSize = paint.getTextSize();
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        float desiredTextSize = (testTextSize * desiredWidth) / ((float) bounds.width());
        paint.setTextSize(originalTextSize);
        return desiredTextSize;
    }


}
