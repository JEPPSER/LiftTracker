package com.jesperbergstrom.lifttracker.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.jesperbergstrom.lifttracker.R;
import com.jesperbergstrom.lifttracker.io.Settings;

public class SettingsActivity extends Activity {

    private RadioButton radioKg;
    private RadioButton radioLbs;
    private Button kgButton;
    private Button lbsButton;

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        radioKg = findViewById(R.id.radioKg);
        radioLbs = findViewById(R.id.radioLbs);
        kgButton = findViewById(R.id.kgButton);
        lbsButton = findViewById(R.id.lbsButton);

        settings = new Settings(getFilesDir());
        if (settings.getWeightUnit().equals("kg")) {
            radioKg.setChecked(true);
        } else {
            radioLbs.setChecked(true);
        }

        radioKg.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                settings.setWeightUnit("kg");
            } else {
                settings.setWeightUnit("lbs");
            }
        });
    }
}
