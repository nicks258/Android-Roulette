package com.zhy.sample_circlemenu.views;

public interface RotationTrackingViewListener {
    void onWheelAngleDidChanged(float f);

    void onWheelStartDescending();

    void onWheelStopDescending();

    void onWheelStopped(float f);

    void onWheelTouchBegan();
}
