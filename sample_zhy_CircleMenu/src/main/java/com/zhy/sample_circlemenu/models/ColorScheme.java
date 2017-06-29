package com.zhy.sample_circlemenu.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ColorScheme {
    public ArrayList<ColorSection> colorSections = new ArrayList();
    public int id;
    public String name;
    public String previewImageName;

    public ColorScheme(JSONObject json) {
        this.id = json.optInt("id");
        this.name = json.optString("name");
        this.previewImageName = json.optString("previewImage");
        JSONArray colorSectionDicts = json.optJSONArray("sections");
        for (int i = 0; i < colorSectionDicts.length(); i++) {
            try {
                this.colorSections.add(new ColorSection(colorSectionDicts.getJSONObject(i)));
            } catch (JSONException e) {
                Log.e("-->", "Got error on ColorScheme " + this.name + " initiation: " + e);
            }
        }
    }
}
