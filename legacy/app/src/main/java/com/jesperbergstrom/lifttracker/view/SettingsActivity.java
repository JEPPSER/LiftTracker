package com.jesperbergstrom.lifttracker.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.jesperbergstrom.lifttracker.R;
import com.jesperbergstrom.lifttracker.io.FileManager;
import com.jesperbergstrom.lifttracker.io.Settings;
import com.jesperbergstrom.lifttracker.model.Lift;
import com.jesperbergstrom.lifttracker.model.Set;
import com.jesperbergstrom.lifttracker.model.Workout;

import java.util.ArrayList;

public class SettingsActivity extends Activity {

    private RadioButton radioKg;
    private RadioButton radioLbs;
    private Button kgButton;
    private Button lbsButton;

    private Settings settings;
    private FileManager fileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        radioKg = findViewById(R.id.radioKg);
        radioLbs = findViewById(R.id.radioLbs);
        kgButton = findViewById(R.id.kgButton);
        lbsButton = findViewById(R.id.lbsButton);

        settings = new Settings(getFilesDir());
        fileManager = new FileManager(getFilesDir());

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

        kgButton.setOnClickListener(e -> {
            dialog(0.45359237, "lbs", "kg");
        });

        lbsButton.setOnClickListener(e -> {
            dialog(2.20462262, "kg", "lbs");
        });
    }

    private void dialog(double val, String from, String to) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning!");
        TextView text = new TextView(this);
        text.setText("Do you want to convert all weights from " + from + " to " + to + "?");

        LinearLayout l = new LinearLayout(this);
        l.setPadding(60, 60, 60, 0);
        l.addView(text);

        builder.setView(l);

        builder.setPositiveButton("YES", (dialog, which) -> {
            convert(val);
        });

        builder.setNegativeButton("NO", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    private void convert(double val) {
        ArrayList<Lift> lifts = fileManager.loadAllLiftFiles();

        for (Lift l : lifts) {
            for (Workout w : l.getWorkouts()) {
                for (Set s : w.getSets()) {
                    float weight = (float) Math.round(s.getWeight() * val * 10) / 10;
                    s.setWeight(weight);
                }
            }
        }

        fileManager.updateAllLiftFiles(lifts);
    }
}
