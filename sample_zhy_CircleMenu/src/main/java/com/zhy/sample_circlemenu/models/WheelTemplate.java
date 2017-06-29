package com.zhy.sample_circlemenu.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class WheelTemplate {
    public int colorSchemeId;
    public ArrayList<WheelTemplateLabel> labels;
    public String name;
    public int predefinedFlag;
    public final int version;
    public String wid;

    public WheelTemplate() {
        this.colorSchemeId = 0;
        this.version = 2;
        this.predefinedFlag = 0;
        this.wid = generateId();
    }

    public WheelTemplate(JSONObject json) {
        this.colorSchemeId = 0;
        this.version = 2;
        this.predefinedFlag = 0;
        this.wid = json.optString("wid");
        if (this.wid.equals("")) {
            this.wid = generateId();
            Log.d("-->", "Generated id is " + this.wid);
        }
        this.name = json.optString("name");
        this.colorSchemeId = json.optInt("colorScheme");
        this.predefinedFlag = json.optInt("pd", 1);
        JSONArray labelArray = json.optJSONArray("labels");
        this.labels = new ArrayList();
        if (labelArray != null) {
            for (int i = 0; i < labelArray.length(); i++) {
                JSONObject labelObj = labelArray.optJSONObject(i);
                if (labelObj != null) {
                    this.labels.add(new WheelTemplateLabel(labelObj));
                }
            }
        }
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("wid", this.wid);
            json.put("name", this.name);
            json.put("colorScheme", this.colorSchemeId);
            json.put("version", 2);
            json.put("pd", this.predefinedFlag);
            JSONArray labelsArray = new JSONArray();
            for (int i = 0; i < this.labels.size(); i++) {
                labelsArray.put(((WheelTemplateLabel) this.labels.get(i)).toJSON());
            }
            json.put("labels", labelsArray);
        } catch (JSONException e) {
        }
        return json;
    }

    public Boolean isPredefined() {
        return Boolean.valueOf(this.predefinedFlag != 0);
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }
}
