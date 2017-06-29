package com.zhy.sample_circlemenu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import com.zhy.sample_circlemenu.helpers.DataProvider;
import com.zhy.sample_circlemenu.helpers.ObjectSerializer;
import com.zhy.sample_circlemenu.helpers.SoundManager;
import com.zhy.sample_circlemenu.models.Wheel;
import com.zhy.sample_circlemenu.models.WheelSection;
import com.zhy.sample_circlemenu.models.WheelTemplate;
import com.zhy.sample_circlemenu.views.DisplayView;
import com.zhy.sample_circlemenu.views.RotationTrackingView;
import com.zhy.sample_circlemenu.views.RotationTrackingViewListener;
import com.zhy.sample_circlemenu.views.WheelSectionsView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WheelActivity extends AppCompatActivity implements RotationTrackingViewListener {
    protected final int TEMPLATES_ACTIVITY_REQUEST = 1;
    protected Wheel currentWheel;
    protected DataProvider dataProvider;
    protected DisplayView displayView;
//    protected ImageButton labelOptionsButton;
    protected WheelSection lastSection;
    protected RotationTrackingView rotationTrackingView;
    protected String selectedLabel;
    protected int selectionSoundId;
    protected SoundManager soundManager;
    protected TextView templateNameLabel;
    protected int wheelClickSoundId;
    protected View wheelGroupView;
    protected boolean wheelIsSelected;
    protected WheelSectionsView wheelSectionsView;
    protected WheelSectionsView wheelSelectedSectionsView;

    class C02171 implements OnClickListener {
        C02171() {
        }

        public void onClick(View v) {
            Log.i("helo","koo");
        }
    }

    class C02182 implements OnClickListener {
        C02182() {
        }

        public void onClick(View v) {
            String textToShare;
            WheelActivity.this.rotationTrackingView.stop();
            if (WheelActivity.this.selectedLabel != null) {
                textToShare = WheelActivity.this.getString(R.string.share_text_selected) + "\n" + WheelActivity.this.currentWheel.template.name + ": " + WheelActivity.this.selectedLabel;
            } else {
                textToShare = WheelActivity.this.getString(R.string.share_text_unselected) + " " + WheelActivity.this.currentWheel.template.name;
            }
            WheelActivity.this.shareBitmap(WheelActivity.this.getBitmapFromView(WheelActivity.this.findViewById(R.id.wheelActivityLayout)), "DecideNow", WheelActivity.this.getString(R.string.decide_now_mail_subject), textToShare);
        }
    }

    class C02193 implements OnClickListener {
        C02193() {
        }

        public void onClick(View v) {
            WheelActivity.this.onLabelOptionsButtonTapped(v);
        }
    }

    class C02204 implements OnClickListener {
        C02204() {
        }

        public void onClick(View v) {
            WheelActivity.this.unselect();
            WheelActivity.this.rotationTrackingView.kickRotatableView();
        }
    }

    class C03296 implements OnMenuItemClickListener {
        C03296() {
        }

        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.deactivateLabel:
                    WheelActivity.this.deactivateCurrentLabel();
                    return true;
                case R.id.resetLabels:
                    WheelActivity.this.resetLabels();
                    return true;
                default:
                    return false;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.rwq);
        this.dataProvider = DataProvider.getInstance().init(this);
        this.soundManager = SoundManager.getInstance().init(this);
        this.wheelGroupView = findViewById(R.id.wheelGroupView);
        this.wheelSectionsView = (WheelSectionsView) findViewById(R.id.sectionsView);
        this.wheelSelectedSectionsView = (WheelSectionsView) findViewById(R.id.selectedSectionsView);
        this.rotationTrackingView = (RotationTrackingView) findViewById(R.id.rotationTrackingView);
        this.rotationTrackingView.setRotatableView(this.wheelGroupView);
        this.rotationTrackingView.addEventListener(this);
        this.displayView = (DisplayView) findViewById(R.id.displayView);
        this.templateNameLabel = (TextView) findViewById(R.id.templateNameLabel);
//        ((ImageButton) findViewById(R.id.templatesButton)).setOnClickListener(new C02171());
        ((ImageButton) findViewById(R.id.shareButton)).setOnClickListener(new C02182());
//        this.labelOptionsButton = (ImageButton) findViewById(R.id.labelOptionsButton);
//        this.labelOptionsButton.setOnClickListener(new C02193());
        ((Button) findViewById(R.id.kickButton)).setOnClickListener(new C02204());
        if (VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ViewCompat.MEASURED_STATE_MASK);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    protected void onResume() {
        super.onResume();
        this.wheelClickSoundId = this.soundManager.load(this, R.raw.wheel_sound, 1);
        this.selectionSoundId = this.soundManager.load(this, R.raw.selection_sound, 1);
        updateView();
    }

    public void onPause() {
        super.onPause();
        this.rotationTrackingView.stop();
        this.soundManager.unload(this.wheelClickSoundId);
        this.soundManager.unload(this.selectionSoundId);
    }

    private void updateView() {
        boolean z;
        Log.d("WheelActivity", "updateView()");
        WheelTemplate template = DataProvider.getInstance().getSelectedTemplate();
        Wheel wheel = new Wheel(template, DataProvider.getInstance().getColorSchemeById(template.colorSchemeId));
        Iterator it = getHiddenSectionIndexes().iterator();
        while (it.hasNext()) {
            int index = ((Integer) it.next()).intValue();
            if (index < wheel.sections.size()) {
                ((WheelSection) wheel.sections.get(index)).enabled = Boolean.valueOf(false);
            }
        }
        this.wheelSectionsView.setWheel(wheel);
        this.wheelSelectedSectionsView.setWheel(wheel);
        this.currentWheel = wheel;
        this.displayView.showDefaultText();
        this.templateNameLabel.setText(template.name);
        unselect();
//        ImageButton imageButton = this.labelOptionsButton;
        if (canHideLabel().booleanValue() || canResetLabels().booleanValue()) {
            z = true;
        } else {
            z = false;
        }
//        imageButton.setEnabled(z);
    }

    private void selectWheelAtAngle(float angle) {
        if (!this.wheelIsSelected) {
            this.wheelSelectedSectionsView.selectAtAngle(angle).setVisibility(View.VISIBLE);
            this.wheelSectionsView.setAlpha(0.3f);
            this.wheelIsSelected = true;
        }
    }

    private void unselect() {
        this.wheelSelectedSectionsView.setVisibility(View.INVISIBLE);
        this.wheelSectionsView.setAlpha(1.0f);
        this.selectedLabel = null;
        this.wheelIsSelected = false;
    }

    private WheelSection getCurrentSection() {
        return this.currentWheel.getSectionAtAngle(this.rotationTrackingView.getCurrentAngle());
    }

    private void handleWheelSelection(float angle) {
        WheelSection section = getCurrentSection();
        if (section.enabled.booleanValue()) {
            this.selectedLabel = section.label;
            selectWheelAtAngle(angle);
            this.soundManager.play(this.selectionSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
            return;
        }
        Log.d("-->", "handleWheelSelection: currentAngle " + this.rotationTrackingView.getCurrentAngle());
        final float nextAngle = this.currentWheel.angleForNearestEnabledSectionAtAngle(this.rotationTrackingView.getCurrentAngle());
        WheelSection nextSection = this.currentWheel.getSectionAtAngle(nextAngle);
        this.selectedLabel = nextSection.label;
        this.displayView.setText(nextSection.label);
        this.rotationTrackingView.setEnabled(false);
        Log.d("-->", "handleWheelSelection: nextAngle " + nextAngle);
        this.rotationTrackingView.setCurrentAngle(nextAngle, true, new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                WheelActivity.this.selectWheelAtAngle(nextAngle);
                WheelActivity.this.rotationTrackingView.setEnabled(true);
                WheelActivity.this.soundManager.play(WheelActivity.this.selectionSoundId, 1.0f, 1.0f, 1, 0, 1.0f);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void onLabelOptionsButtonTapped(View v) {
        this.rotationTrackingView.stop();
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.label_options, popup.getMenu());
        popup.setOnMenuItemClickListener(new C03296());
        popup.getMenu().getItem(0).setEnabled(canHideLabel().booleanValue());
        popup.getMenu().getItem(1).setEnabled(canResetLabels().booleanValue());
        popup.show();
    }

    private void deactivateCurrentLabel() {
        if (this.currentWheel.template.labels.size() > 2) {
            WheelSection section = getCurrentSection();
            Set<Integer> sectionIndexes = new HashSet();
            for (int i = 0; i < this.currentWheel.sections.size(); i++) {
                if (((WheelSection) this.currentWheel.sections.get(i)).indexInTemplate == section.indexInTemplate) {
                    sectionIndexes.add(Integer.valueOf(i));
                }
            }
            HashSet<Integer> hiddenSections = getHiddenSectionIndexes();
            hiddenSections.addAll(sectionIndexes);
            setHiddenSectionIndexes(hiddenSections);
            updateView();
        }
    }

    private void resetLabels() {
        DataProvider.getInstance().resetHiddenLabels();
        updateView();
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(-1);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    private void shareBitmap(Bitmap bitmap, String fileName, String subject, String message) {
        try {
            File file = new File(getApplicationContext().getExternalCacheDir(), fileName + ".png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setFlags(268435456);
            intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
            intent.putExtra("android.intent.extra.TEXT", message);
            intent.putExtra("android.intent.extra.SUBJECT", subject);
            intent.setType("*/*");
            startActivity(Intent.createChooser(intent, getResources().getText(R.string.share)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Boolean canHideLabel() {
        boolean z = false;
        if (!getCurrentSection().enabled.booleanValue()) {
            return Boolean.valueOf(false);
        }
        ArrayList<Integer> enabledLabelIndexes = new ArrayList();
        Iterator it = this.currentWheel.sections.iterator();
        while (it.hasNext()) {
            WheelSection section = (WheelSection) it.next();
            if (section.enabled.booleanValue() && !enabledLabelIndexes.contains(Integer.valueOf(section.indexInTemplate))) {
                enabledLabelIndexes.add(Integer.valueOf(section.indexInTemplate));
            }
        }
        if (enabledLabelIndexes.size() > 2) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    private Boolean canResetLabels() {
        return Boolean.valueOf(getHiddenSectionIndexes().size() > 0);
    }

    private HashSet<Integer> getHiddenSectionIndexes() {
        SharedPreferences prefs = getSharedPreferences("DecideNowPrefs", 0);
        HashSet<Integer> hiddenSections = new HashSet();
        try {
            return (HashSet) ObjectSerializer.deserialize(prefs.getString(DataProvider.HIDDEN_SECTIONS_KEY, ObjectSerializer.serialize(new HashSet())));
        } catch (IOException e) {
            e.printStackTrace();
            return hiddenSections;
        }
    }

    private void setHiddenSectionIndexes(HashSet<Integer> hiddenSections) {
        Editor editor = getSharedPreferences("DecideNowPrefs", 0).edit();
        try {
            editor.putString(DataProvider.HIDDEN_SECTIONS_KEY, ObjectSerializer.serialize(hiddenSections));
            editor.apply();
        } catch (IOException e) {
            Log.e("ERROR", "setHiddenSectionIndexes: ERROR " + e.toString());
            e.printStackTrace();
        }
    }

    public void onWheelTouchBegan() {
        unselect();
    }

    public void onWheelStopped(float angle) {
        Log.d("-->", "onWheelStopped() called with: angle = [" + angle + "]");
        handleWheelSelection(angle);
    }

    public void onWheelAngleDidChanged(float angle) {
        WheelSection section = this.currentWheel.getSectionAtAngle(angle);
        if (section != this.lastSection) {
            this.soundManager.play(this.wheelClickSoundId, 0.5f, 0.5f, 1, 0, 1.0f);
            this.displayView.setText(section.label);
            this.lastSection = section;
        }
    }

    public void onWheelStartDescending() {
    }

    public void onWheelStopDescending() {
    }
}
