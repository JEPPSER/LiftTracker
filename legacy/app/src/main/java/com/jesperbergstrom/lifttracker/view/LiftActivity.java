package com.jesperbergstrom.lifttracker.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.edmodo.rangebar.RangeBar;
import com.google.android.material.tabs.TabLayout;
import com.jesperbergstrom.lifttracker.R;
import com.jesperbergstrom.lifttracker.io.FileManager;
import com.jesperbergstrom.lifttracker.io.Settings;
import com.jesperbergstrom.lifttracker.model.Date;
import com.jesperbergstrom.lifttracker.model.Lift;
import com.jesperbergstrom.lifttracker.model.Set;
import com.jesperbergstrom.lifttracker.model.Workout;
import com.jesperbergstrom.lifttracker.view.fragment.LiftFragmentAdapter;
import com.jesperbergstrom.lifttracker.view.graphs.PlotPoint;
import com.jesperbergstrom.lifttracker.view.graphs.ScatterPlotView;

import java.util.ArrayList;
import java.util.Collections;

public class LiftActivity extends AppCompatActivity {

    private ScatterPlotView scatterPlot;
    private Spinner spinner;
    private RangeBar rangeBar;
    private TextView dateText;
    private CheckBox checkBox;
    private CheckBox propCheckBox;

    private LinearLayout workoutList;
    private Button addWorkoutButton;
    private TextView liftText;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ArrayList<Lift> lifts;
    private FileManager fileManager;
    private Settings settings;
    private int selectedIndex;
    private String liftName;
    private int fromIndex;
    private int toIndex;
    private int metric = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_lift);

        fileManager = new FileManager(getFilesDir());
        settings = new Settings(getFilesDir());

        addWorkoutButton = findViewById(R.id.addWorkoutBtn);
        liftText = findViewById(R.id.liftText);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Workouts"));
        tabLayout.addTab(tabLayout.newTab().setText("Stats"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final LiftFragmentAdapter adapter = new LiftFragmentAdapter(this, this.getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    workoutList = findViewById(R.id.workoutList);
                    loadWorkouts();
                } else if (tab.getPosition() == 1) {
                    scatterPlot = findViewById(R.id.scatterPlot);
                    spinner = findViewById(R.id.spinner);
                    rangeBar = findViewById(R.id.rangeBar);
                    dateText = findViewById(R.id.dateText);
                    checkBox = findViewById(R.id.checkBox);
                    propCheckBox = findViewById(R.id.propCheckBox);

                    checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
                        scatterPlot.setDrawLine(b);
                        scatterPlot.invalidate();
                    });

                    propCheckBox.setOnCheckedChangeListener(((compoundButton, b) -> {
                        scatterPlot.setPropDates(b);
                        scatterPlot.invalidate();
                    }));

                    lifts = fileManager.loadAllLiftFiles();
                    initSpinner();
                    initRangeBar();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        Intent intent = getIntent();
        liftName = intent.getStringExtra("name");
        liftText.setText(liftName);
        lifts = fileManager.loadAllLiftFiles();
        fromIndex = 0;
        toIndex = getLift(liftName).getWorkouts().size() - 1;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        workoutList = findViewById(R.id.workoutList);
        loadWorkouts();

        addWorkoutButton.setOnClickListener((view) -> {
            addWorkoutDialog();
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        selectedIndex = workoutList.indexOfChild(v);
        getMenuInflater().inflate(R.menu.workout_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.removeWorkout:
                removeDialog();
                return true;
            case R.id.changeWorkout:
                changeDialog();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void changeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        DatePicker datePicker = new DatePicker(this);

        builder.setView(datePicker);

        builder.setPositiveButton("OK", (dialog, which) -> {
            Lift lift = getLift(liftName);
            Workout w = lift.getWorkouts().get(selectedIndex);
            w.setDate(new Date(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth()));
            Collections.sort(lift.getWorkouts());
            fileManager.updateAllLiftFiles(lifts);
            loadWorkouts();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    private void removeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning!");
        TextView text = new TextView(this);
        text.setText("Do you want to remove this workout?");

        LinearLayout l = new LinearLayout(this);
        l.setPadding(60, 60, 60, 0);
        l.addView(text);

        builder.setView(l);

        builder.setPositiveButton("YES", (dialog, which) -> {
            getLift(liftName).getWorkouts().remove(selectedIndex);
            fileManager.updateAllLiftFiles(lifts);
            loadWorkouts();
        });

        builder.setNegativeButton("NO", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    private void addWorkoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        DatePicker datePicker = new DatePicker(this);

        builder.setView(datePicker);

        builder.setPositiveButton("OK", (dialog, which) -> {
            Lift lift = getLift(liftName);
            Date date = new Date(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
            if (lift.containsDate(date)) {
                dialog.cancel();
            } else {
                Workout w = new Workout();
                w.setDate(date);
                lift.getWorkouts().add(w);
                Collections.sort(lift.getWorkouts());
                fileManager.updateAllLiftFiles(lifts);
                loadWorkouts();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });

        builder.show();
    }

    private void initRangeBar() {
        Lift lift = getLift(liftName);

        if (lift.getWorkouts().size() < 2) {
            rangeBar.setTickCount(2);
        } else {
            rangeBar.setTickCount(lift.getWorkouts().size());
        }

        changeRange(fromIndex, toIndex);

        rangeBar.setOnRangeBarChangeListener((rangeBar, from, to) -> {
            changeRange(from, to);
        });
    }

    private void changeRange(int from, int to) {
        Lift lift = getLift(liftName);
        if (lift.getWorkouts().size() == 1) {
            from = 0;
            to = 0;
        } else if (lift.getWorkouts().size() == 0) {
            return;
        }
        Workout wFrom = lift.getWorkouts().get(from);
        Workout wTo = lift.getWorkouts().get(to);
        fromIndex = from;
        toIndex = to;
        dateText.setText(wFrom.getDate().toString() + "  to  " + wTo.getDate().toString());
        loadGraphs(metric);
        scatterPlot.invalidate();
    }

    private void initSpinner() {
        String[] items = { "Volume", "Max weight", "Max reps", "Total sets", "Total reps" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, items);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                metric = i;
                loadGraphs(i);
                scatterPlot.invalidate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void loadGraphs(int metric) {
        if (scatterPlot == null) {
            return;
        }

        lifts = fileManager.loadAllLiftFiles();

        scatterPlot.getData().clear();

        int i = 0;

        for (Workout w : getLift(liftName).getWorkouts()) {

            if (i < fromIndex || i > toIndex) {
                i++;
                continue;
            }

            double val = 0;

            switch (metric) {
                case 0:
                    for (Set s : w.getSets()) {
                        val += (s.getReps() * s.getWeight());
                    }
                    break;
                case 1:
                    for (Set s : w.getSets()) {
                        if (val < s.getWeight()) {
                            val = s.getWeight();
                        }
                    }
                    break;
                case 2:
                    for (Set s : w.getSets()) {
                        if (val < s.getReps()) {
                            val = s.getReps();
                        }
                    }
                    break;
                case 3:
                    val = w.getSets().size();
                    break;
                case 4:
                    for (Set s : w.getSets()) {
                        val += s.getReps();
                    }
                    break;
                default:
                    break;
            }

            PlotPoint p = new PlotPoint(w.getDate(), val);
            scatterPlot.getData().add(p);
            i++;
        }

        scatterPlot.updatePlot();
    }

    private void loadWorkouts() {
        if (workoutList == null) {
            return;
        }

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
            hbox.setBackgroundColor(Color.parseColor("#202020"));

            // Generate "thumbnail" string
            String thumbnail = "";
            if (w.getSets().size() > 0 && w.getDisplayIndex() == -1) {
                Set show = w.getSets().get(0);
                for (Set s : w.getSets()) {
                    if (s.getWeight() > show.getWeight()) {
                        show = s;
                    } else if (s.getWeight() == show.getWeight() && s.getReps() > show.getReps()) {
                        show = s;
                    }
                }
                thumbnail = "(" + show.getWeight() + " " + settings.getWeightUnit() + " x " + show.getReps() + ")";
            } else if (w.getDisplayIndex() != -1) {
                Set show = w.getSets().get(w.getDisplayIndex());
                thumbnail = "(" + show.getWeight() + " " + settings.getWeightUnit() + " x " + show.getReps() + ")";
            }

            // Text
            TextView tv = new TextView(this);
            tv.setText(w.getDate() + ": " + thumbnail);
            tv.setTextSize(20);
            tv.setTextColor(Color.WHITE);

            this.registerForContextMenu(hbox);

            hbox.setOnClickListener((view) -> {
                Intent intent = new Intent(this, WorkoutActivity.class);
                intent.putExtra("name", liftName);
                intent.putExtra("date", w.getDate().toString());
                startActivity(intent);
            });

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
