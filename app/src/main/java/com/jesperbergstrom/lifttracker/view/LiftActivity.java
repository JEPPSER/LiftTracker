package com.jesperbergstrom.lifttracker.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jesperbergstrom.lifttracker.R;
import com.jesperbergstrom.lifttracker.io.FileManager;
import com.jesperbergstrom.lifttracker.model.Lift;
import com.jesperbergstrom.lifttracker.model.Workout;

import java.util.ArrayList;

public class LiftActivity extends Activity {

    private LinearLayout workoutList;
    private Button addWorkoutButton;
    private TextView liftText;

    private ArrayList<Lift> lifts;
    private FileManager fileManager;
    private int selectedIndex;
    private String liftName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lift);

        fileManager = new FileManager(getFilesDir());

        workoutList = findViewById(R.id.workoutList);
        addWorkoutButton = findViewById(R.id.addWorkoutBtn);
        liftText = findViewById(R.id.liftText);

        Intent intent = getIntent();
        liftName = intent.getStringExtra("name");
        liftText.setText(liftName);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        loadWorkouts();

        addWorkoutButton.setOnClickListener((view) -> {
            addWorkoutDialog();
        });
    }

    private void addWorkoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        DatePicker datePicker = new DatePicker(this);

        builder.setView(datePicker);

        builder.setPositiveButton("OK", (dialog, which) -> {
            Workout w = new Workout();
            w.setDate(datePicker.getYear() + "-" + datePicker.getMonth() + "-" + datePicker.getDayOfMonth());
            getLift(liftName).getWorkouts().add(w);
            fileManager.updateAllLiftFiles(lifts);
            loadWorkouts();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    private void loadWorkouts() {
        lifts = fileManager.loadAllLiftFiles();

        ArrayList<Workout> workouts = getLift(liftName).getWorkouts();
        workoutList.removeAllViews();

        for (Workout w : workouts) {
            // Container for the workout
            LinearLayout hbox = new LinearLayout(this);
            hbox.setOrientation(LinearLayout.HORIZONTAL);
            hbox.setPadding(30, 30, 30, 30);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 4);
            hbox.setLayoutParams(params);
            hbox.setBackgroundColor(Color.WHITE);

            // Text
            TextView tv = new TextView(this);
            tv.setText(w.getDate());
            tv.setTextSize(30);

            /*this.registerForContextMenu(hbox);

            hbox.setOnClickListener((view) -> {
                intent.putExtra("name", l.getName());
                startActivity(intent);
            });*/

            hbox.addView(tv);
            workoutList.addView(hbox);
        }
    }

    private Lift getLift(String name) {
        for (Lift lift : lifts) {
            if (lift.getName().equals(name)) {
                return lift;
            }
        }
        return null;
    }
}
