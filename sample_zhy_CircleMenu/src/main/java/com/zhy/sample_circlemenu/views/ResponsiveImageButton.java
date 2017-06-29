package com.zhy.sample_circlemenu.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class ResponsiveImageButton extends ImageButton {

    protected class ResponsiveImageButtonDrawable extends LayerDrawable {
        protected int disabledAlpha = 85;
        protected int pressedAlpha = 128;

        public ResponsiveImageButtonDrawable(Drawable d) {
            super(new Drawable[]{d});
        }

        protected boolean onStateChange(int[] states) {
            boolean enabled = false;
            boolean pressed = false;
            for (int state : states) {
                if (state == 16842910) {
                    enabled = true;
                } else if (state == 16842919) {
                    pressed = true;
                }
            }
            mutate();
            if (enabled && pressed) {
                setAlpha(this.pressedAlpha);
            } else if (enabled) {
                setAlpha(255);
            } else {
                setAlpha(this.disabledAlpha);
            }
            invalidateSelf();
            return super.onStateChange(states);
        }

        public boolean isStateful() {
            return true;
        }
    }

    public ResponsiveImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ResponsiveImageButton(Context context) {
        super(context);
        init(context);
    }

    public ResponsiveImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
    }

    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(new ResponsiveImageButtonDrawable(drawable));
    }
}
