package com.zhy.sample_circlemenu.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;


import com.zhy.sample_circlemenu.R;
import com.zhy.sample_circlemenu.models.ColorScheme;
import com.zhy.sample_circlemenu.models.WheelTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class DataProvider {
    public static final String HIDDEN_SECTIONS_KEY = "hiddenSections";
    public static final String PREFS_NAME = "DecideNowPrefs";
    public static final String SELECTED_TEMPLATE_KEY = "selectedTemplate";
    public static final String TEMPLATE_ORDER = "templateOrder";
    private static DataProvider ourInstance = new DataProvider();
    private ArrayList<ColorScheme> colorSchemes;
    private Context context;
    private WheelTemplate selectedTemplate;
    private ArrayList<WheelTemplate> wheelTemplates;

    public static DataProvider getInstance() {
        return ourInstance;
    }

    private DataProvider() {
    }

    public DataProvider init(Context c) {
        this.context = c;
        return this;
    }

    public ArrayList<WheelTemplate> getWheelTemplates() {
        if (this.wheelTemplates == null) {
            initWheelTemplates();
        }
        return this.wheelTemplates;
    }

    public ArrayList<ColorScheme> getColorSchemes() {
        if (this.colorSchemes == null) {
            initColorSchemes();
        }
        return this.colorSchemes;
    }

    public void saveTemplateToFile(WheelTemplate template) {
        try {
            FileOutputStream fos = this.context.openFileOutput(template.wid + ".dnwheel", 0);
            fos.write(template.toJSON().toString().getBytes());
            fos.close();
        } catch (Exception e) {
            Log.d("-->", "Cannot save template " + template.wid + ". Error: " + e.toString());
        }
    }

    public WheelTemplate getSelectedTemplate() {
        if (this.selectedTemplate == null) {
            String templateId = this.context.getSharedPreferences("DecideNowPrefs", 0).getString(SELECTED_TEMPLATE_KEY, "");
            Log.d("-->", "Saved templateId is " + templateId);
            if (templateId == "") {
                setSelectedTemplate((WheelTemplate) getWheelTemplates().get(0));
            } else {
                this.selectedTemplate = getWheelTemplateById(templateId);
                if (this.selectedTemplate == null) {
                    setSelectedTemplate((WheelTemplate) getWheelTemplates().get(0));
                }
            }
        }
        return this.selectedTemplate;
    }

    public void setSelectedTemplate(WheelTemplate template) {
        if (this.selectedTemplate != template) {
            resetHiddenLabels();
        }
        this.selectedTemplate = template;
        Editor editor = this.context.getSharedPreferences("DecideNowPrefs", 0).edit();
        editor.putString(SELECTED_TEMPLATE_KEY, template.wid);
        editor.apply();
    }

    public void resetHiddenLabels() {
        Editor editor = this.context.getSharedPreferences("DecideNowPrefs", 0).edit();
        editor.remove(HIDDEN_SECTIONS_KEY);
        editor.apply();
    }

    public WheelTemplate getWheelTemplateById(String wid) {
        ArrayList<WheelTemplate> templates = getWheelTemplates();
        for (int i = 0; i < templates.size(); i++) {
            if (((WheelTemplate) templates.get(i)).wid.equals(wid)) {
                return (WheelTemplate) templates.get(i);
            }
        }
        return null;
    }

    public ColorScheme getColorSchemeById(int id) {
        Log.d("-->", "getColorSchemeById() called with: id = [" + id + "]");
        ArrayList<ColorScheme> schemes = getColorSchemes();
        Log.d("-->", "Found schemes " + schemes.toString());
        for (int i = 0; i < schemes.size(); i++) {
            if (((ColorScheme) schemes.get(i)).id == id) {
                return (ColorScheme) schemes.get(i);
            }
        }
        return null;
    }

    public boolean removeTemplate(WheelTemplate template) {
        if (this.wheelTemplates.size() == 1) {
            return false;
        }
        boolean deleted = new File(this.context.getFilesDir(), template.wid + ".dnwheel").delete();
        if (!deleted) {
            return deleted;
        }
        this.wheelTemplates.remove(template);
        if (template != getSelectedTemplate()) {
            return deleted;
        }
        setSelectedTemplate((WheelTemplate) this.wheelTemplates.get(0));
        return deleted;
    }

    public void addTemplate(WheelTemplate template) {
        saveTemplateToFile(template);
        if (getWheelTemplateById(template.wid) == null) {
            this.wheelTemplates.add(0, template);
        }
    }

    public int getTemplatePosition(WheelTemplate template) {
        return getWheelTemplates().indexOf(template);
    }

    private void initWheelTemplates() {
        String[] files = this.context.fileList();
        if (files.length > 0) {
            this.wheelTemplates = new ArrayList();
            for (String fileName : files) {
                if (isWheelFile(fileName)) {
                    try {
                        this.wheelTemplates.add(new WheelTemplate(new JSONObject(readFile(fileName))));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (this.wheelTemplates.size() == 0) {
                seedWheelTemplates();
            }
        } else {
            seedWheelTemplates();
        }
        applyTemplatesOrder();
    }

    private void seedWheelTemplates() {
        InputStream inputStream = this.context.getResources().openRawResource(R.raw.templates);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            for (int ctr = inputStream.read(); ctr != -1; ctr = inputStream.read()) {
                byteArrayOutputStream.write(ctr);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONArray jsonArray = new JSONArray(byteArrayOutputStream.toString());
            this.wheelTemplates = new ArrayList();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject wheelObj = jsonArray.getJSONObject(i);
                wheelObj.put("pd", 1);
                WheelTemplate template = new WheelTemplate(wheelObj);
                this.wheelTemplates.add(template);
                saveTemplateToFile(template);
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private String readFile(String filename) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.context.openFileInput(filename), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    return sb.toString();
                }
                sb.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            return "";
        } catch (UnsupportedEncodingException e2) {
            return "";
        } catch (IOException e3) {
            return "";
        }
    }

    private void initColorSchemes() {
        InputStream inputStream = this.context.getResources().openRawResource(R.raw.color_schemes);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            for (int ctr = inputStream.read(); ctr != -1; ctr = inputStream.read()) {
                byteArrayOutputStream.write(ctr);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONArray jsonArray = new JSONArray(byteArrayOutputStream.toString());
            this.colorSchemes = new ArrayList();
            for (int i = 0; i < jsonArray.length(); i++) {
                this.colorSchemes.add(new ColorScheme(jsonArray.getJSONObject(i)));
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void reorder(int fromPosition, int toPosition) {
        ArrayList<WheelTemplate> tmpls = getWheelTemplates();
        if (fromPosition < tmpls.size() && toPosition < tmpls.size()) {
            WheelTemplate template = (WheelTemplate) tmpls.get(fromPosition);
            tmpls.remove(fromPosition);
            tmpls.add(toPosition, template);
            storeTemplateOrder();
        }
    }

    protected void storeTemplateOrder() {
        SharedPreferences prefs = this.context.getSharedPreferences("DecideNowPrefs", 0);
        ArrayList<WheelTemplate> templates = getWheelTemplates();
        ArrayList<String> newWids = new ArrayList();
        Iterator it = templates.iterator();
        while (it.hasNext()) {
            newWids.add(((WheelTemplate) it.next()).wid);
        }
        Editor editor = prefs.edit();
        try {
            editor.putString(TEMPLATE_ORDER, ObjectSerializer.serialize(newWids));
            editor.apply();
            Log.d("-->", "storeTemplateOrder: " + newWids);
        } catch (IOException e) {
            Log.e("ERROR", "applyTemplatesOrder: ERROR " + e.toString());
            e.printStackTrace();
        }
    }

    protected void applyTemplatesOrder() {
        try {
            final ArrayList<String> wids = (ArrayList) ObjectSerializer.deserialize(this.context.getSharedPreferences("DecideNowPrefs", 0).getString(TEMPLATE_ORDER, ObjectSerializer.serialize(new ArrayList())));
            Log.d("-->", "applyTemplatesOrder: " + wids);
            Collections.sort(getWheelTemplates(), new Comparator<WheelTemplate>() {
                public int compare(WheelTemplate lhs, WheelTemplate rhs) {
                    return Math.max(wids.indexOf(lhs.wid), 0) - Math.max(wids.indexOf(rhs.wid), 0);
                }
            });
            storeTemplateOrder();
        } catch (IOException e) {
            Log.e("ERROR", "applyTemplatesOrder: ERROR " + e.toString());
            e.printStackTrace();
        }
    }

    public int getOwnTemplatesCount() {
        int own = 0;
        Iterator it = getWheelTemplates().iterator();
        while (it.hasNext()) {
            if (!((WheelTemplate) it.next()).isPredefined().booleanValue()) {
                own++;
            }
        }
        return own;
    }

    private boolean isWheelFile(String filename) {
        return filename.endsWith(".dnwheel");
    }
}
