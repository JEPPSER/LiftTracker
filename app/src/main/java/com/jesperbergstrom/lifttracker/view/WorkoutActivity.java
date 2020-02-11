package com.jesperbergstrom.lifttracker.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jesperbergstrom.lifttracker.R;
import com.jesperbergstrom.lifttracker.io.FileManager;
import com.jesperbergstrom.lifttracker.model.Lift;
import com.jesperbergstrom.lifttracker.model.Set;
import com.jesperbergstrom.lifttracker.model.Workout;

import java.util.ArrayList;

public class WorkoutActivity extends Activity {

    private LinearLayout setList;
    private Button addSetButton;
    private TextView workoutText;

    private ArrayList<Lift> lifts;
    private FileManager fileManager;
    private int selectedIndex;
    private String liftName;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        fileManager = new FileManager(getFilesDir());

        setList = findViewById(R.id.setList);
        addSetButton = findViewById(R.id.addSetBtn);
        workoutText = findViewById(R.id.workoutText);

        Intent intent = getIntent();
        liftName = intent.getStringExtra("name");
        date = intent.getStringExtra("date");
        workoutText.setText(date + " (" + liftName + ")");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        loadSets();

        addSetButton.setOnClickListener((view) -> {
            addSetDialog();
        });
    }

    private void addSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        EditText weight = new EditText(this);
        weight.setWidth(400);
        weight.setInputType(InputType.TYPE_CLASS_NUMBER);
        TextView weightText = new TextView(this);
        weightText.setText("Weight: ");
        LinearLayout l1 = new LinearLayout(this);
        l1.setPadding(60, 0, 60, 0);
        l1.addView(weightText);
        l1.addView(weight);

        EditText reps = new EditText(this);
        reps.setWidth(400);
        reps.setInputType(InputType.TYPE_CLASS_NUMBER);
        TextView repsText = new TextView(this);
        repsText.setText("Reps:    ");
        LinearLayout l2 = new LinearLayout(this);
        l2.setPadding(60, 0, 60, 0);
        l2.addView(repsText);
        l2.addView(reps);

        LinearLayout vbox = new LinearLayout(this);
        vbox.setOrientation(LinearLayout.VERTICAL);
        vbox.addView(l1);
        vbox.addView(l2);

        builder.setView(vbox);

        builder.setPositiveButton("OK", (dialog, which) -> {
            Set set = new Set(Integer.parseInt(weight.getText().toString()), Integer.parseInt(reps.getText().toString()));
            getWorkout(liftName, date).getSets().add(set);
            fileManager.updateAllLiftFiles(lifts);
            loadSets();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    private void loadSets() {
        lifts = fileManager.loadAllLiftFiles();

        ArrayList<Set> sets = getWorkout(liftName, date).getSets();
        setList.removeAllViews();

        for (Set s : sets) {
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
            tv.setText(s.getWeight() + "x" + s.getReps());
            tv.setTextSize(30);

            //this.registerForContextMenu(hbox);

            /*hbox.setOnClickListener((view) -> {
                Intent intent = new Intent(this, WorkoutActivity.class);
                intent.putExtra("name", liftName);
                intent.putExtra("date", w.getDate());
                startActivity(intent);
            });*/

            hbox.addView(tv);
            setList.addView(hbox);
        }
    }

    private Workout getWorkout(String liftName, String date) {
        Lift lift = getLift(liftName);
        for (Workout w : lift.getWorkouts()) {
            if (w.getDate().equals(date)) {
                return w;
            }
        }
        return null;
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
