package com.zhy.sample_circlemenu.views;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import com.zhy.sample_circlemenu.R;


public class DisplayView extends FrameLayout {
    protected AutoResizeTextView backLabel;
    protected ObjectAnimator defaultTextAnimation;
    protected AutoResizeTextView defaultTextLabel;
    protected AutoResizeTextView frontLabel;

    public DisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DisplayView(Context context) {
        super(context);
        init(context);
    }

    public DisplayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.backLabel = (AutoResizeTextView) findViewById(R.id.backLabel);
        this.frontLabel = (AutoResizeTextView) findViewById(R.id.frontLabel);
        this.defaultTextLabel = (AutoResizeTextView) findViewById(R.id.defaultLabel);
        this.defaultTextLabel.setAlpha(0.0f);
        this.defaultTextLabel.setText(R.string.spin_the_wheel);
        this.defaultTextAnimation = ObjectAnimator.ofFloat(this.defaultTextLabel, "alpha", new float[]{1.0f, 0.4f});
        this.defaultTextAnimation.setDuration(500);
        this.defaultTextAnimation.setRepeatCount(-1);
        this.defaultTextAnimation.setRepeatMode(ValueAnimator.RESTART);
        this.defaultTextAnimation.setInterpolator(new LinearInterpolator());
    }

    public DisplayView setText(String newValue) {
        this.backLabel.setAlpha(0.3f);
        this.backLabel.setText(this.frontLabel.getText());
        this.frontLabel.setAlpha(1.0f);
        this.frontLabel.setText(newValue);
        this.backLabel.animate().alpha(0.0f).setDuration(200);
        this.defaultTextAnimation.cancel();
        this.defaultTextLabel.setAlpha(0.0f);
        return this;
    }

    public DisplayView showDefaultText() {
        this.defaultTextLabel.setAlpha(1.0f);
        this.frontLabel.setAlpha(0.0f);
        this.defaultTextAnimation.start();
        return this;
    }
}
