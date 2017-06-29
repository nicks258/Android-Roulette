package com.zhy.sample_circlemenu.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;

import java.util.ArrayList;
import java.util.Iterator;

public class RotationTrackingView extends View {
    private Boolean _cancelVelocityTracking = Boolean.valueOf(false);
    private double _currentVelocity = 0.0d;
    private Handler _interiaTimerHandler;
    private Runnable _interiaTimerRunnable;
    private Boolean _isDescending = Boolean.valueOf(false);
    private Boolean _isDragging = Boolean.valueOf(false);
    private Boolean _justStoppedDragged = Boolean.valueOf(false);
    private long _lastMoved = 0;
    private double _lastTouchedAngle = 0.0d;
    private double _lastTrackedAngle = 0.0d;
    private long _minMoveTime = 20;
    private Boolean _moveEventTracked = Boolean.valueOf(false);
    private int _passedLoops = 0;
    private View _rotatableView;
    private Boolean _stoppedByAction = Boolean.valueOf(false);
    private Handler _trackingTimerHandler;
    private Runnable _trackingTimerRunnable;
    ArrayList<RotationTrackingViewListener> listeners = new ArrayList();

    class C02361 implements Runnable {
        C02361() {
        }

        public void run() {
            RotationTrackingView.this._trackingTimerHandler.postDelayed(this, 20);
            RotationTrackingView.this._trackVelocity();
        }
    }

    class C02372 implements Runnable {
        C02372() {
        }

        public void run() {
            RotationTrackingView.this._interiaTimerHandler.postDelayed(this, 20);
            RotationTrackingView.this._interia();
        }
    }

    public RotationTrackingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RotationTrackingView(Context context) {
        super(context);
        init(context);
    }

    public RotationTrackingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this._trackingTimerHandler = new Handler();
        this._interiaTimerHandler = new Handler();
        this._trackingTimerRunnable = new C02361();
        this._interiaTimerRunnable = new C02372();
    }

    public void setRotatableView(View rotatableView) {
        this._rotatableView = rotatableView;
    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case 0:
                _onTouchBegan(event);
                break;
            case 1:
                _onTouchEnded(event);
                break;
            case 2:
                _onTouchMoved(event);
                break;
        }
        return true;
    }

    public void kickRotatableViewWitSpeed(double w0, Boolean useRandom, Boolean increase) {
        if (increase.booleanValue()) {
            w0 += this._currentVelocity;
        }
        this._currentVelocity = w0;
        if (useRandom.booleanValue()) {
            this._currentVelocity += (double) (((float) Math.random()) * 4.2f);
        }
        if (this._currentVelocity != 0.0d) {
            this._interiaTimerHandler.removeCallbacks(this._interiaTimerRunnable);
            this._interiaTimerHandler.postDelayed(this._interiaTimerRunnable, 0);
            this._isDescending = Boolean.valueOf(true);
            triggerEvent("onWheelStartDescending");
        }
    }

    public void kickRotatableView() {
        kickRotatableViewWitSpeed(13.0d, Boolean.valueOf(true), Boolean.valueOf(true));
    }

    public float getCurrentAngle() {
        float angle = 270.0f - this._rotatableView.getRotation();
        if (angle >= 360.0f || angle <= -360.0f) {
            angle -= ((float) Math.floor((double) (angle / 360.0f))) * 360.0f;
        }
        if (angle < 0.0f) {
            return angle + 360.0f;
        }
        return angle;
    }

    public void setCurrentAngle(float angle, boolean animated, final AnimationListener listener) {
        final float tAngle = angle;
        float diff = getCurrentAngle() - angle;
        if (diff > 180.0f) {
            diff -= 360.0f;
        } else if (diff < -180.0f) {
            diff += 360.0f;
        }
        if (animated) {
            RotateAnimation rotate = new RotateAnimation(0.0f, diff, 1, 0.5f, 1, 0.5f);
            rotate.setDuration(1500);
            rotate.setInterpolator(new AccelerateDecelerateInterpolator());
            rotate.setAnimationListener(new AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    RotationTrackingView.this._rotatableView.setRotation(270.0f - tAngle);
                    listener.onAnimationEnd(animation);
                }

                public void onAnimationRepeat(Animation animation) {
                }
            });
            this._rotatableView.startAnimation(rotate);
            return;
        }
        this._rotatableView.setRotation(270.0f - angle);
    }

    public void addEventListener(RotationTrackingViewListener listener) {
        this.listeners.add(listener);
    }

    public void stop() {
        this._currentVelocity = 0.0d;
        this._interiaTimerHandler.removeCallbacks(this._interiaTimerRunnable);
        triggerEvent("onWheelStopDescending");
        this._isDescending = Boolean.valueOf(false);
    }

    protected void triggerEvent(String event) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            RotationTrackingViewListener listener = (RotationTrackingViewListener) it.next();
            Object obj = -1;
            int value = -1;
            switch (event.hashCode()) {
                case -2141794146:
                    if (event.equals("onWheelStartDescending")) {
                        obj = 3;
                        value = 3;
                        Log.i("onWheelStartDescending","RotationTrackingView");
                        break;
                    }
                    break;
                case 731876721:
                    if (event.equals("onWheelStopped")) {
                        value = 1;
                        Log.i("onWheelStopped","RotationTrackingView");
                        obj = 1;
                        break;
                    }
                    break;
                case 901577228:
                    if (event.equals("onWheelAngleDidChanged")) {
                        value = 2;
                        Log.i("onWheelAngleDidChanged","RotationTrackingView");
                        obj = 2;
                        break;
                    }
                    break;
                case 994314638:

                    if (event.equals("onWheelTouchBegan")) {
                        value = 0;
                        Log.i("onWheelTouchBegan","RotationTrackingView");
                        obj = null;
                        break;
                    }
                    break;
                case 1959680118:
                    if (event.equals("onWheelStopDescending")) {
                        Log.i("onWheelStopDescending","RotationTrackingView");
                        value = 4;
                        obj = 4;
                        break;
                    }
                    break;
            }
            switch (value) {

                case 0:
                    listener.onWheelTouchBegan();
                    break;
                case  1:
                    listener.onWheelStopped(getCurrentAngle());
                    break;
                case 2:
                    listener.onWheelAngleDidChanged(getCurrentAngle());
                    break;
                case 3:
                    listener.onWheelStartDescending();
                    break;
                case 4:
                    listener.onWheelStopDescending();
                    break;
                default:
                    break;
            }
        }
    }

    protected void _onTouchBegan(MotionEvent event) {
        if (isEnabled()) {
            this._isDragging = Boolean.valueOf(true);
            this._currentVelocity = 0.0d;
            this._lastTouchedAngle = _getAngleFromTouchToCenter(event);
            this._lastTrackedAngle = (double) this._rotatableView.getRotation();
            this._moveEventTracked = Boolean.valueOf(true);
            this._trackingTimerHandler.postDelayed(this._trackingTimerRunnable, 0);
            triggerEvent("onWheelTouchBegan");
        }
    }

    protected void _onTouchMoved(MotionEvent event) {
        if (isEnabled()) {
            long _now = System.currentTimeMillis();
            if (_now - this._lastMoved >= this._minMoveTime) {
                this._lastMoved = _now;
                double currentAngle = _getAngleFromTouchToCenter(event);
                if (this._rotatableView != null) {
                    this._rotatableView.setRotation((float) ((((double) this._rotatableView.getRotation()) + this._lastTouchedAngle) - currentAngle));
                }
                this._lastTouchedAngle = currentAngle;
                this._moveEventTracked = Boolean.valueOf(true);
            }
        }
    }

    protected void _onTouchEnded(MotionEvent event) {
        if (isEnabled()) {
            this._isDragging = Boolean.valueOf(false);
            this._justStoppedDragged = Boolean.valueOf(true);
        }
    }

    protected double _getAngleFromTouchToCenter(MotionEvent event) {
        double angle = -Math.atan2((double) (event.getY() - (((float) getHeight()) * 0.5f)), (double) (event.getX() - (((float) getWidth()) * 0.5f)));
        if (angle < 0.0d) {
            angle += 6.283185307179586d;
        }
        return Math.toDegrees(angle);
    }

    protected void _trackVelocity() {
        if (this._isDragging.booleanValue()) {
            if (!this._moveEventTracked.booleanValue()) {
                this._passedLoops++;
                return;
            } else if (this._rotatableView != null) {
                double currentAngle = (double) this._rotatableView.getRotation();
                if (currentAngle < 0.0d) {
                    currentAngle += 360.0d;
                }
                double angleDiff = currentAngle - this._lastTrackedAngle;
                if (angleDiff > 180.0d) {
                    angleDiff -= 360.0d;
                } else if (angleDiff < -180.0d) {
                    angleDiff += 360.0d;
                }
                this._currentVelocity = (Math.toRadians(angleDiff) * 50.0d) * ((double) (this._passedLoops + 1));
                this._lastTrackedAngle = currentAngle;
                Log.d("-->", "_currentVelocity " + this._currentVelocity + "  angleDiff " + angleDiff);
                triggerEvent("onWheelAngleDidChanged");
            } else {
                return;
            }
        } else if (this._cancelVelocityTracking.booleanValue()) {
            this._cancelVelocityTracking = Boolean.valueOf(false);
            this._trackingTimerHandler.removeCallbacks(this._trackingTimerRunnable);
        } else {
            kickRotatableViewWitSpeed(this._currentVelocity, Boolean.valueOf(false), Boolean.valueOf(false));
            this._trackingTimerHandler.removeCallbacks(this._trackingTimerRunnable);
        }
        this._moveEventTracked = Boolean.valueOf(false);
        this._passedLoops = 0;
    }

    protected void _interia() {
        if (this._isDragging.booleanValue()) {
            stop();
            return;
        }
        this._currentVelocity *= 0.9829999804496765d;
        if (this._currentVelocity > 0.05d || this._currentVelocity < -0.05d) {
            this._rotatableView.setRotation((float) (((double) this._rotatableView.getRotation()) + Math.toDegrees(this._currentVelocity * 0.02d)));
            triggerEvent("onWheelAngleDidChanged");
        } else {
            this._currentVelocity = 0.0d;
            this._interiaTimerHandler.removeCallbacks(this._interiaTimerRunnable);
            triggerEvent("onWheelStopDescending");
            this._isDescending = Boolean.valueOf(false);
            _onWheelStopped();
        }
        this._justStoppedDragged = Boolean.valueOf(false);
    }

    protected void _onWheelStopped() {
        if (!this._justStoppedDragged.booleanValue()) {
            triggerEvent("onWheelStopped");
        }
    }
}
