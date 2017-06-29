package com.zhy.sample_circlemenu.models;

import org.json.JSONException;
import org.json.JSONObject;

public class WheelTemplateLabel {
    public String name;

    public WheelTemplateLabel(String n) {
        this.name = n;
    }

    public WheelTemplateLabel(JSONObject json) {
        this.name = json.optString("name");
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        return json;
    }
}
