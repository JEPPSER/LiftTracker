package com.jesperbergstrom.lifttracker.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jesperbergstrom.lifttracker.R;
import com.jesperbergstrom.lifttracker.io.FileManager;
import com.jesperbergstrom.lifttracker.io.Settings;
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
    private Settings settings;
    private int selectedIndex;
    private String liftName;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        fileManager = new FileManager(getFilesDir());
        settings = new Settings(getFilesDir());

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        selectedIndex = setList.indexOfChild(v);
        getMenuInflater().inflate(R.menu.set_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.removeSet:
                removeDialog();
                return true;
            case R.id.displaySet:
                getWorkout(liftName, date).setDisplayIndex(selectedIndex);
                fileManager.updateAllLiftFiles(lifts);
                loadSets();
                return true;
            case R.id.dupSet:
                duplicateSet(selectedIndex);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void duplicateSet(int index) {
        Workout w = getWorkout(liftName, date);
        Set s = w.getSets().get(index);
        Set nSet = new Set(s.getWeight(), s.getReps());
        w.getSets().add(nSet);
        fileManager.updateAllLiftFiles(lifts);
        loadSets();
    }

    private void removeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning!");
        TextView text = new TextView(this);
        text.setText("Do you want to remove this set?");

        LinearLayout l = new LinearLayout(this);
        l.setPadding(60, 60, 60, 0);
        l.addView(text);

        builder.setView(l);

        builder.setPositiveButton("YES", (dialog, which) -> {
            Workout w = getWorkout(liftName, date);
            w.getSets().remove(selectedIndex);
            w.setDisplayIndex(-1);
            fileManager.updateAllLiftFiles(lifts);
            loadSets();
        });

        builder.setNegativeButton("NO", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    private void addSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        EditText weight = new EditText(this);
        weight.setWidth(400);
        weight.setInputType(InputType.TYPE_CLASS_NUMBER);
        KeyListener keyListener = DigitsKeyListener.getInstance("0123456789.");
        weight.setKeyListener(keyListener);
        TextView weightText = new TextView(this);
        weightText.setText("Weight: ");
        LinearLayout l1 = new LinearLayout(this);
        l1.setPadding(60, 40, 60, 0);
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
            if (weight.getText().toString().isEmpty() || reps.getText().toString().isEmpty()) {
                dialog.cancel();
            } else {
                Set set = new Set(Float.parseFloat(weight.getText().toString()), Integer.parseInt(reps.getText().toString()));
                getWorkout(liftName, date).getSets().add(set);
                fileManager.updateAllLiftFiles(lifts);
                loadSets();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    private void loadSets() {
        lifts = fileManager.loadAllLiftFiles();

        Workout w = getWorkout(liftName, date);
        ArrayList<Set> sets = w.getSets();
        setList.removeAllViews();

        int i = 0;

        for (Set s : sets) {
            // Container for the workout
            LinearLayout hbox = new LinearLayout(this);
            hbox.setOrientation(LinearLayout.HORIZONTAL);
            hbox.setPadding(30, 30, 30, 30);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 4);
            hbox.setLayoutParams(params);
            hbox.setBackgroundColor(Color.parseColor("#202020"));

            // Text
            TextView tv = new TextView(this);
            String text = s.getWeight() + " " + settings.getWeightUnit() + " x " + s.getReps();
            if (i == w.getDisplayIndex()) {
                text += " (display)";
            }
            tv.setText(text);
            tv.setTextSize(30);
            tv.setTextColor(Color.WHITE);

            this.registerForContextMenu(hbox);

            hbox.addView(tv);
            setList.addView(hbox);
            i++;
        }
    }

    private Workout getWorkout(String liftName, String date) {
        Lift lift = getLift(liftName);
        for (Workout w : lift.getWorkouts()) {
            if (w.getDate().toString().equals(date)) {
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
