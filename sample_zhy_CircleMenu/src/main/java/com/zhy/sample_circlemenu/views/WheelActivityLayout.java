package com.zhy.sample_circlemenu.views;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.zhy.sample_circlemenu.R;


public class WheelActivityLayout extends FrameLayout {
    private View displayView;
    private Button kickButton;
//    private View labelOptionsButton;
    private RotationTrackingView rotationTrackingView;
    private View shareButton;
    private AutoResizeTextView templateNameLabel;
//    private View templatesButton;
    private WheelCenterGlassView wheelCenterGlassView;
    private WheelGlassView wheelGlassView;
    private View wheelGroupView;
    private WheelSectionsView wheelSectionsView;
    private WheelSectionsView wheelSelectedSectionsView;

    public WheelActivityLayout(Context context) {
        this(context, null);
    }

    public WheelActivityLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelActivityLayout(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
        setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.wheelGroupView = findViewById(R.id.wheelGroupView);
        this.wheelSectionsView = (WheelSectionsView) findViewById(R.id.sectionsView);
        this.wheelSectionsView.useBitmap = Boolean.valueOf(true);
        this.wheelSelectedSectionsView = (WheelSectionsView) findViewById(R.id.selectedSectionsView);
        this.rotationTrackingView = (RotationTrackingView) findViewById(R.id.rotationTrackingView);
        this.wheelGlassView = (WheelGlassView) findViewById(R.id.wheelGlassView);
        this.wheelCenterGlassView = (WheelCenterGlassView) findViewById(R.id.wheelCenterGlassView);
        this.kickButton = (Button) findViewById(R.id.kickButton);
        this.displayView = findViewById(R.id.displayView);
        this.templateNameLabel = (AutoResizeTextView) findViewById(R.id.templateNameLabel);
//        this.templatesButton = findViewById(R.id.templatesButton);
        this.shareButton = findViewById(R.id.shareButton);
//        this.labelOptionsButton = findViewById(R.id.labelOptionsButton);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        layoutViews(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void layoutViews(int w, int h) {
        Log.d("-->", "layoutViews() called with: w = [" + w + "], h = [" + h + "]");
        int padding = Math.round(((float) w) * 0.02f);
        int effectiveDiameter = Math.round(Math.min((float) (w - (padding * 2)), ((float) h) * 0.7f));
        float effectiveRadius = ((float) effectiveDiameter) * 0.5f;
        int bottom = Math.round(((float) (h - effectiveDiameter)) * 0.135f);
        int minBottom = padding * 2;
        if (bottom < minBottom) {
            bottom = minBottom;
        }
        PointF wheelCenter = new PointF(((float) w) * 0.5f, ((float) ((h - effectiveDiameter) - bottom)) + effectiveRadius);
        int wheelRadius = (int) (Math.sqrt(Math.pow((double) wheelCenter.x, 2.0d) + Math.pow((double) wheelCenter.y, 2.0d)) + 1.0d);
        LayoutParams params = (LayoutParams) this.wheelGroupView.getLayoutParams();
        params.width = wheelRadius * 2;
        params.height = wheelRadius * 2;
        params.setMarginStart((int) (wheelCenter.x - ((float) wheelRadius)));
        params.topMargin = (int) (wheelCenter.y - ((float) wheelRadius));
        this.wheelGroupView.setLayoutParams(params);
        this.wheelGlassView.setMetrics(wheelCenter, effectiveRadius);
        this.wheelSectionsView.setEffectiveRadius(effectiveRadius);
        this.wheelSelectedSectionsView.setEffectiveRadius(effectiveRadius);
        LayoutParams params2 = (LayoutParams) this.rotationTrackingView.getLayoutParams();
        params2.width = w;
        params2.height = w;
        params2.setMarginStart(0);
        params2.topMargin = (int) (wheelCenter.y - (((float) w) * 0.5f));
        this.rotationTrackingView.setLayoutParams(params2);
        float innerRadius = effectiveRadius * 0.55f;
        LayoutParams params3 = (LayoutParams) this.wheelCenterGlassView.getLayoutParams();
        params3.width = ((int) innerRadius) * 2;
        params3.height = ((int) innerRadius) * 2;
        params3.setMarginStart(Math.round((((float) w) - (2.0f * innerRadius)) * 0.5f));
        params3.topMargin = (int) (wheelCenter.y - innerRadius);
        this.wheelCenterGlassView.setLayoutParams(params3);
        float buttonRadius = effectiveRadius * 0.313f;
        params = (LayoutParams) this.kickButton.getLayoutParams();
        params.width = ((int) buttonRadius) * 2;
        params.height = ((int) buttonRadius) * 2;
        params.setMarginStart((int) (wheelCenter.x - buttonRadius));
        params.topMargin = (int) (wheelCenter.y - buttonRadius);
        this.kickButton.setLayoutParams(params);
        int horizPadding = Math.round(((float) w) * 0.07f);
        params = (LayoutParams) this.displayView.getLayoutParams();
        params.width = w - (horizPadding * 2);
        params.height = Math.round(((float) w) * 0.12f);
        params.setMarginStart(horizPadding);
        params.topMargin = Math.round(((wheelCenter.y - effectiveRadius) * 0.64f) - (((float) params.height) * 0.5f));
        this.displayView.setLayoutParams(params);
        params = (LayoutParams) this.templateNameLabel.getLayoutParams();
        params.width = w - (horizPadding * 2);
        params.height = Math.round(((float) w) * 0.06f);
        params.setMarginStart(horizPadding);
        params.topMargin = Math.round(((wheelCenter.y - effectiveRadius) * 0.25f) - (((float) params.height) * 0.5f));
        this.templateNameLabel.setLayoutParams(params);
        int roundButtonPadding = (int) Math.round(((double) w) * 0.03d);
//        params = (LayoutParams) this.templatesButton.getLayoutParams();
        params.bottomMargin = Math.round(((float) h) * 0.015f);
        params.setMarginEnd(Math.round(((float) w) * 0.025f));
//        this.templatesButton.setLayoutParams(params);
//        this.templatesButton.setPadding(roundButtonPadding, roundButtonPadding, roundButtonPadding, roundButtonPadding);
        params = (LayoutParams) this.shareButton.getLayoutParams();
        params.bottomMargin = Math.round(((float) h) * 0.015f);
        params.setMarginStart(Math.round(((float) w) * 0.025f));
        this.shareButton.setLayoutParams(params);
        this.shareButton.setPadding(roundButtonPadding, roundButtonPadding, roundButtonPadding, roundButtonPadding);
//        params = (LayoutParams) this.labelOptionsButton.getLayoutParams();
        params.topMargin = Math.round(((float) h) * 0.015f);
        params.setMarginEnd(Math.round(((float) w) * 0.025f));
//        this.labelOptionsButton.setLayoutParams(params);
//        this.labelOptionsButton.setPadding(roundButtonPadding, roundButtonPadding, roundButtonPadding, roundButtonPadding);
    }
}
