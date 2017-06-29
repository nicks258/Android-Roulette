package com.zhy.sample_circlemenu.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

public class Wheel {
    public ArrayList<WheelSection> sections;
    public WheelTemplate template;

    public Wheel(WheelTemplate aTemplate, ColorScheme colorScheme) {
        this.template = aTemplate;
        int numberOfSections = getNumberOfSections(this.template);
        int numberOfColors = colorScheme.colorSections.size();
        if (numberOfSections > numberOfColors && Math.round(((double) numberOfSections) * 0.5d) < ((long) numberOfColors)) {
            numberOfColors = (int) Math.round(((double) numberOfSections) * 0.5d);
        }
        this.sections = new ArrayList();
        int colorIndex = 0;
        int labelIndex = 0;
        Log.d("-->", "Number of sections " + numberOfSections);
        for (int i = 0; i < numberOfSections; i++) {
            if (colorIndex > numberOfColors - 1) {
                colorIndex = 0;
            }
            if (labelIndex > this.template.labels.size() - 1) {
                labelIndex = 0;
            }
            WheelSection section = new WheelSection();
            section.label = ((WheelTemplateLabel) this.template.labels.get(labelIndex)).name;
            section.indexInTemplate = labelIndex;
            section.colorSection = (ColorSection) colorScheme.colorSections.get(colorIndex);
            section.enabled = Boolean.valueOf(true);
            this.sections.add(section);
            colorIndex++;
            labelIndex++;
        }
    }

    public WheelSection getSectionAtAngle(float angle) {
        if (this.sections.size() <= 0) {
            return null;
        }
        return (WheelSection) this.sections.get((int) Math.floor((double) (angle / (360.0f / ((float) this.sections.size())))));
    }

    public float angleOfSection(WheelSection section) {
        float sectionAngle = 360.0f / ((float) this.sections.size());
        return (((float) this.sections.indexOf(section)) * sectionAngle) + (0.5f * sectionAngle);
    }

    public float angleForNearestEnabledSectionAtAngle(float masterAngle) {
        WheelSection masterSection = getSectionAtAngle(masterAngle);
        WheelSection pre = null;
        Iterator it = this.sections.iterator();
        while (it.hasNext()) {
            WheelSection section = (WheelSection) it.next();
            if (section == masterSection && pre != null) {
                break;
            } else if (section.enabled.booleanValue()) {
                pre = section;
            }
        }
        Boolean found = Boolean.valueOf(false);
        WheelSection post = null;
        it = this.sections.iterator();
        while (it.hasNext()) {
            WheelSection  section = (WheelSection) it.next();
            if (section != masterSection) {
                if (section.enabled.booleanValue() && found.booleanValue()) {
                    post = section;
                    break;
                }
            }
            found = Boolean.valueOf(true);
        }
        if (post == null) {
            it = this.sections.iterator();
            while (it.hasNext()) {
                WheelSection  section = (WheelSection) it.next();
                if (section.enabled.booleanValue()) {
                    post = section;
                    break;
                }
            }
        }
        float preAngle = angleOfSection(pre);
        float postAngle = angleOfSection(post);
        float diffPre = Math.abs(masterAngle - preAngle) % 360.0f;
        float diffPost = Math.abs(postAngle - masterAngle) % 360.0f;
        if (diffPre > 180.0f) {
            diffPre = 360.0f - diffPre;
        }
        if (diffPost > 180.0f) {
            diffPost = 360.0f - diffPost;
        }
        return diffPre < diffPost ? preAngle : postAngle;
    }

    private int getNumberOfSections(WheelTemplate temp) {
        Log.d("-->", "label size " + temp.labels.size());
        int labelCount = temp.labels.size();
        if (labelCount >= 9) {
            return labelCount;
        }
        switch (labelCount) {
            case 1:
            case 2:
            case 8:
                return 8;
            case 3:
            case 4:
            case 6:
                return 12;
            case 5:
                return 10;
            case 7:
                return 14;
            default:
                return labelCount;
        }
    }
}
