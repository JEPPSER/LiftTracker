package com.jesperbergstrom.lifttracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jesperbergstrom.lifttracker.io.FileManager;
import com.jesperbergstrom.lifttracker.model.Lift;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private Button addBtn;
    private LinearLayout liftList;

    private ArrayList<Lift> lifts;
    private FileManager fileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileManager = new FileManager(getFilesDir());

        addBtn = findViewById(R.id.addBtn);
        liftList = findViewById(R.id.liftList);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        loadLifts();

        addBtn.setOnClickListener((view) -> {
            addLiftDialog();
        });
    }

    private void addLiftDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lift name:");

        final EditText input = new EditText(this);
        input.setWidth(400);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        LinearLayout l = new LinearLayout(this);
        l.setPadding(60, 0, 60, 0);
        l.addView(input);

        builder.setView(l);

        builder.setPositiveButton("OK", (dialog, which) -> {
            Lift lift = new Lift(input.getText().toString());
            lifts.add(lift);
            fileManager.updateLiftFiles(lifts);
            loadLifts();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    private void loadLifts() {
        lifts = fileManager.loadLiftFiles();
        liftList.removeAllViews();

        // Add UI elements
        for (Lift l : lifts) {

            // Container for the lift
            LinearLayout hbox = new LinearLayout(this);
            hbox.setOrientation(LinearLayout.HORIZONTAL);
            hbox.setPadding(30, 30, 30, 30);

            // Text
            TextView tv = new TextView(this);
            tv.setText(l.getName());
            tv.setTextSize(30);

            // Border for the container
            GradientDrawable border = new GradientDrawable();
            border.setColor(0xFFFFFFFF);
            border.setStroke(1, 0xFF000000);
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                hbox.setBackgroundDrawable(border);
            } else {
                hbox.setBackground(border);
            }

            hbox.addView(tv);
            liftList.addView(hbox);
        }
    }
}
