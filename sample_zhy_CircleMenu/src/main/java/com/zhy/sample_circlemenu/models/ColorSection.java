package com.zhy.sample_circlemenu.models;

import android.graphics.Color;

import org.json.JSONObject;

public class ColorSection {
    public int backgroundColor;
    public int borderColor = -1;
    public int fontColor;

    public ColorSection(JSONObject json) {
        this.fontColor = Color.parseColor(json.optString("fontColor"));
        this.backgroundColor = Color.parseColor(json.optString("backgroundColor"));
        String borderColorStr = json.optString("borderColor");
        if (borderColorStr != "") {
            this.borderColor = Color.parseColor(borderColorStr);
        }
    }
}
